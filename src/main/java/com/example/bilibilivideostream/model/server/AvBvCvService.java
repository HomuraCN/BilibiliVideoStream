package com.example.bilibilivideostream.model.server;

import com.example.bilibilivideostream.model.response.BangumiInfo;
import com.example.bilibilivideostream.model.response.VideoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AvBvCvService {
    private static final long XOR_CODE = 23442827791579L;
    private static final String Code_Table = "FcwAPNKTMug3GV5Lj7EJnHpWsx4tb8haYeviqBz6rkCy12mUSDQX9RdoZf";
    @Autowired
    private RestTemplate restTemplate;

    public String bvToAv(String url){
        StringBuilder BV = new StringBuilder();
        String regexBV = "video/([^/]+)";
        Pattern patternBV = Pattern.compile(regexBV);
        Matcher matcher = patternBV.matcher(url);

        if (matcher.find()){
            BV.append(matcher.group(1));
        } else {
            System.out.println("未检测到BV");
        }

        swapChars(BV, 3, 9);
        swapChars(BV, 4, 7);
        BV.delete(0, 3);

        long temp = 0;

        for(int i = 0; i < BV.length(); i++){
            char c = BV.charAt(i);
            int idx = Code_Table.indexOf(c);
            temp = temp * 58 + idx;
        }

        temp &= ((1L << 51) - 1);
        temp ^= XOR_CODE;

        return String.valueOf(temp);
    }

    public String getCid(String avid){
        String videoInfoUrl = "https://api.bilibili.com/x/web-interface/view?aid=" + avid;

        HttpHeaders headers = new HttpHeaders();
        List<String> cookies = new ArrayList<>();
        List<String> userAgent = new ArrayList<>();
        List<String> referer = new ArrayList<>();
        cookies.add("SESSDATA=" + "86eaebba%2C1731901940%2Cc0a3d%2A51CjDGoGHnvKZtiHow-5w-fGFaZA38INjbrPeKvfXZ2d2n0hWyW71tHKbkcGrgfnguZdISVk5CdGdkMEp2ZmhJSU5tWVZMZnZqOWx3MTkyYmJ5QXZzZVdoZW9EOGVmUUtGREhCT19uLU5BYWlfX2J2MHBiSUNRTi1SUXBMbzRzeFNjNnpRem5EekVRIIEC");
        userAgent.add("Mozilla/5.0 (Windows NT 6.3;Win64;x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        referer.add("https://www.bilibili.com");
        headers.put(HttpHeaders.COOKIE, cookies);
        headers.put(HttpHeaders.USER_AGENT, userAgent);
        headers.put(HttpHeaders.REFERER, referer);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        Class<VideoInfo> responseType1 = VideoInfo.class;
        ResponseEntity<VideoInfo> responseEntity1 = restTemplate.exchange(
                videoInfoUrl,
                HttpMethod.GET,
                httpEntity,
                responseType1
        );

        VideoInfo body = responseEntity1.getBody();

        if (body != null) {
            return body.getData().getCid();
        }
        return "";
    }

    public BangumiInfo getBangumiInfo(String url){
        String epId = "";
        String regexEpId = "/ep([^?]+)";
        Pattern patternEpId = Pattern.compile(regexEpId);
        Matcher matcher = patternEpId.matcher(url);

        if (matcher.find()){
            epId = matcher.group(1);
        } else {
            System.out.println("未检测到epId");
        }

        HttpHeaders headers = new HttpHeaders();
        List<String> cookies = new ArrayList<>();
        List<String> userAgent = new ArrayList<>();
        List<String> referer = new ArrayList<>();
        cookies.add("SESSDATA=" + "86eaebba%2C1731901940%2Cc0a3d%2A51CjDGoGHnvKZtiHow-5w-fGFaZA38INjbrPeKvfXZ2d2n0hWyW71tHKbkcGrgfnguZdISVk5CdGdkMEp2ZmhJSU5tWVZMZnZqOWx3MTkyYmJ5QXZzZVdoZW9EOGVmUUtGREhCT19uLU5BYWlfX2J2MHBiSUNRTi1SUXBMbzRzeFNjNnpRem5EekVRIIEC");
        userAgent.add("Mozilla/5.0 (Windows NT 6.3;Win64;x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        referer.add("https://www.bilibili.com");
        headers.put(HttpHeaders.COOKIE, cookies);
        headers.put(HttpHeaders.USER_AGENT, userAgent);
        headers.put(HttpHeaders.REFERER, referer);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        String bangumiInfoUrl = "https://api.bilibili.com/pgc/view/web/season?ep_id=" + epId;

        Class<BangumiInfo> responseType1 = BangumiInfo.class;
        ResponseEntity<BangumiInfo> responseEntity1 = restTemplate.exchange(
                bangumiInfoUrl,
                HttpMethod.GET,
                httpEntity,
                responseType1
        );
        BangumiInfo body = responseEntity1.getBody();

        return body;
    }

    private static void swapChars(StringBuilder str, int idx1, int idx2) {
        char temp = str.charAt(idx1);
        str.setCharAt(idx1, str.charAt(idx2));
        str.setCharAt(idx2, temp);
    }
}
