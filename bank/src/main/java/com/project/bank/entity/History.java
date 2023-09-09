package com.project.bank.entity;

import com.project.bank.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Table(name="history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class History {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long Id;
    private int balance;               // 계좌 잔액
    private int money;               // 상대방 이체 금액
    private LocalDateTime updateDate;   // 입출금 일자
    private String memberName;          // 입출금자
    private String chk;              // 입/출금/이자 여부

    private String myAccountNumber; // 조회할 계좌



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountNumber")
    private Account account;            //  보낸 사람의 계좌 번호


}
