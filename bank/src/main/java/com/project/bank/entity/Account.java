package com.project.bank.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Table(name="account")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {

    @Id
    @Column(name="account_number")
    private String accountNumber;       // 계좌 번호

    @Column(name="account_name")
    private String accountName;

    @Column(name="account_pswd")
    private String accountPswd;

    @Column(name="create_date")
    private LocalDateTime createDate;   // 생성 일자

    @Column(name="balance")
    private int balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;              // 회원 아이디



    public void MinusBalance(int balance){
        this.balance -= balance;

    }

    public void PlusBalance(int balance){
        this.balance += balance;

    }





}
