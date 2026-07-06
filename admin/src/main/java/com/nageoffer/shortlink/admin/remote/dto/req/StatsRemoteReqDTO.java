package com.nageoffer.shortlink.admin.remote.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StatsRemoteReqDTO {

    private String gid;

    private String fullShortUrl;

    private LocalDate startDate;

    private LocalDate endDate;
}
