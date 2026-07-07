CREATE TABLE `t_link_access_stats` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                       `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '完整短链接',
                                       `date` date DEFAULT NULL COMMENT '日期',
                                       `pv` int DEFAULT NULL COMMENT '访问量',
                                       `uv` int DEFAULT NULL COMMENT '独立访问数',
                                       `uip` int DEFAULT NULL COMMENT '独立IP数',
                                       `hour` int DEFAULT NULL COMMENT '小时',
                                       `weekday` int DEFAULT NULL COMMENT '星期',
                                       `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                       `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                       `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识：0 未删除 1 已删除',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE KEY `t_link_access_stats_full_short_url_IDX` (`full_short_url`,`date`,`hour`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40668 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC

