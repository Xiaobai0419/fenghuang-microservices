package com.xiaobai.fenghuangeureka.controller;

import com.xiaobai.fenghuangeureka.config.MyBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/controller")
public class TestController {

    @RequestMapping("/getBean")
    public MyBean getBean(HttpServletRequest request) {
        return (MyBean) request.getSession().getAttribute("user");
    }
}
