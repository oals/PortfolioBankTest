package com.project.bank.service;

import com.project.bank.dto.HistoryDTO;
import com.project.bank.dto.SavingsDTO;
import com.project.bank.entity.*;
import com.project.bank.repository.AccountRepository;
import com.project.bank.repository.HistoryRepository;
import com.project.bank.repository.SavingsRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class SavingsServiceImpl implements SavingsService{
    
    private final SavingsRepository savingsRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final HistoryService historyService;
    private final HistoryRepository historyRepository;

    @PersistenceContext
    EntityManager em;

    @Override
    public boolean SavingsCheckBalance(SavingsDTO savingsDTO) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QAccount qAccount = QAccount.account;


        boolean chk =  queryFactory.select(qAccount.balance.gt(savingsDTO.getBalance()))
                .from(qAccount)
                .where(qAccount.accountNumber.eq(savingsDTO.getAccountNumber()))
                .fetchOne();

        log.info("적금 가입자의 현재 잔고 검사 : " + chk);


        return chk;
    }


    @Override
    public SavingsDTO SelectSavingsInfo(String memberId) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember qMember = QMember.member;
        QAccount qAccount = QAccount.account;
        QSavings qSavings = QSavings.savings;

        Savings savings = queryFactory.selectFrom(qSavings)
                .where(qSavings.account.member.memberId.eq(memberId)).fetchOne();

        SavingsDTO savingsDTO = null;
        if(savings != null) {
             savingsDTO = entityToDTO(savings);
        }


        return savingsDTO;
    }

    @Override
    public boolean NewSavings(SavingsDTO savingsDTO) {  //적금 처리


        //계좌에서 금액 감소
        Account account = accountRepository.findById(savingsDTO.getAccountNumber()).orElseThrow();

        //적금 데이터 저장
        Savings savings = dtoToEntity(savingsDTO);
        savings.RegisterSavings(savingsDTO.getBalance());
        savingsRepository.save(savings);

        //일반 내역에 저장
        HistoryDTO historyDTO = HistoryDTO.builder()
                .balance(account.getBalance() - savings.getBalance())  //현재 잔액
                .money(savings.getBalance())                                //보낸 금액
                .chk("적금" )
                .memberName(savings.getProduct_name())
                .myAccountNumber(account.getAccountNumber())
                .accountNumber(account.getAccountNumber())                    //받는 사람의 계좌 정보
                .updateDate(LocalDateTime.now())                                    //해당 날짜
                .build();


        History History = historyService.dtoToEntity(historyDTO);
        historyRepository.save(History);

        account.MinusBalance(savingsDTO.getBalance());
        accountRepository.save(account);





        return true;
    }

    @Override
    public boolean UpdateSavings() {


        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QSavings qSavings = QSavings.savings;

        List<Savings> savings = queryFactory.selectFrom(qSavings).fetch();



        for(int i = 0; i< savings.size(); i++){
            //한달이 지났을 경우
            if(savings.get(i).getSavingsDate().plusMonths(1).getMonth().toString() == LocalDateTime.now().getMonth().toString()){   //시간 메서드 수정

                //적금 처리
                Account account = accountRepository.findById(savings.get(i).getAccount().getAccountNumber()).orElseThrow();

                //적금 반환
                if(savings.get(i).getEndDate().getMonth().toString() == LocalDateTime.now().getMonth().toString()){
                    log.info("적금반환");

                    //반환할 금액 연산
                    int savingsMoney = savings.get(i).getAllBalance();
                    int percent  = savings.get(i).getPercent() * (savingsMoney / 100);
                    int result = savingsMoney + percent;

                    HistoryDTO historyDTO = HistoryDTO.builder()
                            .balance(account.getBalance() + result)  //현재 잔액
                            .money(savings.get(i).getAllBalance())                                //보낸 금액
                            .chk("적금 반환")
                            .memberName(savings.get(i).getProduct_name())
                            .myAccountNumber(account.getAccountNumber())
                            .accountNumber(account.getAccountNumber())                    //받는 사람의 계좌 정보
                            .updateDate(LocalDateTime.now())                                    //해당 날짜
                            .build();

                    // 내역에 저장
                    History History = historyService.dtoToEntity(historyDTO);
                    historyRepository.save(History);


                    //계좌에서 금액 추가
                    account.PlusBalance(savings.get(i).getAllBalance());
                    accountRepository.save(account);
                    
                    //적금 정보 삭제
                    savingsRepository.deleteById(savings.get(i).getId());





                }else {

                    savings.get(i).UpdateSavingsAllBalance(savings.get(i).getBalance());
                    savingsRepository.save(savings.get(i));


                    HistoryDTO historyDTO = HistoryDTO.builder()
                            .balance(account.getBalance() - savings.get(i).getBalance())  //현재 잔액
                            .money(savings.get(i).getBalance())                                //보낸 금액
                            .chk("적금")
                            .memberName(savings.get(i).getProduct_name())
                            .myAccountNumber(account.getAccountNumber())
                            .accountNumber(account.getAccountNumber())                    //받는 사람의 계좌 정보
                            .updateDate(LocalDateTime.now())                                    //해당 날짜
                            .build();

                    // 내역에 저장
                    History History = historyService.dtoToEntity(historyDTO);
                    historyRepository.save(History);


                    //계좌에서 금액 감소
                    account.MinusBalance(savings.get(i).getBalance());
                    accountRepository.save(account);

                }
            }




        }





        return true;
    }


}
