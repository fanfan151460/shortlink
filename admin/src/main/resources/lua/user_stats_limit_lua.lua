local username = KEYS[1]
local timeWindow = ARGV[1] -- 时间窗口，单位：秒

-- 构造 Redis 中存储用户访问次数的键名
local accessKey = "short-link:user-flow-risk-control:" .. username

-- 原子递增次数
local currentAccessCount = redis.call("INCR", accessKey)

-- 设置过期时间
if currentAccessCount == 1 then
    redis.call("EXPIRE", accessKey, timeWindow)
end

--返回数据
    return currentAccessCount  -- 第一次访问，返回访问次数 1
end