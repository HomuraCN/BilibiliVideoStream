package com.example.bilibilivideostream.model.server;

import com.example.bilibilivideostream.model.javabean.Cookie;
import com.example.bilibilivideostream.model.response.BangumiInfo;
import com.example.bilibilivideostream.model.response.VideoStream;
import com.example.bilibilivideostream.utils.PowerShellUtils;
import com.example.bilibilivideostream.utils.RestTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VideoStreamWBIService {
    @Autowired
    private WbiService wbiService;
    @Autowired
    private AvBvCvService avBvCvService;
    @Autowired
    private RestTemplateUtils restTemplateUtils;
    @Autowired
    private CookieService cookieService;
    @Value("${BILIBILI.MID}")
    private int MID;
    private final String directoryPath = "D:\\H\\Video\\BilibiliVideo";

    public void downloadVideoByAidCid(String avid, String cid, String fileName) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("avid", avid);
        map.put("cid", cid);
        map.put("qn", "0");
        map.put("fnval", "80");
        map.put("fnver", "0");
        map.put("fourk", "1");

        String wbiUrl = wbiService.getWbiUrl(map);

        String streamUrl = "https://api.bilibili.com/x/player/wbi/playurl?" + wbiUrl;

        HttpHeaders headers = new HttpHeaders();
        List<String> cookies = new ArrayList<>();
        List<String> userAgent = new ArrayList<>();
        List<String> referer = new ArrayList<>();

        Cookie cookie = new Cookie();
        cookie.setMid(MID);
        cookies.add("SESSDATA=" + cookieService.findOneCookieByMid(cookie).getSessData());
        userAgent.add("Mozilla/5.0 (Windows NT 6.3;Win64;x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        referer.add("https://www.bilibili.com");
        headers.put(HttpHeaders.COOKIE, cookies);
        headers.put(HttpHeaders.USER_AGENT, userAgent);
        headers.put(HttpHeaders.REFERER, referer);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        VideoStream body = restTemplateUtils.getResponseEntity(streamUrl, httpEntity, VideoStream.class);

        List<VideoStream.ResponseData.Dash.Video> videoList = body.getData().getDash().getVideoList();
        List<VideoStream.ResponseData.Dash.Audio> audioList = body.getData().getDash().getAudioList();

        Collections.sort(videoList, (o1, o2) -> {
            int res = Integer.parseInt(o2.getId()) - Integer.parseInt(o1.getId());
            if(res != 0){
                return res;
            }
            else {
                return Integer.parseInt(o2.getBandwidth()) - Integer.parseInt(o1.getBandwidth());
            }
        });

        Collections.sort(audioList, (o1, o2) -> Integer.parseInt(o2.getBandwidth()) - Integer.parseInt(o1.getBandwidth()));

        String videoStreamUrl = videoList.get(0).getBaseUrl();
        String audioStreamUrl = audioList.get(0).getBaseUrl();

        restTemplateUtils.downloadFileFromUrl(videoStreamUrl, headers, directoryPath, "videoStream.m4s");
        restTemplateUtils.downloadFileFromUrl(audioStreamUrl, headers, directoryPath, "audioStream.m4s");

        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<String> arguments = new ArrayList<>();
        arguments.add(fileName);
        PowerShellUtils.executePowerShellScript(directoryPath, "ffmpegShell", arguments);
    }

    public void downloadSingleVideo(String url, String fileName){
        System.out.println(fileName);
        String avid = avBvCvService.bvToAv(url);
        String cid = avBvCvService.getCid(avid);
        downloadVideoByAidCid(avid, cid, fileName);
    }

    public void downloadBangumi(String url, int l, int r){
        BangumiInfo bangumiInfo = avBvCvService.getBangumiInfo(url);
        List<BangumiInfo.ResponseData.Episodes> episodesList = bangumiInfo.getResult().getEpisodesList();
        for(int i = 0; i < episodesList.size(); i++) {
            int ep = i + 1, num = 2;
//            [1-num]
//            if(ep > num) break;
//            [num-end]
//            if(ep < num) continue;
            if(ep < l) continue;
            if(ep > r) break;
            System.out.println("ep" + String.valueOf(i + 1));
            downloadVideoByAidCid(episodesList.get(i).getAid(),
                    episodesList.get(i).getCid(), "ep" + String.valueOf(i + 1));
        }
    }
}
