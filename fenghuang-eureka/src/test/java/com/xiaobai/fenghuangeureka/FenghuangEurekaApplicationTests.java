package com.xiaobai.fenghuangeureka;

import com.xiaobai.fenghuangeureka.config.Config;
import com.xiaobai.fenghuangeureka.config.MyBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)//Spring整合Junit
@SpringBootTest(classes = FenghuangEurekaApplication.class)//默认启动主启动类，也可加classes属性指明启动类
@WebAppConfiguration
public class FenghuangEurekaApplicationTests {

	@Autowired
	private WebApplicationContext wac;//注入WebApplicationContext环境
	private MockMvc mvc;
	private MockHttpSession session;
	@Autowired
	private MyBean bean;

	@Before
	public void setupMockMvc(){
		mvc = MockMvcBuilders.webAppContextSetup(wac).build(); //初始化MockMvc对象
		session = new MockHttpSession();
		session.setAttribute("user",bean); //拦截器那边会判断用户是否登录，所以这里注入一个用户
	}

	@Test
	public void contextLoads() {
		List<String> list = wac.getBean(Config.class).getServers();
		log.info(list.toString());
		MyBean bean = wac.getBean(MyBean.class);
		log.info(bean.getName());
	}

	@Test
	public void testMVC() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/controller/getBean")
		.accept(MediaType.APPLICATION_JSON_UTF8).session(session))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print());
	}
}
