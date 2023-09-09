package com.project.bank.controller;


import com.project.bank.dto.AccountDTO;
import com.project.bank.dto.MemberDTO;
import com.project.bank.dto.PageRequestDTO;
import com.project.bank.dto.PageResponseDTO;
import com.project.bank.entity.Account;
import com.project.bank.entity.Member;
import com.project.bank.service.AccountService;
import com.project.bank.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@Log4j2
@RequiredArgsConstructor
public class RegisterController {

    private final MemberService memberService;
    private final AccountService accountService;


    @GetMapping(value="/registerPage")
    public String registerPage(){

        log.info("register go ...");

        return "register/registerPage";
    }






    @PostMapping(value="/register")   //회원가입 및 계좌 생성 페이지로 전송
    public String register(MemberDTO memberDTO, Model model){


        log.info("가입 정보 : "+memberDTO);

        //회원 정보 저장
        memberService.register(memberDTO);



        return "index";

    }


    @PostMapping("/accountregister")  //계좌 생성
    public ModelAndView accountregister(AccountDTO accountDTO,String memberId){


        log.info("chk : "+ accountDTO);
        //계좌 생성
        accountService.accountRegister(accountDTO,memberId);

        //컨트롤러 -> 컨트롤러 이동 코드
        ModelAndView MAV = new ModelAndView();
        MAV.setViewName("redirect:/");
        return MAV;

    }


    @GetMapping("/SavingsRegisterPage")
    public String SavingsRegisterPage(PageRequestDTO pageRequestDTO, Principal principal,Model model,String product_name){

        String memberId = principal.getName();

        pageRequestDTO.setSize(100);
        PageResponseDTO<AccountDTO> list = accountService.SelectAccount(pageRequestDTO," ",memberId);

        if(product_name.equals("목돈 모으기 상품")){
            model.addAttribute("money",10000);
            model.addAttribute("percent",1);
        }else if(product_name.equals("장기 적금 상품")){
            model.addAttribute("money",100000);
            model.addAttribute("percent",2);
        }else if(product_name.equals("WON 적금 상품")){
            model.addAttribute("money",500000);
            model.addAttribute("percent",3);
        }else{
            model.addAttribute("money",1000000);
            model.addAttribute("percent",4);
        }

        model.addAttribute("product_name",product_name);
        model.addAttribute("accountDTO",list);


        return "register/SavingsRegisterPage";
    }



}
