package cn.itcast.dtx.seatademo.bank1.service.impl;

import cn.itcast.dtx.seatademo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.seatademo.bank1.service.AccountInfoService;
import cn.itcast.dtx.seatademo.bank1.spring.Bank2Client;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Resource
    private AccountInfoDao accountInfoDao;
    @Resource
    private Bank2Client bank2Client;

    @Override
    @Transactional
    @GlobalTransactional
    public void updateAccountBalance(String accountNo, Double amount) {
        log.info("bank1 service begin, XID: {}" + RootContext.getXID());
        // 扣减张三的金额
        accountInfoDao.updateAccountBalance(accountNo, -amount);
        // 调用李四的微服务，转账
        String transfer = bank2Client.transfer(amount);
        if ("fallback".equals(transfer)) {
            // 调用李四微服务异常
            throw new RuntimeException("调用李四微服务异常");
        }
        if (amount == 2) {
            throw new RuntimeException("bank1 make exception.");
        }
    }
}
