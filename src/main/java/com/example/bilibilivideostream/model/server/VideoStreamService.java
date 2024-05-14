package com.example.bilibilivideostream.model.server;

import com.example.bilibilivideostream.model.response.VideoStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class VideoStreamService {
    @Autowired
    private RestTemplate restTemplate;

    public void downloadVideo(String avid, String cid, String fileName) {
        String directoryPath = "D:\\H\\Video\\BilibiliVideo";
        System.out.println(fileName);
        String streamUrl = "https://api.bilibili.com/x/player/playurl?avid=" + avid + "&cid=" + cid + "&qn=0&fnval=80&fnver=0&fourk=1";

        HttpHeaders headers = new HttpHeaders();
        List<String> cookies = new ArrayList<>();
        List<String> userAgent = new ArrayList<>();
        List<String> referer = new ArrayList<>();
        cookies.add("SESSDATA=" + "0e8a14fd%2C1730098942%2C57e15%2A51CjCxZ4LG7pNJnG1vVz9N4L2v2bigQNVO4tktFnABS_qKUmjxW6NmCm5DQucQzc26Ut0SVlpic0ZESWxJaVVnMS1JSFROMHFOR251bUNCUFNmM1draC0wUjk3TmxHOVVNRDFVcUZzNUlNRW5TX2M5Vi1TTVpkdmh6dHBrSVFPSU5PS0pYUl9QM1hBIIEC");
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
            Thread.currentThread().sleep(2000);
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
}
