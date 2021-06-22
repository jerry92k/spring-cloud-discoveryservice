package com.example.secondservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/second-service")
@Slf4j
public class SecondServiceController {
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome to service";
    }

    @GetMapping("/message")
    // spring gateway에서 추가한 second-request 헤더를 파라미터로 읽어
    public String message(@RequestHeader("second-request") String header){
        log.info(header);
        return "hello world in second service";
    }

    @GetMapping("/check")
    public String check(){
        return "hi, check mapping";
    }
}
