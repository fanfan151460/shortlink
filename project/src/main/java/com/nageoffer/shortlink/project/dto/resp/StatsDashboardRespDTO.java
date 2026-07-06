package com.nageoffer.shortlink.project.dto.resp;

import lombok.Data;

import java.util.List;

@Data
public class StatsDashboardRespDTO {

    private List<StatsItemVO> locale;

    private List<StatsItemVO> os;

    private List<StatsItemVO> browser;

    private List<StatsItemVO> device;

    private List<StatsItemVO> network;

    private List<AccessStatsVO> access;
}
