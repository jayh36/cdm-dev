package com.skcc.spring.controller;

import com.skcc.spring.service.ApiHubService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

//@RestController
@Controller
@RequestMapping("/apihub")
public class ApiHubController {

    @Autowired
    ApiHubService apiHubService;

    @GetMapping("/serviceDeviceInfo")
    public String serviceDeviceInfo(Model model, @RequestParam(required = false) String svcMgmtNum){
        if(svcMgmtNum!=null){
            Map<String, Object> body = apiHubService.callApiHub(svcMgmtNum);
            model.addAttribute("serviceDeviceInfo", body);
        }
        return "apihub/apihub";

    }

}
