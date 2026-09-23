package com.nageoffer.shortlink.gateway.filter;

import cn.hutool.json.JSONUtil;
import com.nageoffer.shortlink.gateway.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static com.nageoffer.shortlink.gateway.constant.RedisConstant.LOGIN_KEY;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.short-link.white-list}")
    private String whiteList;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final List<String> shortLinkWhiteList = Arrays.asList(whiteList.split(","));
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
        // 非 /api 路径（短链跳转、swagger 文档等）不需要鉴权，直接放行
        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }
        if (shortLinkWhiteList.contains(path)) {
            return chain.filter(exchange);
        }
        if (path.equals("/api/short-link/admin/v1/user") && request.getMethod() == HttpMethod.POST) {
            return chain.filter(exchange);
        }
        String userName = request.getHeaders().getFirst("username");
        String token = request.getHeaders().getFirst("token");
        if (userName == null || token == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();  // setComplete 返回 Mono<Void>
        }
        Object user = stringRedisTemplate.opsForHash().get(LOGIN_KEY + userName, token);
        if(user == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        UserDTO userDTO = JSONUtil.toBean((String)user, UserDTO.class);
        ServerHttpRequest newReq = request.mutate().header("token", token)
                .header("username", userDTO.getUsername())
                .build();
        exchange = exchange.mutate().request(newReq).build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
