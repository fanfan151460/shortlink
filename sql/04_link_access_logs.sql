CREATE TABLE `t_link_access_logs` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                      `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整短链接',
                                      `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '分组标识',
                                      `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户信息',
                                      `ip` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP',
                                      `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '浏览器',
                                      `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作系统',
                                      `network` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问网络',
                                      `device` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问设备',
                                      `locale` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问地区',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                      `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                      `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40650 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci

