package com.example.bilibilivideostream.utils;

import java.io.IOException;
import java.util.List;

public class PowerShellUtils {
    public static void executePowerShellScript(String directoryPath, String scriptName, List<String> arguments){
        try {
            // PowerShell脚本的路径
            String scriptPath = directoryPath + "\\" + scriptName + ".ps1";

            // 构建PowerShell命令
            StringBuilder command = new StringBuilder();
            command.append("powershell.exe -ExecutionPolicy Bypass -File ").append(scriptPath);
            for (String argument : arguments) {
                command.append(" ").append(argument);
            }

            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command.toString());
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
