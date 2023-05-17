package com.example.jdbc.service;

import com.example.jdbc.domain.Member;
import com.example.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;


/**
 * 트랜잭션매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3 {
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    /**
     * <p>트랜잭션 매니져사용</p>
     * 동작 방식<br>
     * 1. 비즈니스 로직에서 트랜잭션시작 (현재 비즈니스 로직을 실행하는 곳은 테스트코드 쪽이다.)
     *    때문에 테스트의 before을 보면 DriverManagerDataSource로 JDBC Connection하기 위한 설정 정보를 dataSource에 담아뒀다. <br>
     * 2. getTransaction을 하여 트랜잭션 정보를 트랜잭션 매니저가 가지고 있다.<br>
     * 3. TransactionManager는 내부에서 dataSource를 사용해서 커넥션을 생성하고 갖고있다.<br>
     * 5. 각 DB마다 AutoCommit의 설정이 다르기 때문에 setAutoCommit(false)를 하고 트랜잭션 메니저는 트랜잭션 동기화 매니저로 넘긴다.<br>
     *    때문에 멀티쓰레드 환경에서도 안전하게 보관이 가능하다. <br>
     * 6. 이전 버전 Repository에서는 트랜잭션을 위해 connection을 parameter로 넘겼다. 하지만 V3버전에서는 트랜잭션 동기화 매니저에 있는 커넥션을
     *    사용하여 로직을 실행한다. (Connection getConnection메소드에 보면 DataSourceUtills.getConnection(dataSource)<br>
     *
     * @param fromId
     * @param toId
     * @param money
     */

    public void accountTransfer(String fromId, String toId, int money) {
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            //비즈니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
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