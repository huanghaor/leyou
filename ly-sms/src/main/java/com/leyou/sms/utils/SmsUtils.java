package com.leyou.sms.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public  class SmsUtils {

    @Autowired
    private SmsProperties prop;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**产品名称:云通信短信API产品,开发者无需替换*/
    private static final String PRODUCT = "Dysmsapi";

    /**产品域名,开发者无需替换*/
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsUtils.class);

    private static final String KEY_PREFIX="sms:phone:";

    /**设置redis存储时效*/
    private static final Long SMS_MIN_INTERVAL_IN_MILLIS = 60000L;

    public SendSmsResponse sendSms(String phone, String code, String signName, String template) throws ClientException {
        // 按照手机号码限流
        String key = KEY_PREFIX+phone;
        String lastime = redisTemplate.opsForValue().get(key);
        if(StringUtils.isNoneBlank(lastime)){
            Long last = Long.valueOf(lastime);
            if(System.currentTimeMillis()-last < SMS_MIN_INTERVAL_IN_MILLIS){
                log.info("【短信服务】  短信发送频率过高，被拦截   手机号{}"+phone);
                return null;
            }
        }

        try{

            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                    prop.getAccessKeyId(), prop.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            request.setMethod(MethodType.POST);
            //必填:待发送手机号
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(template);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            request.setTemplateParam("{\"code\":\"" + code + "\"}");

            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            request.setOutId("123456");

            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);


            //返回状态码
            LOGGER.info("发送短信状态：{}", sendSmsResponse.getCode());
            //失败原因
            LOGGER.info("发送短信消息：{}", sendSmsResponse.getMessage());

            LOGGER.info("【短信微服务】  发送短信验证码，手机号{} "+phone);

            // 发送短信成功后，将验证码写入redis,以后设置时效后进行后台验证码验证
            redisTemplate.opsForValue().set(key,String.valueOf(System.currentTimeMillis()));
            return sendSmsResponse;
        }catch (Exception e){
            LOGGER.error("【短信服务】 发送短信异常，手机号码为",phone,e);
            return null;
        }

    }
}
