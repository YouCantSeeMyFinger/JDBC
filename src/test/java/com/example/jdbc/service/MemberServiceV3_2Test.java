package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV3;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static com.example.jdbc.connection.ConnectionConst.*;


class MemberServiceV3_2Test {

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";

    /**
     * 테스트 : memberRepositoryV3 , memberSerivceV3
     */
    private MemberRepositoryV3 memberRepositoryV3;
    private MemberServiceV3_2 memberServiceV3;

    /**
     * BeforeEach 테스트를 실행하기 전 반드시 실행되도록하는 어노테이션 <br>
     *
     */
    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        this.memberRepositoryV3 = new MemberRepositoryV3(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberServiceV3 = new MemberServiceV3_2(transactionManager, memberRepositoryV3);
    }


    /**
     * 테스트 메소드 하나가 실행된 후 반드시 실행되는 AfterEach
     * @throws SQLException
     */
    @AfterEach
    void after() throws SQLException {
        memberRepositoryV3.deleteMember(MEMBER_A);
        memberRepositoryV3.deleteMember(MEMBER_B);
        memberRepositoryV3.deleteMember(MEMBER_EX);
    }


    /**
     * 이체 서비스 로직 테스트
     * @throws SQLException
     */
    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepositoryV3.save(memberA);
        memberRepositoryV3.save(memberB);

        //when
        memberServiceV3.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member findMemberA = memberRepositoryV3.findById(memberA.getMemberId());
        Member findMemberB = memberRepositoryV3.findById(memberB.getMemberId());

        Assertions.assertThat(findMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }


}