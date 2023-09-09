package com.project.bank.service;

import com.project.bank.dto.*;
import com.project.bank.entity.Account;
import com.project.bank.entity.Member;
import lombok.extern.log4j.Log4j2;
import org.thymeleaf.expression.Arrays;

import java.time.LocalDateTime;
import java.util.List;


public interface AccountService {


    default String AccountNumberUpdate(String accountNumber){

        //4개 단위로 4개 나누기 후에 추가 후 합치기
        String[] str = new String[4];

        for(int i = 0; i < 4; i++){
            str[i] = accountNumber.substring(i * 4 , (i + 1) * 4);
        }
        String NewAccountNumber = "";

        for(int i = 0; i < 4; i++){

            if(i != 3) {
                NewAccountNumber += str[i] + "-";
            }else{
                NewAccountNumber += str[i];
            }
        }

        return NewAccountNumber;
    }

    default Account dtoToEntity(AccountDTO accountDTO,String memberId){


        Member member = Member.builder()
                .memberId(memberId)
                .build();


        Account account = Account.builder()
                .accountNumber(accountDTO.getAccountNumber())
                .accountPswd(accountDTO.getAccountPswd())
                .accountName(accountDTO.getAccountName())
                .createDate(LocalDateTime.now())
                .balance(1000)
                .member(member)
                .build();



        return account;
    }




    public void accountRegister(AccountDTO accountDTO,String memberId); //계좌 생성


    public boolean SendCheck(TransferDTO transferDTO); //계좌 검사

    public boolean SendMoney(TransferDTO transferDTO); //이체

    public boolean AccountPswdCheck(String accountNumber,String accountPswd);



    public PageResponseDTO<AccountDTO> SelectUserAccount(PageRequestDTO pageRequestDTO,String username);  //유저 정보로 계쫘 정보 얻기

    public AccountDTO SelectAccountInfo(String accountNumber); // 계좌 번호로 계좌 정보 얻기

    public PageResponseDTO<AccountDTO> SelectAccount(PageRequestDTO pageRequestDTO, String accountNumber, String memberId); //해당 유저의 모든 계좌 얻기



    public int SelectAllAccountBalance(String username);    //총 예금액 가져오기

    public boolean SelectAccountNumberCheck(String accountNumber);




}
