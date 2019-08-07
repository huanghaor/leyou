package com.leyou.user.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * MD5加密算法
 */
@Component
public class CodecUtils {

    /**
     * MD5加密
     * @param data 参数
     * @param salt 盐
     * @return
     */
    public static String md5Hex(String data,String salt){
        if(StringUtils.isBlank(salt)){
            salt = data.hashCode()+"";
        }
        //双重加密
        return DigestUtils.md5Hex(salt+DigestUtils.md5Hex(data));
    }

    /**
     * sha512加密
     * @param data
     * @param salt
     * @return
     */
    public static String shaHex(String data,String salt){
        if(StringUtils.isBlank(salt)){
            salt = data.hashCode()+"";
        }
        return DigestUtils.sha512Hex(salt+DigestUtils.sha512Hex(data));
    }

    /**
     * 系统自动设置盐值
     * @return 盐值
     */
    public static String generateSalt(){
        return StringUtils.replace(UUID.randomUUID().toString(),"-","");
    }

}
