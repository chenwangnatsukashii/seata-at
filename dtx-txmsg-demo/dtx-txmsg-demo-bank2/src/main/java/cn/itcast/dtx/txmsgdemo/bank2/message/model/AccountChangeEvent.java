package cn.itcast.dtx.txmsgdemo.bank2.message.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountChangeEvent implements Serializable {

    private String accountNo; // 账号
    private double amount; // 变动金额
    private String txNo; // 事务号

}
