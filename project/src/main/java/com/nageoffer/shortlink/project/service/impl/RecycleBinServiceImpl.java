package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.framework.exception.ClientException;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.req.RecycleDTO;
import com.nageoffer.shortlink.project.dto.req.RecyclePageDTO;
import com.nageoffer.shortlink.project.dto.resp.RecycleBinShortLinkDTO;
import com.nageoffer.shortlink.project.service.IRecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.nageoffer.shortlink.project.common.constant.RedisConstant.FULL_SHORT_LINK;
import static com.nageoffer.shortlink.project.common.constant.RedisConstant.SHORT_URL_NULL_KEY;

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
                .set(ShortLinkDO::getDelTime, System.currentTimeMillis())
                .set(ShortLinkDO::getDelFlag, 1)
                .update();
        if (!update) {
            throw new ClientException("短链接已经不在了");
        }
        //删除缓存
        stringRedisTemplate.delete(String.format(FULL_SHORT_LINK, recycleDTO.getFullShortUrl()));
    }

    @Override
    public boolean saveRecycleBinAll(String gid) {
        Long cnt = lambdaQuery()
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                .count();
        if (cnt == 0) {
            return false;
        }
        lambdaUpdate().eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                .eq(ShortLinkDO::getDelFlag, 0)
                .set(ShortLinkDO::getDelTime, System.currentTimeMillis())
                .set(ShortLinkDO::getDelFlag, 1)
                .update();
        return true;
    }

    @Override
    public List<RecycleBinShortLinkDTO> pageRecycle(RecyclePageDTO pageReqDTO) {
        Page<ShortLinkDO> linkPage = Page.of(pageReqDTO.getCurrent(), pageReqDTO.getSize());
        linkPage.addOrder(OrderItem.desc("update_time"));
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, pageReqDTO.getGid())
                .eq(ShortLinkDO::getUserName, UserContext.getUserName())
                .eq(ShortLinkDO::getDelFlag, 1);
        Page<ShortLinkDO> shortLinkDOPage = page(linkPage, wrapper);
        List<ShortLinkDO> records = shortLinkDOPage.getRecords();
        if (records.isEmpty()) {
            throw new ClientException("目前没有短链接被放入回收站");
        }
        return records.stream().map(each -> BeanUtil.copyProperties(each, RecycleBinShortLinkDTO.class))
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
                .set(ShortLinkDO::getDelTime, "0")
                .update();
        if (!update) {
            throw new ClientException("短链接恢复失败");
        }
        stringRedisTemplate.delete(String.format(SHORT_URL_NULL_KEY, recycleDTO.getFullShortUrl()));
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