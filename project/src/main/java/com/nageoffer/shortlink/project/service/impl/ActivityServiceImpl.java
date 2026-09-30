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
import com.nageoffer.shortlink.project.dto.req.ActivityLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityReqDTO;
import com.nageoffer.shortlink.project.dto.req.ActivityUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ActivityRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.service.IActivityService;
import com.nageoffer.shortlink.project.service.IShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        boolean hasOriginUrl = StrUtil.isNotBlank(reqDTO.getOriginUrl());
        boolean hasStatus = Objects.nonNull(reqDTO.getStatus());
        if (!hasActivityName && !hasOriginUrl && !hasStatus) {
            throw new ClientException("没有需要更新的字段");
        }
        boolean updated = lambdaUpdate()
                .eq(ActivityDO::getId, reqDTO.getId())
                .eq(ActivityDO::getUserName, UserContext.getUserName())
                .eq(ActivityDO::getDelFlag, 0)
                .set(hasActivityName, ActivityDO::getActivityName, reqDTO.getActivityName())
                .set(hasOriginUrl, ActivityDO::getOriginUrl, reqDTO.getOriginUrl())
                .set(hasStatus, ActivityDO::getStatus, reqDTO.getStatus())
                .set(ActivityDO::getUpdateTime, LocalDateTime.now())
                .update();
        if (!updated) {
            throw new ClientException("活动不存在或不属于当前用户");
        }
    }

    @Override
    public void removeActivity(Long id) {
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
        List<ShortLinkCreateRespDTO> results = new ArrayList<>(channels.size());
        for (String channel : channels) {
            ShortLinkReqDTO linkReqDTO = new ShortLinkReqDTO()
                    .setOriginUrl(activityDO.getOriginUrl())
                    .setGid(activityDO.getGid())
                    .setValidDateType(reqDTO.getValidDateType())
                    .setValidDate(reqDTO.getValidDate())
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
