package com.chatroomserver.chatroonbackend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个基于令牌桶算法的过滤器，用于限制HTTP请求的速率。
 * 这个过滤器继承自Spring的OncePerRequestFilter，确保每个请求只被过滤一次。
 *
 * @author peter
 * @since 2024-09-21
 */
public class RateLimitFilter extends OncePerRequestFilter {

    // 用于存储每个IP地址的令牌桶的并发映射。
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // 令牌桶的容量，表示在一个周期内可以生成的最大令牌数。
    private final int capacity;

    // 每个周期内补充的令牌数。
    private final int refillTokens;

    // 补充令牌的周期时间。
    private final Duration refillDuration;

    /**
     * 构造一个新的RateLimitFilter。
     *
     * @param capacity          令牌桶的容量。
     * @param refillTokens      每个周期内补充的令牌数。
     * @param refillDuration    补充令牌的周期时间。
     */
    public RateLimitFilter(int capacity, int refillTokens, Duration refillDuration) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillDuration = refillDuration;
    }

    /**
     * 执行实际的过滤逻辑。
     * 如果请求的IP地址对应的令牌桶中有足够的令牌，则允许请求通过，否则拒绝请求。
     *
     * @param request           当前的HTTP请求。
     * @param response          当前的HTTP响应。
     * @param filterChain       过滤器链，用于将请求和响应传递给下一个过滤器或目标资源。
     * @throws ServletException 如果在处理请求或响应时发生错误。
     * @throws IOException       如果在处理请求或响应时发生I/O错误。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 获取请求的远程IP地址。
        String ipAddress = request.getRemoteAddr();

        // 从并发映射中获取或创建一个令牌桶。
        Bucket bucket = buckets.computeIfAbsent(ipAddress, this::newBucket);

        // 如果令牌桶可以消耗一个令牌，则允许请求通过。
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        }
        // 否则，拒绝请求，并返回429状态码，表示请求过多。
        else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
        }
    }

    /**
     * 创建一个新的令牌桶。
     *
     * @param ipAddress 与令牌桶关联的IP地址。
     * @return 一个新的令牌桶实例。
     */
    public Bucket newBucket(String ipAddress) {
        // 使用经典的带宽限制策略创建一个限制。
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(refillTokens, refillDuration));

        // 使用Bucket4j构建器创建并返回一个新的令牌桶。
        return Bucket4j.builder().addLimit(limit).build();
    }
}
