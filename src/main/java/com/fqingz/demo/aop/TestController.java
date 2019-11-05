package com.fqingz.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Fang Qing
 * @date 2019/10/31 17:23
 */
@Slf4j
@OpLog("aopTest")
@RestController
@RequestMapping("/test")
public class TestController {

    @OpLog("id")
    @PostMapping("/hia/{id}")
    public String hia(@PathVariable(value = "id") String id) {
        log.info ("执行方法");
        return "○( ＾皿＾)っ " + id;
    }

    @PostMapping("/")
    public String emm() {
        return "(⊙o⊙)…";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response){


    }
}
