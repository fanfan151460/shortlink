-- ============================================================
-- t_link 索引优化
-- 1. 删除旧的 full_short_url 单列唯一索引
-- 2. (full_short_url, del_time) 联合唯一索引：解决回收站同名冲突
-- 3. (username, gid, del_flag) 联合索引：覆盖常用列表查询
-- ============================================================

-- t_link_0
ALTER TABLE link.t_link_0 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_0 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_0 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_1
ALTER TABLE link.t_link_1 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_1 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_1 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_2
ALTER TABLE link.t_link_2 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_2 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_2 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_3
ALTER TABLE link.t_link_3 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_3 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_3 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_4
ALTER TABLE link.t_link_4 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_4 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_4 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_5
ALTER TABLE link.t_link_5 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_5 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_5 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_6
ALTER TABLE link.t_link_6 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_6 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_6 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_7
ALTER TABLE link.t_link_7 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_7 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_7 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_8
ALTER TABLE link.t_link_8 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_8 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_8 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_9
ALTER TABLE link.t_link_9 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_9 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_9 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_10
ALTER TABLE link.t_link_10 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_10 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_10 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_11
ALTER TABLE link.t_link_11 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_11 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_11 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_12
ALTER TABLE link.t_link_12 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_12 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_12 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_13
ALTER TABLE link.t_link_13 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_13 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_13 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_14
ALTER TABLE link.t_link_14 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_14 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_14 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);

-- t_link_15
ALTER TABLE link.t_link_15 DROP INDEX idx_unique_full_short_url;
ALTER TABLE link.t_link_15 ADD UNIQUE INDEX uk_full_url_del_time (full_short_url, del_time);
ALTER TABLE link.t_link_15 ADD INDEX idx_user_gid_del (user_name, gid, del_flag);
