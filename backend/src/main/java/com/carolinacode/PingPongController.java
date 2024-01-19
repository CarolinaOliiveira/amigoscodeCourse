package com.carolinacode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingPongController {

    private static int COUNTER = 0;
    record PingPong(String result){}

    @GetMapping("/ping")
    public PingPong getPingPong (){
        System.out.println("hello");
        return new PingPong("Pong: %s".formatted(++COUNTER));
    }

}