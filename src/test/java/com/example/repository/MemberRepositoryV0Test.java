package com.example.repository;

import com.example.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;


@Slf4j
class MemberRepositoryV0Test {


    MemberRepositoryV0 repo = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // primary key 위반 에러 조심
        // memberId => 기본키이다.
        // money => notnull

        //save
        Member member = new Member("test3", 10000);
        repo.save(member);


        //findById
        Member findedMember = repo.findById(member.getMemberId());
        log.info("findedMember : {}", findedMember);
        Assertions.assertThat(findedMember).isEqualTo(member);


        //update: money: 10000 >> 20000
        repo.update(member.getMemberId(), 50000);
        Member member2 = repo.findById(member.getMemberId());
        Assertions.assertThat(member2.getMoney()).isEqualTo(50000);

        //delete
        repo.deleteMember(member.getMemberId());
        Assertions.assertThatThrownBy(() -> repo.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);

    }
}