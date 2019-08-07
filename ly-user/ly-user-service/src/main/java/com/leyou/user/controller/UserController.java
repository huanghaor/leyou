package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据
     * @param data 数据类容
     * @param type 数据类型
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data, @PathVariable(value = "type") Integer type) {
        Boolean boo = this.userService.checkData(data, type);
        if (boo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(boo);
    }

    /**
     * 发送短信
     * @param phone 电话号码
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        userService.sendVerifyCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 注册
     * @param user
     * @param code
     * @return
     * BindingResult result  是为了自定义后台校验后返回给前端的数据；如果不添加这个将返回的是spring自己创建的返回参数形式
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code") String code) {
        //自定义错误信息
        if(result.hasErrors()){
            throw new RuntimeException(result.getFieldErrors().stream().
                    map(e ->e.getDefaultMessage()).collect(Collectors.joining("|")));
        }
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 根据用户名密码查询用户信息
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        return ResponseEntity.ok(userService.queryUser(username, password));
    }
}
