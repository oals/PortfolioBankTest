package com.project.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.bank.entity.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HistoryDTO {

    private Long Id;
    private int balance;               // 계좌 잔액
    private int money;               // 상대방 이체 금액
    private LocalDateTime updateDate;   // 입출금 일자
    private String memberName;          // 입출금자
    private String myAccountNumber;  // 조회 계좌의 주인

    private String chk;              // 입/출금/이자 여부
    private String accountNumber;            // 상대방 계좌 번호

}
