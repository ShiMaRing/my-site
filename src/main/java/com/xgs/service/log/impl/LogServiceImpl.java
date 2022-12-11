package com.xgs.service.log.impl;

import com.qcloud.cos.utils.Jackson;
import com.xgs.constant.ErrorConstant;
import com.xgs.dao.LogDao;
import com.xgs.exception.BusinessException;
import com.xgs.model.LogDomain;
import com.xgs.service.log.LogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

/**
 * 请求日志
 * Created by xgs on 2022/11/29.
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogDao logDao;

    //send to kafka
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void addLog(String action, String data, String ip, Integer authorId) {
        LogDomain logDomain = new LogDomain();
        logDomain.setAuthorId(authorId);
        logDomain.setIp(ip);
        logDomain.setData(data);
        logDomain.setAction(action);
        String msg = Jackson.toJsonString(action);
        kafkaTemplate.send("log", msg).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                // 发送失败的处理
                System.out.printf("发送日志 %s 失败\n", msg);
            }

            @Override
            public void onSuccess(SendResult<String, String> stringObjectSendResult) {
                // 成功的处理
                System.out.printf("发送日志 %s 成功\n", msg);
            }
        });
        logDao.addLog(logDomain);
    }

    @Override
    public void deleteLogById(Integer id) {
        if (null == id)
            throw BusinessException.withErrorCode(ErrorConstant.Common.PARAM_IS_EMPTY);
        logDao.deleteLogById(id);
    }

    @Override
    public PageInfo<LogDomain> getLogs(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<LogDomain> logs = logDao.getLogs();
        PageInfo<LogDomain> pageInfo = new PageInfo<>(logs);
        return pageInfo;
    }
}
