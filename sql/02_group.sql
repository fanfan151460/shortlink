CREATE TABLE `t_group` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                           `name` varchar(64) DEFAULT NULL COMMENT '分组名称',
                           `user_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建分组用户名',
                           `sort_order` int DEFAULT NULL COMMENT '分组排序',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `t_group_username_IDX` (`user_name`,`gid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

