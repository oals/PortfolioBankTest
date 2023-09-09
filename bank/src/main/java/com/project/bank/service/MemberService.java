package com.project.bank.service;

import com.project.bank.constant.Level;
import com.project.bank.dto.AccountDTO;
import com.project.bank.dto.MemberDTO;
import com.project.bank.entity.Account;
import com.project.bank.entity.Member;
import org.springframework.stereotype.Service;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

public interface MemberService {




    public void register(MemberDTO memberDTO);

    public boolean MemberIdCheck(String memberId);

    public MemberDTO SelectMemberInfo(String memberId);



}
