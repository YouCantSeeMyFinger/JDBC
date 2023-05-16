package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV1;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static com.example.jdbc.connection.ConnectionConst.*;

class MemberServiceTest {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_C = "memberC";
    public static final String MEMBER_EX = "memberEX";

    private MemberRepositoryV1 repo1;
    private MemberServiceV1 service;


    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repo1 = new MemberRepositoryV1(dataSource);
        service = new MemberServiceV1(repo1);
    }

    @AfterEach
    void after() throws SQLException {
        repo1.deleteMember(MEMBER_A);
        repo1.deleteMember(MEMBER_B);
        repo1.deleteMember(MEMBER_C);
        repo1.deleteMember(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        repo1.save(memberA);
        repo1.save(memberB);

        //when
        service.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member findMemberA = repo1.findById(memberA.getMemberId());
        Member findMemberB = repo1.findById(memberB.getMemberId());

        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }
}