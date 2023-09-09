package com.project.bank.service;

import com.project.bank.dto.AccountDTO;
import com.project.bank.dto.HistoryDTO;
import com.project.bank.entity.Account;
import com.project.bank.entity.History;
import com.project.bank.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryService {

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


    default HistoryDTO entityToDTO(History history){

        HistoryDTO historyDTO = HistoryDTO.builder()
                .balance(history.getBalance())
                .money(history.getMoney())
                .updateDate(history.getUpdateDate())
                .memberName(history.getMemberName())
                .myAccountNumber(history.getMyAccountNumber())
                .chk(history.getChk())
                .accountNumber(history.getAccount().getAccountNumber())
                .build();

        return historyDTO;
    }
    default History dtoToEntity(HistoryDTO historyDTO){


        Account account = Account.builder()
                .accountNumber(historyDTO.getAccountNumber())
                .build();

        History history = History.builder()
                .balance(historyDTO.getBalance())
                .money(historyDTO.getMoney())
                .updateDate(historyDTO.getUpdateDate())
                .memberName(historyDTO.getMemberName())
                .chk(historyDTO.getChk())
                .myAccountNumber(historyDTO.getMyAccountNumber())
                .account(account)
                .build();


        return history;
    }


    public List<HistoryDTO> SelectHistory(String accountNumber,String opt);
}
