package com.example.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/first-service")
@Slf4j
public class FirstServiceController {


    Environment env;

    public FirstServiceController(Environment env){
        this.env=env;
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome to service";
    }

    @GetMapping("/message")
    // spring gateway에서 추가한 first-request 헤더를 파라미터로 읽어
    public String message(@RequestHeader("first-request") String header){
        log.info(header);
        return "hello world in first service";
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request){
        //HttpServletRequest 객체에서 가져오거나 Environment객체에서 가져옴
        log.info("server port = {} "+request.getServerPort());
        return "hi, check mapping"+env.getProperty("local.server.port");
    }
}
