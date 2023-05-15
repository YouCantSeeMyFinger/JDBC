package com.example.repository;

import com.example.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;


@Slf4j
class MemberRepositoryV0Test {


    MemberRepositoryV0 repo = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // primary key 위반 에러 조심
        // memberId => 기본키이다.
        // money => notnull
        Member member = new Member("gyoon", 10000);
        repo.save(member);

        Member findedMember = repo.findById(member.getMemberId());
        log.info("findedMember : {}", findedMember);
        Assertions.assertThat(findedMember).isEqualTo(member);

    }
}