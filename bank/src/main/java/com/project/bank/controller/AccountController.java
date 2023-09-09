package com.project.bank.controller;

import com.project.bank.dto.AccountDTO;
import com.project.bank.dto.HistoryDTO;
import com.project.bank.dto.TransferDTO;
import com.project.bank.service.AccountService;
import com.project.bank.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.expression.Arrays;

import java.sql.Array;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class AccountController {

    private final HistoryService historyService;
    private final AccountService accountService;


    @GetMapping("/InsertAccount")
    public String InsertAccount(String memberId, Model model){
        
        log.info("계좌 등록 페이지 접근");

        model.addAttribute("memberId",memberId);
        
        return "register/accountRegisterPage";
    }


    @GetMapping("/accountInfo")
    public String accountInfo(String accountNumber,Model model){
        log.info("계좌 정보 페이지 접근");

        log.info("계좌번호 : " + accountNumber);

        //계좌 정보 / 내역 가져오기

       List<HistoryDTO> historyDTO = historyService.SelectHistory(accountNumber,"전체");

       AccountDTO accountDTO = accountService.SelectAccountInfo(accountNumber);

       log.info(historyDTO);
       log.info(accountDTO);

       model.addAttribute("accountDTO",accountDTO);
       model.addAttribute("historyDTO",historyDTO);

        
        
        return "account/accountInfo";
    }

    @GetMapping("/sendPage")
    public String sendPage(Model model,String accountNumber){
        log.info("이체 페이지 접근");
        log.info("보내는 사람의 게좌번호 : " + accountNumber);
        //보내는 사람 정보

        AccountDTO accountDTO = accountService.SelectAccountInfo(accountNumber);


        model.addAttribute("accountDTO",accountDTO);


        return "account/sendMoney";
    }






}
