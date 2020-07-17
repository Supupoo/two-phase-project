package com.aaa.yay.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author yay
 * @Description Ftp上传工具类
 * @CreatTime 2020年 07月13日 星期一 21:07:33
 */
public class FtpUtils {

    private FtpUtils(){

    }

    /**
    * 上传方法
     *      *      host:ftp服务器的ip地址
     *      *      port:ftp服务器的端口号
     *      *      username:ftp服务器的用户名
     *      *      password:ftp服务器的密码
     *      *      basePath:用户上传文件的根路径
     *      *      filePath:用户上传当天日期路径
     *      *      fileName:修改之后的文件名
     *      *      inputStream:使用IO上传
    * @author yay
    * @param host port username password basePath filePath filename inputStream
    * @updateTime 2020/07/13 21:36
    * @throws
    * @return java.lang.Boolean
    */
    public static Boolean upload(String host, Integer port, String username, String password, String basePath,
                                 String filePath, String filename, InputStream inputStream){
        /**
         * 按照每天的日期作为文件夹来进行上传
         */
        // 1.创建临时路径
        String tempPath = "";
        // 2.创建FTPClient对象(这个对象就是FTP给java所提供的API)
        FTPClient ftpClient = new FTPClient();
        // 3.定义返回状态码
        int replyCode;
        // 4.连接ftp
        try {
            ftpClient.connect(host,port);
            // 5.登录ftp服务器
            ftpClient.login(username,password);
            // 6.接收返回的状态码 如果成功返回230，如果失败返回503
            replyCode = ftpClient.getReplyCode();
            // 7.判断
            if (!FTPReply.isPositiveCompletion(replyCode)){
                // 连接失败了
                ftpClient.disconnect();
                return false;
            }
            // 8.先检测我要上传的目录是否存在(2020/07/13)
            // basePath:/home/ftp
            // filePath: /2020/07/10
            // --->/home/ftp/2020/07/13
            if (!ftpClient.changeWorkingDirectory(basePath + filePath)){
                // 该文件夹不存在
                // 9.创建文件夹
                // [2020, 07, 13] ["", "2020", "07", "13"]
                String[] dirs = filePath.split("/");
                // 10.把basePath赋值给临时路径
                // tempPath:/home/ftp
                tempPath = basePath;
                // 11.循环
                for (String dir : dirs){
                    // 严谨判断--->获取当前循环出来的String类型文件夹地址(2020)
                    if (null == dir || "".equals(dir)){
                        // 没有截取到数据，在这不能直接return，因为循环还没有结束，continue是跳过当前循环，进入下一次循环
                        continue;
                    }
                    // 12.说明有数据，拼接临时路径(如果没有进入if，则取到的值应该就是2020)
                    tempPath += "/" + dir;
                    // tempPath: /home/ftp/2020
                    // 13.再次检测tempPath是否存在
                    if (!ftpClient.changeWorkingDirectory(tempPath)){
                        // 文件夹不存在
                        // 14.创建文件夹
                        if (!ftpClient.makeDirectory(tempPath)){
                            // 说明文件夹创建失败
                            return false;
                        }else {
                            // 15.严谨判断，判断创建出来的目录确实存在
                            ftpClient.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            // 16.把文件转换为二进制的形式来进行上传
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // 17.这里才是真正的文件上传
            if (!ftpClient.storeFile(filename,inputStream)){
                // 说明上传失败
                return false;
            }
            // 18.关闭输入流
            inputStream.close();
            // 19.退出ftp
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // 20.判断当前ftp服务器是否处于正在连接状态
            if (ftpClient.isConnected()){
                try {
                    // 说明还在连接中(说明正在占用资源)
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
