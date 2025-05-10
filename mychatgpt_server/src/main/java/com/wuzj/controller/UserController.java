package com.wuzj.controller;

import com.wuzj.entity.User;
import com.wuzj.service.UserService;
import com.wuzj.utils.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuzj
 * @date 2023/8/29
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    public static ConcurrentHashMap<String, User> loginUser = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Long> loginUserKey = new ConcurrentHashMap<>();
    @RequestMapping("/login")
    public R login(String username, String password) {
        if (username == null) return R.fail("必须填写用户名");


        User user = userService.queryByName(username);
        if (user == null) return R.fail("用户名不存在");
        String targetPassword = user.getPassword();
        if (targetPassword == null) return R.fail("用户密码异常");
        if (!targetPassword.equals(password)) return R.fail("密码错误");

        loginUser.put(username, user);
        loginUserKey.put(username, System.currentTimeMillis());
        return R.ok(String.valueOf(loginUserKey.get(username)));
    }

    @RequestMapping("/logout")
    public R logout(String username) {
        loginUser.remove(username);
        loginUserKey.remove(username);
        return R.ok();
    }

    @RequestMapping("/checkUserKey")
    public R checkUserKey(String username, Long key){
        if (username==null || key == null)return R.fail("用户校验异常");
        if (!Objects.equals(loginUserKey.get(username), key)){
            return R.fail("用户在其他地方登录！！！");
        }
        return R.ok();
    }

    @RequestMapping("/loginUser")
    public R loginUser(){
        return R.ok("success",loginUser.keySet());
    }
}
