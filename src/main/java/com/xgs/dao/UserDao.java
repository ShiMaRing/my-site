package com.xgs.dao;

import com.xgs.model.UserDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by xgs on 2022/11/20.
 */
@Mapper
public interface UserDao {

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
    UserDomain getUserInfoById(@Param("uid") Integer uId);

    /**
     * 根据用户名和密码获取用户信息
     * @param username
     * @param password
     * @return
     */
    UserDomain getUserInfoByCond(@Param("username") String username, @Param("password") String password);

}
