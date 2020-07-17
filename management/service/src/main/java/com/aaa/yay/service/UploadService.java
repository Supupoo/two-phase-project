package com.aaa.yay.service;

import com.aaa.yay.properties.FtpProperties;
import com.aaa.yay.utils.FileNameUtils;
import com.aaa.yay.utils.FtpUtils;
import org.apache.commons.httpclient.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

import static com.aaa.yay.staticproperties.TimeFormatProperties.*;


/**
 * @Author yay
 * @Description Ftp上传业务
 * @CreatTime 2020年 07月13日 星期一 21:43:18
 */
@Service
public class UploadService {

    @Autowired
    private FtpProperties ftpProperties;


    public Boolean upload(MultipartFile file){
        // 1.获取文件的远程名称(为了获取后缀名)
        String oldFileName = file.getOriginalFilename();
        // 2.生成新的文件名
        String newFileName = FileNameUtils.getFileName();
        // 3.截取后缀名，拼接到新的文件名上
        newFileName += oldFileName.substring(oldFileName.lastIndexOf("."));
        // 4.获取文件的上传路径(2020/07/13)
        String filePath = DateUtil.formatDate(new Date(), DATE_FORMAT);
        // 5.调用文件上传工具类
        try {
            return FtpUtils.upload(ftpProperties.getHost(),ftpProperties.getPort(),ftpProperties.getUsername(),
                    ftpProperties.getPassword(),ftpProperties.getBasePath(),filePath,newFileName,file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
