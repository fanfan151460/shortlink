-- 营销活动：一个活动 = 一个目标链接 + N 个渠道短链
--
-- !! 手动执行时必须显式指定客户端字符集，否则中文注释会被二次编码成乱码：
--      mysql -uroot -p --default-character-set=utf8mb4 -D link < 13_activity.sql
--    Windows 版 mysql 客户端默认跟随系统字符集（GBK），会把 UTF-8 文件当 GBK 发给服务器。
--    （docker-compose 首次初始化自动执行不受影响：容器内 Linux 默认就是 utf8mb4）
--
-- 建模选择：渠道短链复用 t_link（见文件末尾 ALTER），不另起 t_activity_channel 表。
-- 收益：渠道短链天然复用短链的布隆判重 / 缓存 / 跳转 / UV·PV 统计 / 回收站 / 删除，
--       因此"按渠道看效果"这条统计链路一行都不用改。
-- 代价：t_link 的 16 张分片表都要加列。
--
-- t_activity 未列入 shardingsphere-config.yaml，按单表处理，无需分片（活动数据量极小）。

CREATE TABLE `t_activity` (
    `id`            bigint        NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `activity_name` varchar(64)   DEFAULT NULL COMMENT '活动名称',
    `origin_url`    varchar(1024) DEFAULT NULL COMMENT '活动目标链接（各渠道短链共享）',
    `gid`           varchar(32)   DEFAULT NULL COMMENT '分组标识',
    `user_name`     varchar(64)   DEFAULT NULL COMMENT '创建用户名',
    `status`        tinyint(1)    DEFAULT '0' COMMENT '活动状态 0：进行中 1：已结束',
    `create_time`   datetime      DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime      DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)    DEFAULT '0' COMMENT '删除标识 0：未删除 1：已删除',
    `del_time`      varchar(32)   DEFAULT NULL COMMENT '删除时间戳',
    PRIMARY KEY (`id`),
    KEY `idx_user_gid_del` (`user_name`,`gid`,`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='营销活动';


-- 渠道归属：给 16 张 t_link 分片表各加两列
--   activity_id 为 NULL → 普通短链；非 NULL → 该短链是某活动的渠道短链
--   channel 为投放渠道标识（weixin / douyin / sms ...）
--
-- 唯一索引 uk_activity_channel (activity_id, channel, del_time)：
--   保证同一活动同一渠道只有一个有效短链；
--   普通短链 activity_id/channel 均为 NULL，MySQL 唯一索引不约束多个 NULL，故不受影响；
--   带 del_time 是为了逻辑删除后可重建同渠道短链，与 t_link 自身的 uk_full_url_del_time 同思路。
--
-- !! 前提：未删除的行 del_time 必须是字符串 '0'（由 MyMetaObjectHandler 插入时自动填充），
--    删除后才是时间戳。MySQL 唯一索引只要有一列为 NULL 就整行不校验，
--    所以任何绕过 MyBatis-Plus 自动填充的写入路径（裸 SQL、不经 fill 的批量插入）
--    都会让 del_time 落成 NULL，从而静默地让本约束失效——测试时务必走 save()。
--
-- 注意：分片表上的唯一索引仅在该分片内生效。但同一活动的渠道短链同属一个 gid → 落同一分片，
--       所以分片内唯一约束成立。这是"gid 既是权限维度又是分片键"带来的红利。
ALTER TABLE `t_link_0`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_1`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_2`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_3`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_4`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_5`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_6`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_7`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_8`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_9`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_10`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_11`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_12`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_13`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_14`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);

ALTER TABLE `t_link_15`
    ADD COLUMN `activity_id` bigint      DEFAULT NULL COMMENT '所属活动ID NULL-普通短链',
    ADD COLUMN `channel`     varchar(32) DEFAULT NULL COMMENT '推广渠道标识',
    ADD UNIQUE KEY `uk_activity_channel` (`activity_id`,`channel`,`del_time`);
