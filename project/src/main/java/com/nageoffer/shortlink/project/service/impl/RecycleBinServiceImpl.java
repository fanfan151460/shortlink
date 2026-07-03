package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.project.dto.req.RecycleDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import com.nageoffer.shortlink.project.service.IRecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.nageoffer.shortlink.project.common.constant.RedisConstant.FULL_SHORT_LINK;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements IRecycleBinService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRecycleBin(RecycleDTO recycleDTO) {
        boolean update = lambdaUpdate()
                .eq(ShortLinkDO::getFullShortUrl, recycleDTO.getFullShortUrl())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                .eq(ShortLinkDO::getGid, recycleDTO.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .and(v -> v.isNull(ShortLinkDO::getValidDate)
                        .or().gt(ShortLinkDO::getValidDate, new Date()))
                .set(ShortLinkDO::getDelFlag, 1)
                .update();
        if (!update) {
            throw new ClientException("短链接已经不在了");
        }
        //删除缓存
        stringRedisTemplate.delete(String.format(FULL_SHORT_LINK, recycleDTO.getFullShortUrl()));
    }

    @Override
    public List<ShortLinkRespDTO> pageRecycle(RecyclePageDTO pageReqDTO) {
        Page<ShortLinkDO> linkPage = Page.of(pageReqDTO.getCurrent(), pageReqDTO.getSize());
        //TODO 排序
        Wrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, pageReqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                .eq(ShortLinkDO::getDelFlag, 1);
        Page<ShortLinkDO> shortLinkDOPage = page(linkPage, wrapper);
        return shortLinkDOPage.getRecords()
                .stream().map(each -> BeanUtil.copyProperties(each, ShortLinkRespDTO.class))
                .toList();
    }

    @Override
    public void rmRecycleBin(RecycleDTO recycleDTO) {
        boolean update = lambdaUpdate().eq(ShortLinkDO::getGid, recycleDTO.getGid())
                .eq(ShortLinkDO::getFullShortUrl, recycleDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 1)
                .and(v -> v.isNull(ShortLinkDO::getValidDate)
                        .or().gt(ShortLinkDO::getValidDate, new Date()))
                .set(ShortLinkDO::getDelFlag, 0)
                .update();
        if (!update) {
            throw new ClientException("短链接恢复失败");
        }
    }

    @Override
    public void removeShortLink(RecycleDTO recycleDTO) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, recycleDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                .eq(ShortLinkDO::getFullShortUrl, recycleDTO.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 1);
        remove(queryWrapper);
    }
}