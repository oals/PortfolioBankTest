package com.project.bank.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransferDTO {

    //보내는 사람
    private String SendAccountNumber;
    private String SendAccountName;
    private String SendMemberName;
    private int SendBalance;

    //받는 사람
    private String receiveAccountNumber;
    private String receiveAccountName;
    private String receiveMemberName;



}
