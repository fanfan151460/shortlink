-- 活动有效期：活动自身的有效期，作为批量建渠道时短链有效期的默认来源
--
-- !! 与 13_activity.sql 同样：手动执行时必须显式指定客户端字符集，否则中文注释乱码：
--      mysql -uroot -p --default-character-set=utf8mb4 -D link < 14_activity_valid_date.sql
--
-- !! 不要追加到 13_activity.sql：那个文件在 docker 首次初始化数据卷时已执行过，追加不会重跑。
--
-- 为什么是两列而不是一列可空 valid_date（NULL = 永久）：
--   1. 前端「永久有效 / 自定义日期」单选天然映射到 valid_date_type，
--      一列方案下"显式选了永久"和"从没设置过"都是 NULL、无法区分；
--   2. 继承映射可以直接复用 ShortLinkServiceImpl.updateShortLink 已有的归一化写法
--      type == 0 ? null : validDate，不用再造第二套规则；
--   3. 与 t_link 同构，读代码不用切心智模型。
--
-- 类型用 date 不是 datetime：有效期只到日期，不精确到时间。
-- （t_link.valid_date 是历史遗留的 datetime，Java 侧本来就是 LocalDate，不动它。）
--
-- DEFAULT '0' 让存量活动自动落到"永久有效"，正好符合"继承只对新链接生效、旧链接不回溯"。

ALTER TABLE `t_activity`
    ADD COLUMN `valid_date_type` tinyint(1)  DEFAULT '0' COMMENT '有效期类型 0：永久有效 1：用户自定义',
    ADD COLUMN `valid_date`      date        DEFAULT NULL COMMENT '有效期（仅自定义日期时有值）';
