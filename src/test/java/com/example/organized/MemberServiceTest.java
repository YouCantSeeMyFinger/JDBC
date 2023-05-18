package com.example.organized;


import com.example.organize.repo.IMemberRepository;
import com.example.organize.repo.MemberRepo;
import com.example.organize.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootTest
@RequiredArgsConstructor
public class MemberServiceTest {

    @Autowired
    private final IMemberRepository memberRepository;
    @Autowired
    private final MemberService memberService;

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";


    /**
     * 아래의 클래스는 테스트를 위해 만들어놓은 TestConfiguration
     */
    @TestConfiguration
    @RequiredArgsConstructor
    static class TestConfig {

        private final DataSource dataSource;

        @Bean
        IMemberRepository memberRepository() {
            return new MemberRepo(this.dataSource);
        }

        @Bean
        MemberService service() {
            return new MemberService(this.memberRepository());
        }
    }


    /**
     * AfterEach 메소드는 테스트하는 메소드가 한번씩 실행되고 똑같이 한번씩 실행되도록 만든 어노테이션이다. <br>
     * 테스트할 때마다 memberRepository에 회원이 계속 저장되어있어서 Primary key Violation 때문에 작성
     */
    @AfterEach
    public void afterEach() {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

}
