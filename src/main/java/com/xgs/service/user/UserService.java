package com.xgs.service.user;

import com.xgs.model.UserDomain;

import org.springframework.stereotype.Service;
/**
 * Created by xgs on 2022/11/20.
 */
@Service
public interface UserService {

    /**
     * @Author: xgs
     * @Description: 更改用户信息
     * @Date: 2022/11/20
     * @param user
     */
    int updateUserInfo(UserDomain user);

    /**
     * @Author: xgs
     * @Description: 根据主键编号获取用户信息
     * @Date: 2022/11/20
     * @param uId 主键
     */
    UserDomain getUserInfoById(Integer uId);


    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return
     */
    UserDomain login(String username, String password);

}
