package com.nageoffer.shortlink.admin.remote.dto.resp;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccessStatsVO {

    private LocalDate date;

    private Integer pv;

    private Integer uv;

    private Integer uip;
}
