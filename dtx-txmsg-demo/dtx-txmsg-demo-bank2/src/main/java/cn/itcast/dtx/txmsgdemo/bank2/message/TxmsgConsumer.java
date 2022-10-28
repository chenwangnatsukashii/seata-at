package cn.itcast.dtx.txmsgdemo.bank2.message;

import cn.itcast.dtx.txmsgdemo.bank2.constant.MQConstant;
import cn.itcast.dtx.txmsgdemo.bank2.message.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank2.service.AccountInfoService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = MQConstant.group1, topic = MQConstant.topic1)
public class TxmsgConsumer implements RocketMQListener<String> {

    @Resource
    private AccountInfoService accountInfoService;

    // 接收消息
    @Override
    public void onMessage(String message) {
        log.info("开始消费消息：{}", message);
        // 解析消息
        JSONObject jsonObject = JSONObject.parseObject(message);
        String accountChangeString = jsonObject.getString("accountChange");
        // 转成AccountChangeEvent
        AccountChangeEvent accountChangeEvent = JSONObject.parseObject(accountChangeString, AccountChangeEvent.class);
        // 设置账号为李四的
        accountChangeEvent.setAccountNo("2");
        // 更新账户，增加金额
        accountInfoService.addAccountInfoBalance(accountChangeEvent);
    }
}
