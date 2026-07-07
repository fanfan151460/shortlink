CREATE TABLE `users_0` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_1` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_2` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_3` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_4` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_5` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_6` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_7` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_8` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_9` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_10` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_11` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_12` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_13` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_14` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


CREATE TABLE `users_15` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
                           `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
                           `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
                           `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
                           `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
                           `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                           `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标识 0: 未删除 1: 已删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `users_username_IDX` (`username`) USING BTREE,
                           UNIQUE KEY `users_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci


