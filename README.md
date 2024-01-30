# PortfolioBank
은행 웹 사이트 개인 프로젝트 포트폴리오

# 소개
 개인의 계좌를 사이트에 등록 시켜 사용자 간의 이체와 적금 가입이 가능한 사이트 입니다.  


# 제작기간 & 참여 인원
<UL>
  <LI>2023.09.01 ~ 2023.09.07</LI>
  <LI>개인 프로젝트</LI>
</UL>


# 사용기술
![js](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white)
![js](https://img.shields.io/badge/Java-FF0000?style=for-the-badge&logo=Java&logoColor=white)
![js](https://img.shields.io/badge/IntelliJ-004088?style=for-the-badge&logo=IntelliJ&logoColor=white)
![js](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=MariaDB&logoColor=white)
![js](https://img.shields.io/badge/security-6DB33F?style=for-the-badge&logo=security&logoColor=white)

![js](https://img.shields.io/badge/jquery-0769AD?style=for-the-badge&logo=jquery&logoColor=white)
![js](https://img.shields.io/badge/bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)
![js](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white)

# E-R 다이어그램

![은행 프로젝트 erd](https://github.com/oals/PortfolioBank/assets/136543676/287f1ddb-4e14-4fa2-8248-1af365fb7412)

# 핵심 기능 및 페이지 소개



<details>
 <summary> Member Entity
 
 </summary> 




    @Getter
    @Setter
    @Table(name="member")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    public class Member {

    @Id
    @Column(name="member_id")
    private String memberId; // 아이디
    private String memberName; //유저이름
    private String pswd;     // 비밀번호
    private String email;     // 이메일
    private String phone;       // 전화번호
    private String age;         // 생년월일
    private String address;     // 주소
    private String gender;      // 성별
    private LocalDateTime regDate;  // 등록일자

    @Enumerated(EnumType.STRING)
    private Level level;       // 레벨



    public static Member createMember(MemberDTO memberDTO, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setMemberId(memberDTO.getMemberId());
        member.setMemberName(memberDTO.getMemberName());
        member.setEmail(memberDTO.getEmail());
        member.setPhone(memberDTO.getPhone());
        member.setAge(memberDTO.getAge());
        member.setAddress(memberDTO.getAddress());
        member.setGender(memberDTO.getGender());
        member.setRegDate(LocalDateTime.now());
        member.setLevel(Level.FIVE); //일반 유저 디폴트값 5등급

        // 암호화
        String password = passwordEncoder.encode(memberDTO.getPswd());
        member.setPswd(password);

        return member;
    }



    }







 
</details>


<details>
 <summary> Account Entity
 
 </summary> 





    @Getter
    @Table(name="account")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    public class Account {

    @Id
    @Column(name="account_number")
    private String accountNumber;       // 계좌 번호

    @Column(name="account_name")
    private String accountName;         //은행명

    @Column(name="account_pswd")
    private String accountPswd;         //계좌 비밀번호

    @Column(name="create_date")
    private LocalDateTime createDate;   // 생성 일자

    @Column(name="balance")
    private int balance;                //계좌 잔액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;              // 회원 아이디



    public void MinusBalance(int balance){
        this.balance -= balance;

    }

    public void PlusBalance(int balance){
        this.balance += balance;

    }





    }






 
</details>


<details>
 <summary> History Entity
 
 </summary> 




    @Getter
    @Table(name="history")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    public class History {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long Id;
    private int balance;               // 계좌 잔액
    private int money;               // 상대방 이체 금액
    private LocalDateTime updateDate;   // 입출금 일자
    private String memberName;          // 입출금자
    private String chk;              // 입/출금/이자 여부

    private String myAccountNumber; // 조회할 계좌



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountNumber")
    private Account account;            //  상대방 계좌 번호


    }







 
</details>


<details>
 <summary> Savings Entity
 
 </summary> 




    @Getter
    @Setter
    @Table(name="savings")
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    public class Savings {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountNumber")
    private Account account;   //적금 게좌

    private int balance;  //월 적금 금액
    private String product_name; // 상품 이름
    private int percent; //이자 퍼센트

    private int AllBalance; //총 적금된 금액

    @Column(name="savings_date")
    private LocalDateTime savingsDate;   // 적금 시작일자

    private LocalDateTime endDate; //적금이 끝나는 날짜


    public void RegisterSavings(int balance){
        this.setAllBalance(balance);
        this.setSavingsDate(LocalDateTime.now());
        this.setEndDate(this.getSavingsDate().plusMonths(6));
    }

    public void UpdateSavingsAllBalance(int balance){
        this.setAllBalance(this.getAllBalance() + balance);
        this.setSavingsDate(LocalDateTime.now());
    }


     }








 
</details>





<hr>

<H3>메인 페이지</H3>
<BR>

![개인 프로젝트 bank 메인 페이지](https://github.com/oals/PortfolioBank/assets/136543676/c8a1a3a4-6a7b-4514-b1cb-6861596c772a)



<br>
<br>
<details>
 <summary> 메인 페이지 플로우 차트
 
 </summary> 
 
<img src='https://github.com/oals/PortfolioBank/assets/136543676/f559e884-030d-40f9-b996-98f5dd9cad50'>
</details>



<br>
<br>






<HR>


<H3>계좌 이체 페이지</H3>
<BR>

![개인 프로젝트 bank 계좌 이체 페이지1](https://github.com/oals/PortfolioBank/assets/136543676/5318187c-6293-4f95-a91c-358576b57034)

<br>
<br>
<details>
 <summary> 계좌 이체 페이지 플로우 차트
 
 </summary> 
 
<img src='https://github.com/oals/PortfolioBankTest/assets/136543676/41d86553-b8c2-4398-985a-bbb63da439b1'>
</details>


<details>
 <summary> 계좌 이체 검사 Service 코드
 
 </summary> 
 


       public boolean SendCheck(TransferDTO transferDTO) {

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QAccount qAccount = QAccount.account;

        Member member = memberRepository.findById(transferDTO.getReceiveMemberName()).orElseThrow();

        // 받는 사람의 계좌 정보 검사
        boolean chk = accountRepository.findByAccountNumberAndAccountNameAndMember(
                transferDTO.getReceiveAccountNumber(), transferDTO.getReceiveAccountName(),member).isPresent();

        if(chk){

            //보내는 사람의 잔액과 이체하려는 금액 비교
           chk =  queryFactory.select(qAccount.balance.gt(transferDTO.getSendBalance()))
                   .from(qAccount)
                   .where(qAccount.accountNumber.eq(transferDTO.getSendAccountNumber()))
                   .fetchOne();

            return chk;
        }

        return chk;
    }




</details>



<details>
 <summary> 계좌 이체 Service 코드
 
 </summary> 
 


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
                .accountNumber(ReceiveAccount.getAccountNumber())           //받는 사람의 계좌 정보
                .updateDate(LocalDateTime.now())                                    //해당 날짜
                .build();


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




</details>





<HR>

<H3>계좌 상세 페이지</H3>
<BR>

![개인 프로젝트 bank 계좌 상세 페이지1](https://github.com/oals/PortfolioBank/assets/136543676/869c59bc-40cf-4d41-9e43-88d323e5b45b)

<UL>
 <LI>계좌 상세 페이지 접근 시 해당 계좌의 비밀번호 네자리를 입력해야합니다. </LI>
</UL>
<BR>


![개인 프로젝트 bank 계좌 상세 페이지2](https://github.com/oals/PortfolioBank/assets/136543676/a44b4ddf-223e-40de-8f71-64336e7f1db3)



<br>
<br>
<details>
 <summary> 계좌 상세 페이지 플로우 차트
 
 </summary> 
 
<img src='https://github.com/oals/PortfolioBank/assets/136543676/ad209e69-06b6-43f0-95d2-00e4940fca14'>
</details>


<br>
<br>









<HR>

<H3>계좌 등록 페이지</H3>
<BR>

![개인 프로젝트 bank 계좌 등록 페이지1](https://github.com/oals/PortfolioBank/assets/136543676/e8b00da1-15e5-4f1e-b099-125c0fab2dcf)


<BR>
<UL>
 <LI>계좌명, 계좌번호 , 비밀번호 네자리를 통해 계좌를 등록 할 수 있습니다. </LI>
</UL>
<BR>

![개인 프로젝트 bank 계좌 등록 페이지2](https://github.com/oals/PortfolioBank/assets/136543676/8174ccd3-96b7-4fd8-9bc3-8316198f3e35)


<BR>
<UL>
 <LI>계좌 번호는 16자리만 등록 가능 합니다. </LI>
</UL>
<BR>


<br>
<br>
<details>
 <summary> 계좌 등록 페이지 플로우 차트
 
 </summary> 
 
<img src='https://github.com/oals/PortfolioBank/assets/136543676/95142535-a473-4b82-b4f2-273f8cf78245'>
</details>


<br>
<br>






<HR>

<H3>적금 가입 페이지</H3>
<BR>

![개인 프로젝트 bank 적금 가입 페이지1](https://github.com/oals/PortfolioBank/assets/136543676/edde13e5-cf82-4287-8e11-6455e1ef971f)

<BR>
<UL>
 <LI> 메인 페이지 하단의 적금란을 통해 적금에 가입 할 수 있습니다.</LI>
  <LI> 하나의 적금에만 가입 할 수 있습니다.</LI>
</UL>
<BR>
<BR>



![개인 프로젝트 bank 적금 가입 페이지2](https://github.com/oals/PortfolioBank/assets/136543676/5fc4e70f-637d-4dc5-81c4-fe5782929f68)


<BR>
<UL>
 <LI> 적금액이 빠져나갈 계좌를 연결 합니다.</LI>
  <LI> 해당 계좌에 첫 1회 적금액이 존재하지 않을 경우 가입 실패로 이어집니다.</LI>
</UL>
<BR>
<BR>




![개인 프로젝트 bank 적금 가입 페이지3](https://github.com/oals/PortfolioBank/assets/136543676/b8aa3cb1-900e-42db-acd3-e82d613a65c3)



<BR>
<UL>
 <LI> 가입된 적금 정보는 메인 페이지의 프로필을 통해 확인 할 수 있습니다. </LI>
</UL>
<BR>
<BR>



<br>
<br>
<details>
 <summary> 적금 페이지 플로우 차트
 
 </summary> 
 
<img src='https://github.com/oals/PortfolioBank/assets/136543676/3ee7004f-90e8-4ec6-b60b-cfd034fafb1c'>
</details>



<br>
<br>


<HR>

# 프로젝트를 통해 느낀 점과 소감


이번 프로젝트는 제작기간이 짧았던 만큼 많은 기능을 넣지 못했다. <BR>
그럼에도 이 프로젝트가 나에게 의미 있었던 이유는 학원이 아닌 스프링 부트 책을 통해서 <BR> 배운 DTO TO ENTTIY 혹은 ENTTITY TO DTO 등의 정석적인 코드 구조를 실험 해볼 수 있어서이다. <BR>
이를 통해서 DTO를 어떤 구조로 코딩 해야 하는 지 좀 더 알게 된 것 같다.


