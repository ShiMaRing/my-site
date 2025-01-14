package com.xgs.service.log;

import com.xgs.model.LogDomain;
import com.github.pagehelper.PageInfo;

/**
 * 用户请求日志
 * Created by xgs on 2022/11/29.
 */
public interface LogService {

    /**
     * 添加
     * @param action
     * @param data
     * @param ip
     * @param authorId
     */
    void addLog(String action, String data, String ip, Integer authorId);

    /**
     * 删除日志
     * @param id
     * @return
     */
    void deleteLogById(Integer id);

    /**
     * 获取日志
     * @return
     */
    PageInfo<LogDomain> getLogs(int pageNum, int pageSize);
}
