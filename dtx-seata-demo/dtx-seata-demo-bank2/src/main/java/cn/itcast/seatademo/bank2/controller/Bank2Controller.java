package cn.itcast.seatademo.bank2.controller;

import cn.itcast.seatademo.bank2.service.AccountInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class Bank2Controller {

    @Resource
    private AccountInfoService accountInfoService;

    // 接受张三转账
    @GetMapping("transfer")
    public String transfer(Double amount) {
        accountInfoService.updateAccountBalance("2", amount);
        return "bank2" + amount;
    }

}
