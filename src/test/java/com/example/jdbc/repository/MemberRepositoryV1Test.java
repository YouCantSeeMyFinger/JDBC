package com.example.jdbc.repository;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV1;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static com.example.jdbc.connection.ConnectionConst.*;


@Slf4j
class MemberRepositoryV1Test {


    MemberRepositoryV1 memberRepositoryV1;

    @BeforeEach
    void beforeEach() throws SQLException {
//      DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        log.info("dataSource : {}", dataSource);
        memberRepositoryV1 = new MemberRepositoryV1(dataSource);

    }

    @Test
    void crud() throws SQLException {
        //save
        Member member = new Member("test200", 10000);
        memberRepositoryV1.save(member);


        //findById
        Member findedMember = memberRepositoryV1.findById(member.getMemberId());
        log.info("findedMember : {}", findedMember);
        Assertions.assertThat(findedMember).isEqualTo(member);


        //update: money: 10000 >> 20000
        memberRepositoryV1.update(member.getMemberId(), 50000);
        Member member2 = memberRepositoryV1.findById(member.getMemberId());
        Assertions.assertThat(member2.getMoney()).isEqualTo(50000);

//        //delete
//        memberRepositoryV1.deleteMember(member.getMemberId());
//        Assertions.assertThatThrownBy(() -> memberRepositoryV1.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);

    }
}