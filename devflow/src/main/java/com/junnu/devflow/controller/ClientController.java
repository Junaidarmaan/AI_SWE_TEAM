package com.junnu.devflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.junnu.devflow.services.BusinessAnalystService;

@RestController
public class ClientController {
    BusinessAnalystService service;
    public ClientController(BusinessAnalystService service){
        this.service = service;
    }
    @GetMapping("chat/{msg}")
    String chat(@PathVariable String msg){
        String response =  service.chat(msg);
        return response;
    }
}
