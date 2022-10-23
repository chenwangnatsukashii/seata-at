package cn.itcast.dtx.seatademo.bank1.controller;

import cn.itcast.dtx.seatademo.bank1.service.AccountInfoService;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
public class Bank1Controller {

    @Resource
    private AccountInfoService accountInfoService;

    // 张三转账
    @GetMapping("transfer")
    public String transfer(Double amount) {
        accountInfoService.updateAccountBalance("1", amount);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", "bank1 " + UUID.randomUUID() + " " + amount);
        jsonObject.put("code", 200);
        return jsonObject.toJSONString();
    }

}
