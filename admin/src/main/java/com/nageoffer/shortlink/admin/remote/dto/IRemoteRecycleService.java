package com.nageoffer.shortlink.admin.remote.dto;

import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkRecycleDTO;

public interface IRemoteRecycleService {

    void saveRecycleBin(ShortLinkRecycleDTO recycleDTO);
}
