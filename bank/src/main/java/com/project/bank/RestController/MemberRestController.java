package com.project.bank.RestController;

import com.project.bank.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MemberRestController {

    private final MemberService memberService;

    @GetMapping("/memberIdCheck")
    public boolean memberIdCheck(String memberId){

        boolean chk = false;

        chk = memberService.MemberIdCheck(memberId);
        log.info("검사 결과 : " + chk);


        return chk;
    }



}
