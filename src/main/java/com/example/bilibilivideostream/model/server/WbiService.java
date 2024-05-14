package com.example.bilibilivideostream.model.server;

import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.bilibilivideostream.model.javabean.Cookie;
import com.example.bilibilivideostream.model.javabean.Nav;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WbiService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Cookie cookie;
    private static final int[] mixinKeyEncTab = new int[]{
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };

    public String getMixinKey(String imgKey, String subKey) {
        String s = imgKey + subKey;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            key.append(s.charAt(mixinKeyEncTab[i]));
        }
        return key.toString();
    }

    public Map<String, String> getWbiKey() {
        String imgKey = "";
        String subKey = "";

        String url = "https://api.bilibili.com/x/web-interface/nav";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "SESSDATA=" + "0e8a14fd%2C1730098942%2C57e15%2A51CjCxZ4LG7pNJnG1vVz9N4L2v2bigQNVO4tktFnABS_qKUmjxW6NmCm5DQucQzc26Ut0SVlpic0ZESWxJaVVnMS1JSFROMHFOR251bUNCUFNmM1draC0wUjk3TmxHOVVNRDFVcUZzNUlNRW5TX2M5Vi1TTVpkdmh6dHBrSVFPSU5PS0pYUl9QM1hBIIEC");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        Class<Nav> responseType = Nav.class;
        ResponseEntity<Nav> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                responseType
        );
        Nav nav = responseEntity.getBody();

//        System.out.println(nav);

        // 定义正则表达式
        String regexWbi = "wbi/([^.]+)";
        // 编译正则表达式
        Pattern patternWbi = Pattern.compile(regexWbi);
        // 创建 Matcher 对象
        Matcher matcher = patternWbi.matcher(nav.getData().getWbiImg().getImgUrl());

        Map<String, String> key = new HashMap<>();

        // 查找匹配
        if (matcher.find()) {
            // 获取匹配到的值
            imgKey = matcher.group(1);
//            System.out.println("imgKey 参数值为: " + imgKey);
            key.put("imgKey", imgKey);
        } else {
//            System.out.println("未找到 imgKey 参数");
        }

        matcher = patternWbi.matcher(nav.getData().getWbiImg().getSubUrl());
        // 查找匹配
        if (matcher.find()) {
            // 获取匹配到的值
            subKey = matcher.group(1);
//            System.out.println("subKey 参数值为: " + subKey);
            key.put("subKey", subKey);
        } else {
//            System.out.println("未找到 subKey 参数");
        }
        return key;
    }

    public String getWbiUrl(LinkedHashMap<String, Object> map) {
        Map<String, String> key = getWbiKey();
        String mixinKey = getMixinKey(key.get("imgKey"), key.get("subKey"));
        map.put("wts", System.currentTimeMillis() / 1000);
        StringJoiner param = new StringJoiner("&");
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> param.add(entry.getKey() + "=" + URLUtil.encode(entry.getValue().toString())));
        String s = param + mixinKey;
        String wbiSign = SecureUtil.md5(s);
//        System.out.println(wbiSign);
        String finalParam = param + "&w_rid=" + wbiSign;
        return finalParam;
    }
}
