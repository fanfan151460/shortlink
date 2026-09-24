package com.nageoffer.shortlink.project.controller;

import com.nageoffer.shortlink.project.service.IShortLinkService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * 回归用：短链跳转接口自己写 ServletResponse，返回值必须保持 void。
 * 一旦改成 Result 这类非 null 返回值，@RestController 会接着把返回值序列化成 body，
 * 而 response 此刻已经是 text/html，转换器写不进去 —— 网关侧的表现就是 500
 * （HttpMediaTypeNotAcceptableException 触发错误页，再调 getOutputStream 时被
 * “getWriter() has already been called” 顶掉）。
 */
class ShortLinkControllerGoToTest {

    private static final String PAGE = "<html><body><div class=\"pc-container\">页面不存在</div></body></html>";

    @Test
    void gotoMustWritePageAndNotSerializeReturnValue() throws Exception {
        IShortLinkService service = mock(IShortLinkService.class);
        doAnswer(invocation -> {
            HttpServletResponse resp = invocation.getArgument(2);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getOutputStream().write(PAGE.getBytes(StandardCharsets.UTF_8));
            return null;
        }).when(service).gotoOriginUrl(any(), any(), any());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ShortLinkController(service)).build();

        mockMvc.perform(get("/4d5U")).andExpect(result -> {
            assertEquals(404, result.getResponse().getStatus(),
                    "返回值被当成 body 序列化时状态码会变成 406");
            assertTrue(new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8)
                            .contains("pc-container"),
                    "service 写的页面没落到响应体里");
        });
    }
}
