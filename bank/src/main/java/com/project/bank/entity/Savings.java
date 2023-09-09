package com.project.bank.entity;


import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name="savings")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Savings {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountNumber")
    private Account account;   //적금 게좌

    private int balance;  //월 적금 금액
    private String product_name; // 상품 이름
    private int percent; //이자 퍼센트

    private int AllBalance; //총 적금된 금액

    @Column(name="savings_date")
    private LocalDateTime savingsDate;   // 적금 시작일자

    private LocalDateTime endDate; //적금이 끝나는 날짜


    public void RegisterSavings(int balance){
        this.setAllBalance(balance);
        this.setSavingsDate(LocalDateTime.now());
        this.setEndDate(this.getSavingsDate().plusMonths(6));
    }

    public void UpdateSavingsAllBalance(int balance){
        this.setAllBalance(this.getAllBalance() + balance);
        this.setSavingsDate(LocalDateTime.now());
    }


}
