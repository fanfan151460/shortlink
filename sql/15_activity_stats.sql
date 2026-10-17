-- 活动级跨渠道去重 UV 统计表
--
-- !! 手动执行时必须显式指定客户端字符集，否则中文注释会被二次编码成乱码：
--      mysql -uroot -p --default-character-set=utf8mb4 -D link < 15_activity_stats.sql
--    Windows 版 mysql 客户端默认跟随系统字符集（GBK），会把 UTF-8 文件当 GBK 发给服务器。
--    （docker-compose 首次初始化自动执行不受影响：容器内 Linux 默认就是 utf8mb4）
--
-- 为什么需要这张表：渠道短链各自的 t_link.total_uv 是按"链接"去重的，同一批用户投放到多个渠道时
-- 每个渠道各算一次，加起来会高估真实触达人数。本表按 (activity_id, date) 存"当天该活动去重后的访客数"，
-- 与"各渠道 UV 之和"对比即可看出重复触达规模。
--
-- 去重的前提是访客身份跨渠道一致：uv cookie 已从 path=/{code} 提到 path=/（见 ShortLinkServiceImpl.addLinkStats），
-- 同一个浏览器访问同一活动的任意渠道短链都带同一个 uvid。写入口在 MQ 消费者，按 Redis Set
-- Short-Link:Activity-UV:{activityId}:{date} 的 SADD 结果决定是否 +1。
--
-- 语义边界（与 t_link 的按天分桶一致，刻意为之）：
--   跨天不做精确去重 —— 同一个人连着两天访问，两天各计一次（否则需要无 TTL 的集合，会变成 bigkey）。
--   所以本表的 uv 是"累计日活之和"口径中的"单日活动去重 UV"，不是累计去重 UV。
--
-- 与 t_activity 同样未列入 shardingsphere-config.yaml，按单表处理（活动数量极小，无需分片）。

CREATE TABLE `t_activity_stats` (
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `activity_id` bigint      NOT NULL COMMENT '活动ID',
    `date`        date        NOT NULL COMMENT '日期',
    `uv`          int         DEFAULT NULL COMMENT '当天活动去重UV',
    `create_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime    DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)  DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_date` (`activity_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动统计表';
