package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.controller.uploadController;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * 文件上传
 * @author hhr
 */
@Service
@Slf4j
@EnableConfigurationProperties( UploadProperties.class)
public class UploadService {

    private static final Logger logger = LoggerFactory.getLogger(uploadController.class);

    /** 支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");*/

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Autowired
    private  UploadProperties uploadProperties;
    /**
     * 保存图片
     * @param file 上传文件
     * @return
     */
    public String uploadImage(MultipartFile file) {
        try {
            // 1、图片信息校验
            // 1)校验文件类型
            String type = file.getContentType();
            if (!uploadProperties.getAllowtypes().contains(type)) {
                logger.info("上传失败，文件类型不匹配：{}", type);
                throw new LyException(ExceptionEnum.INVLID_FILE_TYPE);
            }
            // 2)校验图片内容E
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                logger.info("上传失败，文件内容不符合要求");
                throw new LyException(ExceptionEnum.INVLID_FILE_TYPE);
            }
            //保存图片只FastDFS中，linux
            //获取后缀名
            /**String extension =file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);*/
            String extension = StringUtils.substringAfter(file.getOriginalFilename(),".");
            // 上传并且生成缩略图
            StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(
                    file.getInputStream(), file.getSize(), extension, null);
            // 带分组的路径
            System.out.println(storePath.getFullPath());
            // 不带分组的路径
            System.out.println(storePath.getPath());
            // 获取缩略图路径
            String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
            System.out.println(path);
            // 2.3、拼接图片地址
            String url = uploadProperties.getBaseUrl() + storePath.getFullPath();

            return url;
        } catch (Exception e) {
            logger.error("文件上传失败",e);
            throw  new LyException(ExceptionEnum.UPLOAD_FAIL_ERROR);
        }
    }
}
