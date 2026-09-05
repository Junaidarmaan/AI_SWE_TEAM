package com.junnu.devflow.controller;

import org.springframework.web.bind.annotation.RestController;

import com.junnu.devflow.services.ChatService;

@RestController("Home/")
public class HomeController {
    ChatService service;
    public HomeController(ChatService service){
        this.service = service;
    }
    // @GetMapping("chat/{msg}")
    // public String chat(@PathVariable String msg){
    //     return service.chat(msg);
    // }
}
