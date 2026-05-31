package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.nageoffer.shortlink.project.dao.mapper.LinkAccessLogsMapper;
import com.nageoffer.shortlink.project.dto.req.AccessLogReqDTO;
import com.nageoffer.shortlink.project.dto.resp.accessLogRespDTO;
import com.nageoffer.shortlink.project.service.ILinkAccessLogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkAccessLogsServiceImpl extends ServiceImpl<LinkAccessLogsMapper, LinkAccessLogsDO> implements ILinkAccessLogsService {

    private final LinkAccessLogsMapper linkAccessLogsMapper;

    @Override
    public List<accessLogRespDTO> getAccessLogs(AccessLogReqDTO accessLogReqDTO) {
        // 分页查询访问日志
        Page<LinkAccessLogsDO> page = Page.of(accessLogReqDTO.getCurrent(), accessLogReqDTO.getSize());
        LambdaQueryWrapper<LinkAccessLogsDO> wrapper = Wrappers.lambdaQuery(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getFullShortUrl, accessLogReqDTO.getFullShortUrl())
                .eq(LinkAccessLogsDO::getGid, accessLogReqDTO.getGid())
                .between(LinkAccessLogsDO::getCreateTime,
                        LocalDateTime.of(accessLogReqDTO.getStartDate(), LocalTime.MIN),
                        LocalDateTime.of(accessLogReqDTO.getEndDate(), LocalTime.MAX));
        page(page, wrapper);

        // 收集所有用户，查询新老访客类型
        List<String> userList = page.getRecords().stream()
                .map(LinkAccessLogsDO::getUser)
                .distinct()
                .toList();
        Map<String, String> userTypeMap = linkAccessLogsMapper.selectUvTypeGroupByUser(
                        accessLogReqDTO.getFullShortUrl(),
                        accessLogReqDTO.getGid(),
                        accessLogReqDTO.getStartDate().toString(),
                        accessLogReqDTO.getEndDate().toString(),
                        userList)
                .stream()
                .collect(Collectors.toMap(m -> m.get("user"), m -> m.get("uvType")));

        // 转换为响应结果
        return page.getRecords().stream()
                .map(each -> {
                    accessLogRespDTO resp = BeanUtil.copyProperties(each, accessLogRespDTO.class);
                    resp.setUserType(userTypeMap.getOrDefault(each.getUser(), "新访客"));
                    return resp;
                })
                .toList();
    }

}
