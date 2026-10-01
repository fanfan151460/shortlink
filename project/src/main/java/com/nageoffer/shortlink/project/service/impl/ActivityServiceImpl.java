package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.framework.duplicate.NoDuplicateSubmit;
import com.nageoffer.shortlink.framework.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.framework.exception.ClientException;
import com.nageoffer.shortlink.framework.exception.ServiceException;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.dao.entity.ActivityDO;
import com.nageoffer.shortlink.project.dao.mapper.ActivityMapper;
import com.nageoffer.shortlink.project.dto.req.*;
import com.nageoffer.shortlink.project.dto.resp.ActivityRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.service.IActivityService;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, ActivityDO> implements IActivityService {

    @Value("${spring.short-link.block-domain-list}")
    private String blockDomainList;
    private final IShortLinkService shortLinkService;

    @Override
    public void addNewActivity(ActivityReqDTO activity) {
        String originUrl = activity.getOriginUrl();
        boolean blocked = Arrays.stream(blockDomainList.split(","))
                .anyMatch(keyword -> originUrl.contains(keyword.trim()));
        if (blocked) {
            throw new ClientException("该域名下不允许创办活动！");
        }
        if (Objects.equals(activity.getValidDateType(), 1) && Objects.isNull(activity.getValidDate())) {
            throw new ClientException("请选择活动有效期");
        }
        String userName = UserContext.getUserName();
        ActivityDO activityDO = BeanUtil.copyProperties(activity, ActivityDO.class)
                .setUserName(userName);
        try {
            baseMapper.insert(activityDO);
        } catch (Exception e) {
            throw new ServiceException("创建活动异常", e, BaseErrorCode.SERVICE_ERROR);
        }
    }

    @Override
    public void updateActivity(ActivityUpdateReqDTO reqDTO) {
        boolean hasActivityName = StrUtil.isNotBlank(reqDTO.getActivityName());
        boolean hasStatus = Objects.nonNull(reqDTO.getStatus());
        if (!hasActivityName && !hasStatus) {
            throw new ClientException("没有需要更新的字段");
        }
        // 先取当前行：既做归属校验，也要拿旧状态判断要不要连带启停渠道
        ActivityDO current = lambdaQuery()
                .eq(ActivityDO::getId, reqDTO.getId())
                .eq(ActivityDO::getUserName, UserContext.getUserName())
                .eq(ActivityDO::getDelFlag, 0)
                .one();
        if (Objects.isNull(current)) {
            throw new ClientException("活动不存在或不属于当前用户");
        }
        boolean updated = lambdaUpdate()
                .eq(ActivityDO::getId, reqDTO.getId())
                .eq(ActivityDO::getUserName, UserContext.getUserName())
                .eq(ActivityDO::getDelFlag, 0)
                .set(hasActivityName, ActivityDO::getActivityName, reqDTO.getActivityName())
                .set(hasStatus, ActivityDO::getStatus, reqDTO.getStatus())
                .set(ActivityDO::getUpdateTime, LocalDateTime.now())
                .update();
        if (!updated) {
            throw new ClientException("活动不存在或不属于当前用户");
        }
        if (hasStatus && !Objects.equals(current.getStatus(), reqDTO.getStatus())) {
            if (Objects.equals(reqDTO.getStatus(), 1)) {
                disableActivityChannels(current);
            } else {
                enableActivityChannels(current);
            }
        }
    }

    @Override
    public void removeActivity(Long id) {
        ActivityDO activityDO = lambdaQuery()
                .eq(ActivityDO::getId, id)
                .eq(ActivityDO::getUserName, UserContext.getUserName())
                .eq(ActivityDO::getDelFlag, 0)
                .one();
        if (Objects.isNull(activityDO)) {
            throw new ClientException("活动不存在或不属于当前用户");
        }
        // 先让渠道短链失效，再逻辑删除活动。链接本身不删——分页 SQL 的 a.id IS NULL 会让它们
        // 回落到短链接列表，只是访问返回 404，需要时可以逐条再启用。
        disableActivityChannels(activityDO);
        boolean updated = lambdaUpdate()
                .eq(ActivityDO::getId, id)
                .eq(ActivityDO::getUserName, UserContext.getUserName())
                .eq(ActivityDO::getDelFlag, 0)
                .set(ActivityDO::getDelFlag, 1)
                .set(ActivityDO::getDelTime, System.currentTimeMillis())
                .set(ActivityDO::getUpdateTime, LocalDateTime.now())
                .update();
        if (!updated) {
            throw new ClientException("活动不存在或不属于当前用户");
        }
    }

    /** 关闭活动：渠道短链全部停用。也可以被删除活动复用 */
    private void disableActivityChannels(ActivityDO activityDO) {
        applyChannelEnableStatus(activityDO, 1);
    }

    /** 恢复活动：渠道短链全部启用回来，和 disableActivityChannels 对称 */
    private void enableActivityChannels(ActivityDO activityDO) {
        applyChannelEnableStatus(activityDO, 0);
    }

    /**
     * 按活动批量改渠道短链的 enable_status。
     * @param enableStatus 0 启用 1 停用
     */
    private void applyChannelEnableStatus(ActivityDO activityDO, int enableStatus) {
        if (StrUtil.isBlank(activityDO.getGid())) {
            return;
        }
        ShortLinkBatchStatusReqDTO reqDTO = new ShortLinkBatchStatusReqDTO()
                .setGid(activityDO.getGid())
                .setActivityId(activityDO.getId());
        if (enableStatus == 1) {
            shortLinkService.batchDisableShortLink(reqDTO);
        } else {
            shortLinkService.batchEnableShortLink(reqDTO);
        }
    }

    @Override
    @NoDuplicateSubmit
    @Transactional(rollbackFor = Exception.class)
    public List<ShortLinkCreateRespDTO> addActivityLinks(ActivityLinkCreateReqDTO reqDTO) {
        ActivityDO activityDO = lambdaQuery()
                .eq(ActivityDO::getId, reqDTO.getActivityId())
                .eq(ActivityDO::getUserName, UserContext.getUserName())
                .eq(ActivityDO::getDelFlag, 0)
                .one();
        if (Objects.isNull(activityDO)) {
            throw new ClientException("活动不存在或不属于当前用户");
        }
        List<String> channels = Optional.ofNullable(reqDTO.getChannels()).orElse(List.of())
                .stream()
                .map(StrUtil::trim)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
        if (channels.isEmpty()) {
            throw new ClientException("渠道不能为空");
        }
        // 入参没指定有效期就是"跟随活动"；活动自身也没设过时兜底成永久有效
        Integer validDateType = Objects.isNull(reqDTO.getValidDateType())
                ? Optional.ofNullable(activityDO.getValidDateType()).orElse(0)
                : reqDTO.getValidDateType();
        LocalDate validDate = Objects.equals(validDateType, 1)
                ? (Objects.isNull(reqDTO.getValidDateType()) ? activityDO.getValidDate() : reqDTO.getValidDate())
                : null;
        if (Objects.equals(validDateType, 1) && Objects.isNull(validDate)) {
            throw new ClientException("请选择有效期");
        }
        List<ShortLinkCreateRespDTO> results = new ArrayList<>(channels.size());
        for (String channel : channels) {
            ShortLinkReqDTO linkReqDTO = new ShortLinkReqDTO()
                    .setOriginUrl(activityDO.getOriginUrl())
                    .setGid(activityDO.getGid())
                    .setValidDateType(validDateType)
                    .setValidDate(validDate)
                    .setDescription(reqDTO.getDescription())
                    .setActivityId(activityDO.getId())
                    .setChannel(channel);
            results.add(shortLinkService.createShortLink(linkReqDTO));
        }
        return results;
    }

    @Override
    public List<ActivityRespDTO> pageActivity(ActivityPageReqDTO reqDTO) {
        long current = reqDTO.getCurrent() == null ? 1L : reqDTO.getCurrent();
        long size = reqDTO.getSize() == null ? 10L : reqDTO.getSize();
        LambdaQueryWrapper<ActivityDO> queryWrapper = Wrappers.lambdaQuery(ActivityDO.class)
                .eq(ActivityDO::getUserName, UserContext.getUserName())
                .eq(ActivityDO::getDelFlag, 0)
                .eq(StrUtil.isNotBlank(reqDTO.getGid()), ActivityDO::getGid, reqDTO.getGid())
                .eq(reqDTO.getStatus() != null, ActivityDO::getStatus, reqDTO.getStatus())
                .like(StrUtil.isNotBlank(reqDTO.getActivityName()), ActivityDO::getActivityName, reqDTO.getActivityName())
                .orderByDesc(ActivityDO::getCreateTime);
        Page<ActivityDO> activityPage = baseMapper.selectPage(Page.of(current, size), queryWrapper);
        return BeanUtil.copyToList(activityPage.getRecords(), ActivityRespDTO.class);
    }
}
