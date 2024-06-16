package com.example.bilibilivideostream.controller;

import com.example.bilibilivideostream.config.WebConfiguration;
import com.example.bilibilivideostream.model.javabean.Result;
import com.example.bilibilivideostream.model.server.QRCodeService;
import com.example.bilibilivideostream.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
public class QRCodeController {

    @Autowired
    private WebConfiguration webConfiguration;
    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("/getQRCode")
    public Result<?> getQRCode(){
        return ResultUtils.success(webConfiguration.restTemplate().getForObject(
                "https://passport.bilibili.com/x/passport-login/web/qrcode/generate",
                Object.class
        ));
    }
    @GetMapping("/verifyQRCode")
    public Result<?> verifyQRCode(@RequestParam("qrcodekey") String qrcodekey){
        Result result = new Result();
        result = ResultUtils.success(webConfiguration.restTemplate().getForObject(
                "https://passport.bilibili.com/x/passport-login/web/qrcode/poll?qrcode_key="
                        + qrcodekey, Object.class
        ));
        String json = result.getData().toString();

        // 定义正则表达式
        String regexCode = " code=([^,]+)";
        // 编译正则表达式
        Pattern patternCode = Pattern.compile(regexCode);
        // 创建 Matcher 对象
        Matcher matcher = patternCode.matcher(json);

        if(matcher.find()){
            // 查找匹配
//            System.out.println("matcher.group(1):" + matcher.group(1));
            String codeValue = matcher.group(1);
            if(!codeValue.equals("0")){
                return ResultUtils.error(Integer.valueOf(codeValue), "扫描异常");
            }
            else{
                qrCodeService.matchParamsUpdateCookies(json);
                return ResultUtils.success();
            }
        }
        else{
            return ResultUtils.error(0, "未找到匹配");
        }


    }
}
