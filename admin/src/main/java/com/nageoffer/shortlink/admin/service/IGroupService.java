package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkDTO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkOrderDTO;
import com.nageoffer.shortlink.admin.dto.req.GroupLinkUpdateDTO;

import java.util.List;

public interface IGroupService extends IService<GroupDO> {

    /**
     * 创建分组
     * @param groupName 组名
     */
    void saveGroup(String groupName);

    /**
     * 创建分组（指定用户，用于注册时初始化默认分组）
     * @param groupName 组名
     * @param username  用户名
     */
    void saveGroup(String groupName, String username);

    /**
     * 查询分组
     * @return 分组列表
     */
    List<GroupLinkDTO> listGroup();

    /**
     * 更新分组
     * @param groupLinkUpdateDTO
     */
    void updateGroup(GroupLinkUpdateDTO groupLinkUpdateDTO);

    /**
     * 删除分组
     * @param gid
     */
    void delGroup(String gid);

    /**
     * 排序分组
     * @param linkOrderDTOList
     * @return 新顺序
     */
    List<GroupLinkDTO> order(List<GroupLinkOrderDTO> linkOrderDTOList);

    /**
     * 判断gid是否属于当前用户
     * @param gid 分组标识
     * @return 是否属于当前用户
     */
    boolean hasGid(String gid);
}
