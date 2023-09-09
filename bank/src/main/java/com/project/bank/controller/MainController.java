package com.project.bank.controller;

import com.project.bank.dto.*;
import com.project.bank.service.AccountService;
import com.project.bank.service.MemberService;
import com.project.bank.service.SavingsService;
import groovy.util.logging.Log4j;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {


    private final AccountService accountService;
    private final MemberService memberService;
    private final SavingsService savingsService;

    private LocalDateTime localDateTime = LocalDateTime.now().plusMonths(1);

    @GetMapping(value={"/","/main"})
    public String MainPage(Model model,PageRequestDTO pageRequestDTO){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        if(LocalDateTime.now().getMonth().toString() == localDateTime.getMonth().toString()) { //한달에 한번 실행
            //적금 처리
            savingsService.UpdateSavings();
            localDateTime = localDateTime.plusMonths(1);
        }


        if (!principal.equals("anonymousUser")) {  //로그인 상태인 경우
            UserDetails userDetails = (UserDetails) principal;

            pageRequestDTO.setSize(5);
             PageResponseDTO<AccountDTO> accountDTO = accountService.SelectUserAccount(pageRequestDTO,((UserDetails) principal).getUsername());

            MemberDTO memberDTO = memberService.SelectMemberInfo(((UserDetails) principal).getUsername());

            int allAccountBalance = accountService.SelectAllAccountBalance(((UserDetails) principal).getUsername());

            SavingsDTO savingsDTO = savingsService.SelectSavingsInfo(((UserDetails) principal).getUsername());
            log.info("적금 체크 : " +savingsDTO);
            //적금 가입 정보 가져오기



             model.addAttribute("allAccountBalance",allAccountBalance);
             model.addAttribute("memberDTO",memberDTO);
             model.addAttribute("accountDTO",accountDTO);
             model.addAttribute("savingsDTO",savingsDTO);

        }



        return "index";
    }



}
