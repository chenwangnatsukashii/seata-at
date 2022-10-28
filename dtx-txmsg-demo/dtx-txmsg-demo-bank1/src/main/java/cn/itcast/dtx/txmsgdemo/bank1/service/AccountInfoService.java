package cn.itcast.dtx.txmsgdemo.bank1.service;

import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;

public interface AccountInfoService {

    // 向MQ发送转账消息
    void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent);

    // 更新账户，扣减金额
    void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent);
}
