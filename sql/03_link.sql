CREATE TABLE `t_link_0` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_1` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_2` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_3` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_4` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_5` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_6` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_7` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_8` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_9` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_10` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_11` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_12` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_13` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_14` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `t_link_15` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `domain` varchar(128) DEFAULT NULL COMMENT '域名',
                            `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '短链接',
                            `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                            `origin_url` varchar(1024) DEFAULT NULL COMMENT '原始链接',
                            `click_num` int DEFAULT '0' COMMENT '点击量',
                            `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',
                            `enable_status` tinyint(1) DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
                            `created_type` tinyint(1) DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
                            `valid_date_type` tinyint(1) DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
                            `valid_date` datetime DEFAULT NULL COMMENT '有效期',
                            `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                            `total_uv` int DEFAULT '0' COMMENT '历史uv',
                            `total_pv` int DEFAULT '0' COMMENT '历史pv',
                            `total_uip` int DEFAULT '0' COMMENT '历史uip',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            `favicon` varchar(256) DEFAULT NULL COMMENT '网站图标',
                            `user_name` varchar(64) DEFAULT NULL COMMENT '创建用户名',
                            `del_time` varchar(32) DEFAULT NULL COMMENT '删除时间戳',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_full_url_del_time` (`full_short_url`,`del_time`),
                            KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



