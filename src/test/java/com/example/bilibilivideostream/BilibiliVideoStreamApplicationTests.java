package com.example.bilibilivideostream;

import com.example.bilibilivideostream.model.javabean.Cookie;
import com.example.bilibilivideostream.model.response.VideoStream;
import com.example.bilibilivideostream.model.server.AvBvCvService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BilibiliVideoStreamApplicationTests {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AvBvCvService avBvCvService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void testDownload() {
        String avid = "326022832";
        String cid = "1401252638";
        String streamUrl = "https://api.bilibili.com/x/player/playurl?avid=" + avid + "&cid=" + cid + "&qn=112&fnval=16&fourk=1";

        HttpHeaders headers = new HttpHeaders();
        List<String> cookies = new ArrayList<>();
        List<String> userAgent = new ArrayList<>();
        List<String> referer = new ArrayList<>();
        cookies.add("SESSDATA=" + "580f2776%2C1723815781%2C9c816%2A21CjDAd_1s9yhjBChrL6tQ_4GL6AHWTikpBHGOEWnaO2XOjw_GTeA4KrrDlOJcFRZSvO8SVm82Z1FkNWwteWlZMUJRVzVBUmhTd0ZyTU5hR0JxT0NCZUZPbXpoN2pBUHBET0dpcERMSkFkMlZrTDRwbW9wYnczY2VRQlRsbXVXNjRoUktRazVXSW53IIEC");
        userAgent.add("Mozilla/5.0 (Windows NT 6.3;Win64;x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        referer.add("https://www.bilibili.com");
        headers.put(HttpHeaders.COOKIE, cookies);
        headers.put(HttpHeaders.USER_AGENT, userAgent);
        headers.put(HttpHeaders.REFERER, referer);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        Class<VideoStream> responseType1 = VideoStream.class;
        ResponseEntity<VideoStream> responseEntity1 = restTemplate.exchange(
                streamUrl,
                HttpMethod.GET,
                httpEntity,
                responseType1
        );

        VideoStream body = responseEntity1.getBody();
        String videoStreamUrl = body.getData().getDash().getVideoList().get(0).getBaseUrl();
        String audioStreamUrl = body.getData().getDash().getAudioList().get(0).getBaseUrl();

        ResponseEntity<byte[]> videoResponse = restTemplate.exchange(
                videoStreamUrl,
                HttpMethod.GET,
                httpEntity,
                byte[].class
        );

        byte[] file = videoResponse.getBody();
        try (FileOutputStream fos = new FileOutputStream("D:\\G\\无能小OP\\更新\\videoStream.m4s")) {
            fos.write(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ResponseEntity<byte[]> audioResponse = restTemplate.exchange(
                audioStreamUrl,
                HttpMethod.GET,
                httpEntity,
                byte[].class
        );

        file = audioResponse.getBody();
        try (FileOutputStream fos = new FileOutputStream("D:\\G\\无能小OP\\更新\\audioStream.m4s")) {
            fos.write(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRegex(){
        String s = avBvCvService.bvToAv("https://www.bilibili.com/video/BV1g1421B7FR/?spm_id_from=333.1007.tianma.1-1-1.click");
        System.out.println(s);
    }

    @Test
    void testCid(){
        String cid = avBvCvService.getCid("1554707486");
        System.out.println(cid);
    }

    @Test
    void testBangumiInfo(){
        System.out.println(avBvCvService.getBangumiInfo("https://www.bilibili.com/bangumi/play/ep247620?spm_id_from=333.1007.top_right_bar_window_history.content.click&from_spmid=666.25.episode.0"));
    }

    @Test
    void insertCookie(){
        Cookie cookie = new Cookie();
        cookie.setMid(27842728);
        cookie.setSessData("437e145e%2C1734073520%2C3363a%2A61CjC17wI7wyQann0QJeJCS9ig2lsJGP8llFANL67Dcm4gsOwJKGKniCuI1FwptAiJLg0SVm1va3BFa1o1eldmbVJwODhTWGpVbGU0cElPbXNzdHY0SFY2NHhhVmt1T0JpWDFhemFXU1NjeE9zbUFtUTliekN2OEY2c3ZnWVp5Qk1LRnE1V0dlQTRBIIEC");
        cookie.setBiliJct("a3ff2de40f06b68e2608fc9bfe939bf6");
        mongoTemplate.insert(cookie);
    }
}
