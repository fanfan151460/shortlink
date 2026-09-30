CREATE TABLE `t_link_locale_stats` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                       `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整短链接',
                                       `date` date DEFAULT NULL COMMENT '日期',
                                       `cnt` int DEFAULT NULL COMMENT '访问量',
                                       `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '省份名称',
                                       `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '市名称',
                                       `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '城市编码',
                                       `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '国家标识',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_time` datetime NOT NULL COMMENT '修改时间',
                                       `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0表示删除 1表示未删除',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `idx_unique_locale_stats` (`full_short_url`,`date`,`adcode`,`province`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1203 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

