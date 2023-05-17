package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;


    /**
     * Transactional 어노테이션 <br>
     * 특징 : 서비스 로직이 실행 되기전에 프록시가 실행되고 트랜잭션만 따로 모아서 실행<br>
     * 1. 해당 어노테이션이 붙은 메소드가 있을 경우 메소드 실행 시에 트랜젝션이 시작<br>
     * 2. 해당 메소드가 실행되면서 런타임에러가 발생할 시에 롤백한다. (참고로 언체크예외만 롤백)<br>
     *
     * @param fromId
     * @param toId
     * @param money
     * @throws SQLException
     */
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        this.bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}