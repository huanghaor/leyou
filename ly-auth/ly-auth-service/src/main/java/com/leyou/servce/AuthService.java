package com.leyou.servce;

import com.leyou.client.UserClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.config.JwtProperties;
import com.leyou.entity.UserInfo;
import com.leyou.user.pojo.User;
import com.leyou.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties properties;
    /**
     * 登录授权
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    public String login(String username, String password) {
        try {
            //校验账号和密码
            User user = userClient.queryUser(username, password);
            if (user == null) {
                throw new LyException(ExceptionEnum.USER_NOT_FOUND);
            }
            //生成token
            // 如果有查询结果，则根据秘钥生成token，然后在登录授权认证时使用公钥进行解密
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username),
                    properties.getPrivateKey(), properties.getExpire());
            return token;
        }catch (Exception e){
            log.error("【授权中心】 生成token失败，用户名称："+username);
            throw new LyException(ExceptionEnum.CREATE_TOKEN_ERROR);
        }
    }
}
