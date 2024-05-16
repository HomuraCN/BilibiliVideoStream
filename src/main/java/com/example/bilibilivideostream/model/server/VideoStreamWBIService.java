package com.example.bilibilivideostream.model.server;

import com.example.bilibilivideostream.model.response.BangumiInfo;
import com.example.bilibilivideostream.model.response.VideoStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class VideoStreamWBIService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WbiService wbiService;
    @Autowired
    private AvBvCvService avBvCvService;

    public void downloadVideo(String url, String fileName) {
        String directoryPath = "D:\\H\\Video\\BilibiliVideo";
        System.out.println(fileName);

        String avid = avBvCvService.bvToAv(url);
        String cid = avBvCvService.getCid(avid);

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
        cookies.add("SESSDATA=" + "aa2a80e9%2C1731147874%2C009c6%2A51CjApd7wlJsBSzj0v5lvQaaZOwj5IZCyhrC8byHqICOKCNkdjAhczWWErrZHZcnjaWHkSVldHVjdtX3BQMzhDMXFyQmwzNWlzWnJUOHJ1T0EzNG1kSHdRbVRZOUVnTFNwZkRVeEtrQUdtWmVCTXZhNGtyNVc0Y3RIWFlsZVBCZkEzZFBDU2psc0JnIIEC");
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

        List<VideoStream.ResponseData.Dash.Video> videoList = body.getData().getDash().getVideoList();
        List<VideoStream.ResponseData.Dash.Audio> audioList = body.getData().getDash().getAudioList();

        Collections.sort(videoList, new Comparator<VideoStream.ResponseData.Dash.Video>() {
            @Override
            public int compare(VideoStream.ResponseData.Dash.Video o1, VideoStream.ResponseData.Dash.Video o2) {
                return Integer.parseInt(o2.getBandwidth()) - Integer.parseInt(o1.getBandwidth());
            }
        });

        Collections.sort(audioList, new Comparator<VideoStream.ResponseData.Dash.Audio>() {
            @Override
            public int compare(VideoStream.ResponseData.Dash.Audio o1, VideoStream.ResponseData.Dash.Audio o2) {
                return Integer.parseInt(o2.getBandwidth()) - Integer.parseInt(o1.getBandwidth());
            }
        });

        String videoStreamUrl = videoList.get(0).getBaseUrl();
        String audioStreamUrl = audioList.get(0).getBaseUrl();

        ResponseEntity<byte[]> videoResponse = restTemplate.exchange(
                videoStreamUrl,
                HttpMethod.GET,
                httpEntity,
                byte[].class
        );

        byte[] file = videoResponse.getBody();
        try (FileOutputStream fos = new FileOutputStream(directoryPath + "\\videoStream.m4s")) {
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
        try (FileOutputStream fos = new FileOutputStream(directoryPath + "\\audioStream.m4s")) {
            fos.write(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            // PowerShell脚本的路径
            String scriptPath = directoryPath + "\\ffmpegShell.ps1";

            // 脚本参数
            String argument1 = fileName;

            // 构建PowerShell命令
            String command = "powershell.exe -ExecutionPolicy Bypass -File " + scriptPath + " " + argument1;

            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // 获取命令执行的输出
            java.io.InputStream is = process.getInputStream();
            int i;
            char c;
            StringBuilder output = new StringBuilder();
            while ((i = is.read()) != -1) {
                c = (char) i;
                output.append(c);
            }

            // 打印输出
            System.out.println(output.toString());

            // 等待命令执行完成
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void downloadVideoByAidCid(String avid, String cid, String fileName) {
        String directoryPath = "D:\\H\\Video\\BilibiliVideo";
        System.out.println(fileName);

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
        cookies.add("SESSDATA=" + "aa2a80e9%2C1731147874%2C009c6%2A51CjApd7wlJsBSzj0v5lvQaaZOwj5IZCyhrC8byHqICOKCNkdjAhczWWErrZHZcnjaWHkSVldHVjdtX3BQMzhDMXFyQmwzNWlzWnJUOHJ1T0EzNG1kSHdRbVRZOUVnTFNwZkRVeEtrQUdtWmVCTXZhNGtyNVc0Y3RIWFlsZVBCZkEzZFBDU2psc0JnIIEC");
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

        List<VideoStream.ResponseData.Dash.Video> videoList = body.getData().getDash().getVideoList();
        List<VideoStream.ResponseData.Dash.Audio> audioList = body.getData().getDash().getAudioList();

        Collections.sort(videoList, new Comparator<VideoStream.ResponseData.Dash.Video>() {
            @Override
            public int compare(VideoStream.ResponseData.Dash.Video o1, VideoStream.ResponseData.Dash.Video o2) {
                return Integer.parseInt(o2.getBandwidth()) - Integer.parseInt(o1.getBandwidth());
            }
        });

        Collections.sort(audioList, new Comparator<VideoStream.ResponseData.Dash.Audio>() {
            @Override
            public int compare(VideoStream.ResponseData.Dash.Audio o1, VideoStream.ResponseData.Dash.Audio o2) {
                return Integer.parseInt(o2.getBandwidth()) - Integer.parseInt(o1.getBandwidth());
            }
        });

        String videoStreamUrl = videoList.get(0).getBaseUrl();
        String audioStreamUrl = audioList.get(0).getBaseUrl();

        ResponseEntity<byte[]> videoResponse = restTemplate.exchange(
                videoStreamUrl,
                HttpMethod.GET,
                httpEntity,
                byte[].class
        );

        byte[] file = videoResponse.getBody();
        try (FileOutputStream fos = new FileOutputStream(directoryPath + "\\videoStream.m4s")) {
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
        try (FileOutputStream fos = new FileOutputStream(directoryPath + "\\audioStream.m4s")) {
            fos.write(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            // PowerShell脚本的路径
            String scriptPath = directoryPath + "\\ffmpegShell.ps1";

            // 脚本参数
            String argument1 = fileName;

            // 构建PowerShell命令
            String command = "powershell.exe -ExecutionPolicy Bypass -File " + scriptPath + " " + argument1;

            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // 获取命令执行的输出
            java.io.InputStream is = process.getInputStream();
            int i;
            char c;
            StringBuilder output = new StringBuilder();
            while ((i = is.read()) != -1) {
                c = (char) i;
                output.append(c);
            }

            // 打印输出
            System.out.println(output.toString());

            // 等待命令执行完成
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void downloadBangumi(String url){
        BangumiInfo bangumiInfo = avBvCvService.getBangumiInfo(url);
        List<BangumiInfo.ResponseData.Episodes> episodesList = bangumiInfo.getResult().getEpisodesList();
        for(int i = 0; i < episodesList.size(); i++) {
            downloadVideoByAidCid(episodesList.get(i).getAid(),
                    episodesList.get(i).getCid(), "ep" + String.valueOf(i + 1));
        }
    }
}
