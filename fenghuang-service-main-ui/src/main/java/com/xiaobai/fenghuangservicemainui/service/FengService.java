package com.xiaobai.fenghuangservicemainui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(name = "service-main")//调用的服务名serviceId
public interface FengService {
    @GetMapping("/service/main/hi")
    String sayHi(@RequestParam(value = "name") String name);//类名、方法名均不一定与被调用服务对应类名、方法名一致，只要保证注册到同一中心的服务名和调用映射路径即可
}
