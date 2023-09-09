package com.project.bank.service;

import com.project.bank.dto.AccountDTO;
import com.project.bank.dto.SavingsDTO;
import com.project.bank.entity.Account;
import com.project.bank.entity.Member;
import com.project.bank.entity.Savings;

import java.time.LocalDateTime;

public interface SavingsService {


    default SavingsDTO entityToDTO(Savings savings){


        SavingsDTO savingsDTO = SavingsDTO.builder()
                .accountNumber(savings.getAccount().getAccountNumber())
                .balance(savings.getBalance())
                .product_name(savings.getProduct_name())
                .percent(savings.getPercent())
                .AllBalance(savings.getAllBalance())
                .savingsDate(savings.getSavingsDate())
                .build();



        return savingsDTO;
    }

    default Savings dtoToEntity(SavingsDTO savingsDTO){


        Account account = Account.builder()
                .accountNumber(savingsDTO.getAccountNumber())
                .build();


        Savings savings = Savings.builder()
                .account(account)
                .balance(savingsDTO.getBalance())
                .product_name(savingsDTO.getProduct_name())
                .percent(savingsDTO.getPercent())
                .AllBalance(savingsDTO.getAllBalance())
                .savingsDate(savingsDTO.getSavingsDate())
                .build();



        return savings;
    }


    public boolean SavingsCheckBalance(SavingsDTO savingsDTO);  //적금시 계좌의 잔액 검사



    public SavingsDTO SelectSavingsInfo(String memberId);

    public boolean NewSavings(SavingsDTO savingsDTO);  //적금 신규 가입 유저


    public boolean UpdateSavings(); //적금 가입 유저 데이터 업데이트 / 적금 처리




}
