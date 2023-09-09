package com.project.bank.service;

import com.project.bank.dto.*;
import com.project.bank.entity.*;
import com.project.bank.repository.AccountRepository;
import com.project.bank.repository.HistoryRepository;
import com.project.bank.repository.MemberRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountServiceImpl implements AccountService{

    @PersistenceContext
    EntityManager em;

    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final HistoryRepository historyRepository;
    private  final MemberRepository memberRepository;
    private final HistoryService historyService;

    @Override
    public void accountRegister(AccountDTO accountDTO,String memberId) {

        Account account = dtoToEntity(accountDTO,memberId);

        accountRepository.save(account);

    }

    @Override
    public boolean SendCheck(TransferDTO transferDTO) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QAccount qAccount = QAccount.account;

        Member member = memberRepository.findById(transferDTO.getReceiveMemberName()).orElseThrow();

        // 받는 사람의 계좌 정보 검사
        boolean chk = accountRepository.findByAccountNumberAndAccountNameAndMember(
                transferDTO.getReceiveAccountNumber(), transferDTO.getReceiveAccountName(),member).isPresent();

        log.info("받는 사람의 계좌 존재 검사 : " + chk);
        log.info(transferDTO);


        if(chk){

            //받는 사람의 계좌가 있으면 보내는 사람의 잔액과 이체하려는 금액 비교
           chk =  queryFactory.select(qAccount.balance.gt(transferDTO.getSendBalance()))
                   .from(qAccount)
                   .where(qAccount.accountNumber.eq(transferDTO.getSendAccountNumber()))
                   .fetchOne();
            log.info("보내는 사람의 잔고 정보 : " + chk);

            return chk;
        }

        return chk;
    }

    @Override
    public boolean SendMoney(TransferDTO transferDTO) {

        boolean result = true;
        //보내는 사람의 예금 감소
        Account SendAccount = accountRepository.findById(transferDTO.getSendAccountNumber()).orElseThrow();

        //받는 사람의 예금 추가
        Account ReceiveAccount = accountRepository.findById(transferDTO.getReceiveAccountNumber()).orElseThrow();

        try{

        // 보내는 사람의 계좌 이체 내역 추가
        HistoryDTO SendHistoryDTO = HistoryDTO.builder()
                .balance(SendAccount.getBalance() - transferDTO.getSendBalance())  //현재 잔액
                .money(transferDTO.getSendBalance())                                //보낸 금액
                .chk("송금")
                .memberName(ReceiveAccount.getMember().getMemberName())
                .myAccountNumber(SendAccount.getAccountNumber())
                .accountNumber(ReceiveAccount.getAccountNumber())                                             //받는 사람의 계좌 정보
                .updateDate(LocalDateTime.now())                                    //해당 날짜
                .build();


        //여기 modelmapper -> dtoToEntity 변경

        History SendHistory = historyService.dtoToEntity(SendHistoryDTO);

        historyRepository.save(SendHistory);


        //받는 사람의 계좌 이체 내역 추가
        HistoryDTO ReceiveHistoryDTO = HistoryDTO.builder()
                .balance(ReceiveAccount.getBalance() + transferDTO.getSendBalance())
                .money(transferDTO.getSendBalance())
                .chk("입금")
                .accountNumber(SendAccount.getAccountNumber())
                .memberName(SendAccount.getMember().getMemberName())
                .myAccountNumber(ReceiveAccount.getAccountNumber())
                .updateDate(LocalDateTime.now())
                .build();



        History ReceiveHistory = historyService.dtoToEntity(ReceiveHistoryDTO);

                //modelMapper.map(ReceiveHistoryDTO,History.class);

        historyRepository.save(ReceiveHistory);



        SendAccount.MinusBalance(transferDTO.getSendBalance());
        accountRepository.save(SendAccount);


        ReceiveAccount.PlusBalance(transferDTO.getSendBalance());
        accountRepository.save(ReceiveAccount);

        }catch (Exception e){
            result =false;
        }


        return result;
    }

    @Override
    public boolean AccountPswdCheck(String accountNumber, String accountPswd) {

        boolean result = accountRepository.findByAccountNumberAndAccountPswd(accountNumber,accountPswd).isPresent();

        log.info("계좌 비밀번호 검사 결과 : " + result);


        return result;
    }


    @Override
    public PageResponseDTO<AccountDTO> SelectUserAccount(PageRequestDTO pageRequestDTO,String username) {


        Pageable pageable =pageRequestDTO.getPageable();


        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QAccount qAccount = QAccount.account;

        List<Account> query =  queryFactory.selectFrom(qAccount)
                .where(qAccount.member.memberId.eq(username))
                .offset(pageable.getOffset())   //N 번부터 시작
                .limit(pageable.getPageSize()) //조회 갯수
                .fetch();

        //현재 dto 로는 유저 정보 뿌려주기 불가
        List<AccountDTO> list = new ArrayList<>();

        if(query.size() != 0) {

            list = query.stream().map(x -> modelMapper.map(x,AccountDTO.class)).collect(Collectors.toList());

            //계좌 번호 ' - ' 추가
            for(int i = 0; i < list.size(); i++){
                list.get(i).setAccountNumber(AccountNumberUpdate(list.get(i).getAccountNumber()));
            }

        }

        Long count = queryFactory
                .select(qAccount.count())
                .from(qAccount)
                .where(qAccount.member.memberId.eq(username))
                .fetchOne();



        return PageResponseDTO.<AccountDTO>widthAll()
                .pageRequestDTO(pageRequestDTO)
                .list(list)
                .total(Integer.parseInt(count.toString()))
                .build();
    }

    @Override
    public AccountDTO SelectAccountInfo(String accountNumber) {

        Account account = accountRepository.findById(accountNumber).orElseThrow();

        AccountDTO accountDTO = modelMapper.map(account,AccountDTO.class);

        //계좌 번호 ' - ' 추가
        accountDTO.setAccountNumber(AccountNumberUpdate(accountDTO.getAccountNumber()));


        return accountDTO;
    }

    @Override
    public PageResponseDTO<AccountDTO> SelectAccount(PageRequestDTO pageRequestDTO, String accountNumber, String memberId) {

        Pageable pageable =pageRequestDTO.getPageable();

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QAccount qAccount = QAccount.account;

        List<Account> query =  queryFactory.selectFrom(qAccount)
                .where(qAccount.member.memberId.eq(memberId).and(qAccount.accountNumber.ne(accountNumber)))
                .offset(pageable.getOffset())   //N 번부터 시작
                .limit(pageable.getPageSize()) //조회 갯수
                .fetch();

        List<AccountDTO> list = query.stream().map(x -> modelMapper.map(x,AccountDTO.class)).collect(Collectors.toList());


        //계좌 번호 ' - ' 추가
        for(int i = 0; i< list.size(); i++){
            list.get(i).setAccountNumber( AccountNumberUpdate(list.get(i).getAccountNumber()));
        }



        Long count = queryFactory
                .select(qAccount.count())
                .from(qAccount)
                .where(qAccount.member.memberId.eq(memberId).and(qAccount.accountNumber.ne(accountNumber)))
                .fetchOne();



        return PageResponseDTO.<AccountDTO>widthAll()
                .pageRequestDTO(pageRequestDTO)
                .list(list)
                .total(Integer.parseInt(count.toString()))
                .build();



    }

    @Override
    public int SelectAllAccountBalance(String username) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QAccount qAccount = QAccount.account;

        List<Integer> list =  queryFactory.select(qAccount.balance)
                .from(qAccount)
                .where(qAccount.member.memberId.eq(username)).fetch();
        int AllBalance = 0;

        for(int i =0; i< list.size(); i++){
            AllBalance += list.get(i);
        }


        log.info("총 예금액 : " + AllBalance);

        return AllBalance;
    }

    @Override
    public boolean SelectAccountNumberCheck(String accountNumber) {


       boolean chk =  accountRepository.findById(accountNumber).isPresent();


        return chk;
    }


}
