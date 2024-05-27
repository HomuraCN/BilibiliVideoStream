package com.example.bilibilivideostream.controller;

import com.example.bilibilivideostream.model.javabean.Result;
import com.example.bilibilivideostream.model.server.VideoStreamWBIService;
import com.example.bilibilivideostream.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class VideoStreamWBIController {
    @Autowired
    private VideoStreamWBIService videoStreamWBIService;

    @GetMapping("/downloadVideoWBI")
    public Result<?> downloadVideo(@RequestParam("url") String url, @RequestParam("fileName") String fileName){
        videoStreamWBIService.downloadSingleVideo(url, fileName);
        return ResultUtils.success();
    }

    @GetMapping("/downloadBangumi")
    public Result<?> downloadBangumi(@RequestParam("url") String url){
        videoStreamWBIService.downloadBangumi(url);
        return ResultUtils.success();
    }
}
