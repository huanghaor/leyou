package com.lygateway.config;

import com.leyou.util.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * 获取配置文件信息
 */
@Data
@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {

    /**公钥*/
    private String pubKeyPath;

    private String cookie_name;

    private PublicKey  publicKey;

    /**
     * @PostContruct：在构造方法执行之后执行该方法
     */
    @PostConstruct
    public void init(){
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);

    }

}

