package com.example.bilibilivideostream.model.server;

import com.example.bilibilivideostream.model.javabean.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QRCodeService {
    @Autowired
    private CookieService cookieService;
    @Autowired
    private Cookie cookie;

    public void matchParamsUpdateCookies(String urlString){
        // 定义正则表达式
        String regexSESSDATA = "SESSDATA=([^&]+)";
        String regexJCT = "bili_jct=([^&]+)";
        String regexMid = "DedeUserID=([^&]+)";

        // 编译正则表达式
        Pattern patternSESSDATA = Pattern.compile(regexSESSDATA);
        Pattern patternJCT = Pattern.compile(regexJCT);
        Pattern patternMid = Pattern.compile(regexMid);

        // 创建 Matcher 对象
        Matcher matcher = patternSESSDATA.matcher(urlString);

        // 查找匹配
        if (matcher.find()) {
            // 获取匹配到的值
            String sessdataValue = matcher.group(1);
            sessdataValue = URLEncoder.encode(sessdataValue, StandardCharsets.UTF_8);
            sessdataValue = sessdataValue.replace("*", "%2A");
            System.out.println("SESSDATA 参数值为: " + sessdataValue);
            cookie.setSessData(sessdataValue);
        } else {
            System.out.println("未找到 SESSDATA 参数");
        }

        matcher = patternJCT.matcher(urlString);
        // 查找匹配
        if (matcher.find()) {
            // 获取匹配到的值
            String jctValue = matcher.group(1);
            System.out.println("jct 参数值为: " + jctValue);
            cookie.setBiliJct(jctValue);
        } else {
            System.out.println("未找到 jct 参数");
        }

        matcher = patternMid.matcher(urlString);
        // 查找匹配
        if (matcher.find()) {
            // 获取匹配到的值
            String midValue = matcher.group(1);
            System.out.println("mid 参数值为: " + midValue);
            cookie.setMid(Integer.parseInt(midValue));
        } else {
            System.out.println("未找到 mid 参数");
        }

        System.out.println(cookie);

        cookieService.updateCookie(cookie);
    }
}
