package com.nageoffer.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkGoToMapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.mq.producer.LinkStatsProducer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShortLinkServiceImplTest {

    @Mock private RedissonClient redissonClient;
    @Mock private RLock rLock;
    @Mock private RBloomFilter<String> bloomFilter;
    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;
    @Mock private ShortLinkGoToMapper shortLinkGoToMapper;
    @Mock private LinkStatsProducer linkStatsProducer;
    @Mock private ShortLinkMapper baseMapper;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @InjectMocks
    private ShortLinkServiceImpl shortLinkService;

    private final StringWriter notFoundBody = new StringWriter();

    @BeforeEach
    void setUp() throws IOException {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(response.getWriter()).thenReturn(new PrintWriter(notFoundBody));
        ReflectionTestUtils.setField(shortLinkService, "domain", "xiyl.cn");
    }

    @Test
    void testRedisHit() throws IOException {
        when(valueOperations.get("Full-Link:xiyl.cn/4d5U")).thenReturn("https://taobao.com");

        shortLinkService.gotoOriginUrl("4d5U", request, response);

        verify(response).sendRedirect("https://taobao.com");
        verify(bloomFilter, never()).contains(anyString());
        verify(redissonClient, never()).getLock(anyString());
    }

    @Test
    void testBloomFilterReject() throws IOException {
        when(valueOperations.get(anyString())).thenReturn(null);
        when(bloomFilter.contains("xiyl.cn/4d5U")).thenReturn(false);

        shortLinkService.gotoOriginUrl("4d5U", request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(response, never()).sendRedirect(anyString());
        // 断言拿到的是 jar 里那份页面，而不是加载失败时的兜底文案
        assertTrue(notFoundBody.toString().contains("pc-container"),
                "notFound 页面没从 classpath 里读出来");
        verify(redissonClient, never()).getLock(anyString());
    }

    @Test
    void testNullCacheHit() throws IOException {
        when(valueOperations.get("Full-Link:xiyl.cn/4d5U")).thenReturn(null);
        when(bloomFilter.contains("xiyl.cn/4d5U")).thenReturn(true);
        when(valueOperations.get("SHORT_URL_NULL:xiyl.cn/4d5U")).thenReturn("1");

        shortLinkService.gotoOriginUrl("4d5U", request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(response, never()).sendRedirect(anyString());
        verify(redissonClient, never()).getLock(anyString());
    }

    @Test
    void testLockDoubleCheckRealCacheHit() throws IOException {
        when(bloomFilter.contains("xiyl.cn/4d5U")).thenReturn(true);
        when(valueOperations.get("Full-Link:xiyl.cn/4d5U")).thenReturn(null, "https://taobao.com");
        when(valueOperations.get("SHORT_URL_NULL:xiyl.cn/4d5U")).thenReturn(null);

        shortLinkService.gotoOriginUrl("4d5U", request, response);

        verify(response).sendRedirect("https://taobao.com");
        verify(shortLinkGoToMapper, never()).selectOne(any(Wrapper.class));
    }

    @Test
    void testLockDoubleCheckNullCacheHit() throws IOException {
        when(bloomFilter.contains("xiyl.cn/4d5U")).thenReturn(true);
        when(valueOperations.get("Full-Link:xiyl.cn/4d5U")).thenReturn(null, null);
        when(valueOperations.get("SHORT_URL_NULL:xiyl.cn/4d5U")).thenReturn(null, "1");

        shortLinkService.gotoOriginUrl("4d5U", request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(response, never()).sendRedirect(anyString());
        verify(shortLinkGoToMapper, never()).selectOne(any(Wrapper.class));
    }
}
