package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 读取配置文件
 * @author hhr
 */
@Data
@ConfigurationProperties(prefix = "ly.upload")
public class UploadProperties {

    /**上传文件基本路径*/
    private String baseUrl;

    /**允许上传文件的类型*/
    private List<String> allowtypes;
}
