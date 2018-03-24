package com.example.controller;

import com.example.comm.aop.LoggerManage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/activity")
public class ActivityController {

    @RequestMapping("/activityList")
    @LoggerManage(description = "活动列表")
    public String activityList(){
        return "admin/activity_list";
    }
    @RequestMapping("/draftList")
    @LoggerManage(description = "草稿箱列表")
    public String draft(){
        return "admin/draft_list";
    }
}