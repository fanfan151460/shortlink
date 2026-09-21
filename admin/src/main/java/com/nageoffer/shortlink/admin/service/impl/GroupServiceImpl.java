package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkDTO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkOrderDTO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkUpdateDTO;
import com.nageoffer.shortlink.admin.service.IGroupService;
import com.nageoffer.shortlink.framework.exception.ClientException;
import com.nageoffer.shortlink.framework.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_GROUP;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fanfanfan
 * @since 2026-05-20
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements IGroupService {

    private final RedissonClient redissonClient;

    @Override
    public void saveGroup(String groupName) {
        saveGroup(groupName, UserContext.getUsername());
        throw new ServiceException("异常拦截测试");
    }

    @Override
    public void saveGroup(String groupName, String username) {
        RLock rLock = redissonClient.getLock(String.format(LOCK_GROUP, username));
        rLock.lock();
        try {
            Long count = lambdaQuery().eq(GroupDO::getUserName, username)
                    .eq(GroupDO::getDelFlag, 0)
                    .count();
            if (count > 10) {
                throw new ClientException("链接分组最多为10");
            }

            String gid;
            do {
                gid = RandomUtil.randomString(6);
            } while (hasGid(gid));

            GroupDO groupDO = new GroupDO()
                    .setName(groupName)
                    .setGid(gid)
                    .setSortOrder(0)
                    .setUserName(username);

            baseMapper.insert(groupDO);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public List<GroupLinkDTO> listGroup() {
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> groupDOLambdaQueryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUserName, username)
                .eq(GroupDO::getDelFlag, 0)
                .orderByAsc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getUpdateTime);
        List<GroupDO> groupDOS = baseMapper.selectList(groupDOLambdaQueryWrapper);
        return BeanUtil.copyToList(groupDOS, GroupLinkDTO.class);
    }

    public List<GroupLinkDTO> listAllGroup() {
        String username = UserContext.getUsername();
        List<GroupDO> groupDOS = lambdaQuery()
                .eq(GroupDO::getUserName, username)
                .eq(GroupDO::getDelFlag, 1)
                .orderByAsc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getUpdateTime)
                .list();
        return BeanUtil.copyToList(groupDOS, GroupLinkDTO.class);
    }


    @Override
    public void updateGroup(GroupLinkUpdateDTO groupLinkUpdateDTO) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUserName, UserContext.getUsername())
                .eq(GroupDO::getGid, groupLinkUpdateDTO.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        groupDO.setName(groupLinkUpdateDTO.getName());
        baseMapper.update(groupDO, queryWrapper);
    }

    @Override
    public void delGroup(String gid, boolean hasLinks) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUserName, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        if (hasLinks) {
            groupDO.setDelFlag(1);
            baseMapper.update(groupDO, queryWrapper);
        } else {
            baseMapper.delete(queryWrapper);
        }
    }

    @Override
    public List<GroupLinkDTO> order(List<GroupLinkOrderDTO> linkOrderDTOList) {
        linkOrderDTOList.forEach(each -> lambdaUpdate()
                .eq(GroupDO::getGid, each.getGroupId())
                .eq(GroupDO::getUserName, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .set(GroupDO::getSortOrder, each.getSortOrder())
                .update());
        return listGroup();
    }

    @Override
    public boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> groupDOLambdaQueryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUserName, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        return baseMapper.selectOne(groupDOLambdaQueryWrapper) != null;
    }
}
