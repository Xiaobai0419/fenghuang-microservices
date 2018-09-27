package com.xiaobai.fenghuangservicemain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/main")
public class FengMainController {

    @Autowired
    private DiscoveryClient client;

    @Value("${server.port}")
    String port;

    @Value("${spring.application.name}")
    String serviceId;

    @GetMapping("/hi")
    public String home(@RequestParam String name) {//这里要传入参数，否则返回400状态码！！
        String serviceId = client.getInstances(this.serviceId).get(0).getServiceId();
        return "Hi "+name+",I am from port:" +port + ",I am an instance of:" + serviceId;
    }
/**400返回状态码原因：
 原因：1）前端提交数据的字段名称或者是字段类型和后台的实体类不一致 或 前端提交的参数跟后台需要的参数个数不一致，导致无法封装；

 2）前端提交的到后台的数据应该是json字符串类型，而前端没有将对象转化为字符串类型；

 解决方案：

 1）对照字段名称，类型保证一致性

 2）使用stringify将前端传递的对象转化为字符串    data: JSON.stringify(param)  ;
 */
}
