package cn.itcast.dtx.txmsgdemo.bank1.service.impl;

import cn.itcast.dtx.txmsgdemo.bank1.constant.MQConstant;
import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank1.service.AccountInfoService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Resource
    private AccountInfoDao accountInfoDao;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    // 向MQ发送转账消息
    @Override
    public void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountChange", accountChangeEvent);
        String jsonString = jsonObject.toJSONString();
        Message<String> message = MessageBuilder.withPayload(jsonString).build();

        // 发送一条事务消息
        rocketMQTemplate.sendMessageInTransaction(MQConstant.group1, MQConstant.topic1, message, null);
    }

    // 更新账户，扣减金额
    @Override
    @Transactional
    public void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {

        // 幂等判断
        if (accountInfoDao.isExistTx(accountChangeEvent.getTxNo()) > 0) {
            return;
        }
        // 扣减金额
        accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(), -accountChangeEvent.getAmount());
        // 添加事务的日志，幂等
        accountInfoDao.addTx(accountChangeEvent.getTxNo());
    }
}
