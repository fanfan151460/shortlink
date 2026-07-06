package com.nageoffer.shortlink.admin.remote.dto.resp;

import lombok.Data;

import java.util.List;

@Data
public class StatsDashboardVO {

    private List<StatsItemVO> locale;

    private List<StatsItemVO> os;

    private List<StatsItemVO> browser;

    private List<StatsItemVO> device;

    private List<StatsItemVO> network;

    private List<AccessStatsVO> access;
}
