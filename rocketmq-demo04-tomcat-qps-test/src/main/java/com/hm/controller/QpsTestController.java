package com.hm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class QpsTestController {

    @GetMapping("/qps")
    public String testTomcatQps() {

        return "test tomcat qps";
    }

    @GetMapping("/qps50")
    public String testTomcatQps50() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(50);

        return "test tomcat qps 50";
    }
}
