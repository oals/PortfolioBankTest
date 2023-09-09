package com.project.bank.RestController;

import com.project.bank.dto.*;
import com.project.bank.entity.Account;
import com.project.bank.entity.Savings;
import com.project.bank.service.AccountService;
import com.project.bank.service.HistoryService;
import com.project.bank.service.SavingsService;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;
    private final HistoryService historyService;
    private final SavingsService savingsService;


    @GetMapping("/AccountPswdChk")
    public boolean AccountPswdChk(String accountNumber,String accountPswd){

        log.info(accountNumber);
        log.info(accountPswd);
        boolean result = false;

        result = accountService.AccountPswdCheck(accountNumber,accountPswd);


        return result;
    }

    @PutMapping("/InsertMoney")
    public boolean InsertMoney(TransferDTO transferDTO){

        log.info("이체 정보 : " + transferDTO);

        //계좌 검사
        boolean result = accountService.SendCheck(transferDTO);

        log.info("계좌 검사 결과 : " + result);

        if(result){
            //계좌 이체 및 이체 내역 생성
            result = accountService.SendMoney(transferDTO);

        }

        return result;   //계좌 이체 완료 메세지 페이지로 전송 혹은 오류 내용 페이지 전송


    }


    @GetMapping("/GetAccount")
    public Object GetAccount(PageRequestDTO pageRequestDTO, String accountNumber, String memberId){

        pageRequestDTO.setSize(5);
        PageResponseDTO<AccountDTO> list = accountService.SelectAccount(pageRequestDTO,accountNumber,memberId);


      return list;
    }


    @GetMapping("/GetUserAccount")
    public Object GetUserAccount(PageRequestDTO pageRequestDTO, Principal principal){




        String memberId = principal.getName();
        pageRequestDTO.setSize(5);

        PageResponseDTO<AccountDTO> list = accountService.SelectUserAccount(pageRequestDTO,memberId);



        return list;
    }

    @GetMapping("/GetOptAccountInfo")
    public List<HistoryDTO> GetAccountInfo(String accountNumber,String opt){

        log.info("계좌번호 : "+accountNumber);
        log.info("옵션 : " + opt);

        List<HistoryDTO> list=  historyService.SelectHistory(accountNumber,opt);

        log.info("데이터 체크 : " + list);

        return list;
    }


    @PostMapping("/SavingsAccount")
    public boolean SavingsAccount(SavingsDTO savingsDTO){

        //해당 계좌의 잔액 검사 (1회분 검사)
       boolean balacneChk = savingsService.SavingsCheckBalance(savingsDTO);
        log.info("데이터 검사 : " + savingsDTO);

        boolean chk = false;

       if(balacneChk){
               log.info("신규 가입 접근 ");
               savingsService.NewSavings(savingsDTO);
               chk = true;
       }

        return chk;
    }

    @GetMapping("AccountRegisterCheck")
    public boolean AccountRegisterCheck(String accountNumber){
        log.info("AccountRegisterCheck w접근");

        boolean chk = accountService.SelectAccountNumberCheck(accountNumber);

        return chk;
    }




}
