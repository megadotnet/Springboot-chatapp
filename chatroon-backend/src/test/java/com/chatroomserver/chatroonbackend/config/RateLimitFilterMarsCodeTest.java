package com.chatroomserver.chatroonbackend.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * RateLimitFilterMarsCodeTest
 * @author peter
 */
public class RateLimitFilterMarsCodeTest {


  @Test
  public void testDoFilterInternal_AllowRequest() throws ServletException, IOException {
    // 创建一个 RateLimitFilter 对象，设置容量、补充令牌数和补充周期
    RateLimitFilter filter = new RateLimitFilter(10, 5, Duration.ofSeconds(1));

    // 创建一个模拟的 HTTP 请求和响应
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 调用 doFilterInternal 方法
    filter.doFilterInternal(request, response, (servletRequest, servletResponse) -> {
    });

    // 断言请求被允许通过，响应状态码为 200
    assertEquals(200, response.getStatus());
  }

  @Test
  public void testDoFilterInternal_DenyRequest() throws ServletException, IOException {
    // 创建一个 RateLimitFilter 对象，设置容量、补充令牌数和补充周期
    RateLimitFilter filter = new RateLimitFilter(1, 1, Duration.ofSeconds(10));

    // 创建一个模拟的 HTTP 请求和响应
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // 连续调用 doFilterInternal 方法多次，模拟请求过多的情况
    for (int i = 0; i < 10; i++) {
      filter.doFilterInternal(request, response, (servletRequest, servletResponse) -> {
      });
    }

    // 断言请求被拒绝，响应状态码为 429
    assertEquals(429, response.getStatus());
    assertEquals("Too many requestsToo many requestsToo many requestsToo many requestsToo many requestsToo many requestsToo many requestsToo many requestsToo many requests", response.getContentAsString());
  }



  @Test
  public void testNewBucket() {
    // 创建一个 RateLimitFilter 对象
    RateLimitFilter filter = new RateLimitFilter(10, 5, Duration.ofSeconds(1));

    // 创建一个新的令牌桶
    Bucket bucket = filter.newBucket("127.0.0.1");

    // 断言令牌桶不为空
    assertNotNull(bucket);
  }
}
