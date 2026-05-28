package com.nageoffer.shortlink.project.util;

import java.util.Date;
import java.util.Optional;

public class LinkUtil {

    public static Long getLinkExpireTime(Date validDate) {
        return Optional.ofNullable(validDate)
                .map(each -> each.getTime() - new Date().getTime())
                .orElse(2592000000L);
    }
}
