package com.xiaobai.fenghuangservicemain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FenghuangServiceMainApplication.class)
public class FenghuangServiceMainApplicationTests {

	@Autowired
	private WebApplicationContext wac;
	private MockMvc mvc;

	@Before
	public void prepare() {
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
//需要打开注册中心，并使用非测试方式开启一个这里的服务注册到其中，才能有服务Id,这里再开启一个测试才能测试成功！！（但Port不对！）
	@Test
	public void testMVC() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/service/main/hi")
				.param("name","Phenix")//一定要按接口要求传入参数！否则返回400状态码！！
		.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());
	}

}
