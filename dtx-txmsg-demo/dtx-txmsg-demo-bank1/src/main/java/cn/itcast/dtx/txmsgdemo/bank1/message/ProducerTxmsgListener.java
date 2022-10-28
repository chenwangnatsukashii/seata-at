package cn.itcast.dtx.txmsgdemo.bank1.message;

import cn.itcast.dtx.txmsgdemo.bank1.constant.MQConstant;
import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank1.service.AccountInfoService;
import com.alibaba.fastjson2.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RocketMQTransactionListener(txProducerGroup = MQConstant.group1)
public class ProducerTxmsgListener implements RocketMQLocalTransactionListener {

    @Resource
    private AccountInfoService accountInfoService;
    @Resource
    private AccountInfoDao accountInfoDao;

    // 事务消息发送后的回调方法，当消息发送给MQ成功，此方法被回调
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {

        try {
            // 解析message，转成AccountChangeEvent
            String messageString = new String((byte[]) message.getPayload());
            JSONObject jsonObject = JSONObject.parseObject(messageString);
            String accountChange = jsonObject.getString("accountChange");

            // 将accountChange（json）转成AccountChangeEvent
            AccountChangeEvent accountChangeEvent = JSONObject.parseObject(accountChange, AccountChangeEvent.class);
            // 执行本地事务，扣减金额
            accountInfoService.doUpdateAccountBalance(accountChangeEvent);
        } catch (Exception e) {
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;

    }

    // 如果未收到ack，就要事务状态回查，查询是否扣减金额
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        // 解析message，转成AccountChangeEvent
        String messageString = new String((byte[]) message.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(messageString);
        String accountChange = jsonObject.getString("accountChange");

        // 将accountChange（json）转成AccountChangeEvent
        AccountChangeEvent accountChangeEvent = JSONObject.parseObject(accountChange, AccountChangeEvent.class);
        int existTx = accountInfoDao.isExistTx(accountChangeEvent.getTxNo());
        if (existTx > 0) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.UNKNOWN;
    }
}
