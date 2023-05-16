package com.example.jdbc.service;


import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 repo;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = repo.findById(fromId);
        Member toMember = repo.findById(toId);

        repo.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        repo.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 오류발생");
        }
    }
}
