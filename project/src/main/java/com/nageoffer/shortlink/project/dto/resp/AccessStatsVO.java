package com.nageoffer.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessStatsVO {

    private LocalDate date;

    private Integer pv;

    private Integer uv;

    private Integer uip;
}
