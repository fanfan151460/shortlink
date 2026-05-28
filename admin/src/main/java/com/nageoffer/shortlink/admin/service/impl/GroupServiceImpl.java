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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements IGroupService {

    @Override
    public void saveGroup(String groupName) {
        saveGroup(groupName, UserContext.getUsername());
    }

    @Override
    public void saveGroup(String groupName, String username) {
        Long count = lambdaQuery().eq(GroupDO::getUsername, username)
                .count();
        if (count > 10) {
            throw new RuntimeException("链接分组最多为10");
        }

        String gid;
        do {
            gid = RandomUtil.randomString(6);
        } while (hasGid(gid));

        GroupDO groupDO = new GroupDO()
                .setName(groupName)
                .setGid(gid)
                .setSortOrder(0)
                .setUsername(username);

        baseMapper.insert(groupDO);
    }

    @Override
    public List<GroupLinkDTO> listGroup() {
        //TODO 当前用户
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> groupDOLambdaQueryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .orderByAsc(GroupDO::getSortOrder, GroupDO::getUpdateTime)
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOS = baseMapper.selectList(groupDOLambdaQueryWrapper);
        return BeanUtil.copyToList(groupDOS, GroupLinkDTO.class);
    }

    @Override
    public void updateGroup(GroupLinkUpdateDTO groupLinkUpdateDTO) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, groupLinkUpdateDTO.getGid())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        groupDO.setName(groupLinkUpdateDTO.getName());
        baseMapper.update(groupDO, queryWrapper);
    }

    @Override
    public void delGroup(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, queryWrapper);

    }

    @Override
    public List<GroupLinkDTO> order(List<GroupLinkOrderDTO> linkOrderDTOList) {
        linkOrderDTOList.forEach(each -> lambdaUpdate()
                .eq(GroupDO::getGid, each.getGroupId())
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .set(GroupDO::getSortOrder, each.getSortOrder())
                .update());
        return listGroup();
    }

    public Boolean hasGid(String Gid) {
        LambdaQueryWrapper<GroupDO> groupDOLambdaQueryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, Gid)
                .eq(GroupDO::getDelFlag, 0);
        return baseMapper.selectOne(groupDOLambdaQueryWrapper) != null;
    }
}
