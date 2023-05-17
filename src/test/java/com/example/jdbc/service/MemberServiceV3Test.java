package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV3;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Driver;
import java.sql.SQLException;

import static com.example.jdbc.connection.ConnectionConst.*;

/**
 * 트랜잭션 매니져 테스트 코드
 */

class MemberServiceV3Test {

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";

    /**
     * 테스트 : memberRepositoryV3 , memberSerivceV3
     */
    private MemberRepositoryV3 memberRepositoryV3;
    private MemberServiceV3 memberServiceV3;

    /**
     * BeforeEach 테스트를 실행하기 전 반드시 실행되도록하는 어노테이션 <br>
     *
     */
    @BeforeEach
    void before() {
        // DriverManagerDataSource를 통해 커넥션을 얻을 자원지정 => 일종의 로그인 ?? 같은 개념
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // memberRepository는 실제로 커넥션을 얻은 것을 통해 Repository가 작동하므로 MemberRepositoryV3에 dataSource를 주입해줘야한다.
        this.memberRepositoryV3 = new MemberRepositoryV3(dataSource);

        // java.lang.IllegalStateException: No DataSource set
        // 서비스 로직에서 트랜잭션을 시작할 때 트랜잭션 매니져는 datasource로부터 connection을 얻어야한다.

        //JDBC를 필요하기 때문에 JDBC 트랜잭션 매니저를 받고 트랙젝션 매니저는 어떤 커넥션을 트랜잭션처리할지 필요하기 때문에 datasource가 반드시 필요하다.
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberServiceV3 = new MemberServiceV3(transactionManager, memberRepositoryV3);
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