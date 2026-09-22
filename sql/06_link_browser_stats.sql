CREATE TABLE `t_link_browser_stats` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                        `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                                        `date` date DEFAULT NULL COMMENT '日期',
                                        `cnt` int DEFAULT NULL COMMENT '访问量',
                                        `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                        `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                        `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`,`date`,`browser`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40650 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

