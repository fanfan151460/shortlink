package com.nageoffer.shortlink.project.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StatsQueryReqDTO {

    private String gid;

    private String fullShortUrl;

    private LocalDate startDate;

    private LocalDate endDate;
}
