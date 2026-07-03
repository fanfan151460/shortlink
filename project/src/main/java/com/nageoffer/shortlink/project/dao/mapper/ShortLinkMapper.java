package com.nageoffer.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkRespDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    IPage<ShortLinkRespDTO> pageShortLinkWithStats(Page<ShortLinkRespDTO> page, @Param("gid") String gid, @Param("orderFlag") String orderFlag, @Param("userName") String userName);
}
