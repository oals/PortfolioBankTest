package com.project.bank.dto;

import com.project.bank.entity.Account;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SavingsDTO {
    private Long id;

    private String accountNumber;   //적금 게좌

    private int balance;  //월 적금 금액
    private String product_name; // 상품 이름
    private int percent; //이자 퍼센트

    private int AllBalance; //총 적금된 금액

    private LocalDateTime savingsDate;   // 적금 시작일자

    private LocalDateTime endDate; //적금이 끝나는 날짜



}
