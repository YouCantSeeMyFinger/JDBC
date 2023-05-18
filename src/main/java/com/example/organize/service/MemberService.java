package com.example.organize.service;

import com.example.jdbc.domain.Member;
import com.example.organize.cumstomexception.blackListException;
import com.example.organize.repo.IMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    public final IMemberRepository memberRepository;


    /**
     * --주의-- <br>
     * Transactional 어노테이션 주의사항 <br>
     * RuntimeException이 발생 시 Rollback<br>
     * 그 외의 CheckedExceptino는 commit 수행한다
     *
     * @param fromId
     * @param toId
     * @param money
     */
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        this.bizLogic(fromId, toId, money);
    }

    /**
     * 계좌이체 서비스 <br>
     * A To B <br>
     *
     * @param fromId
     * @param toId
     * @param money
     */
    public void bizLogic(String fromId, String toId, int money) {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        this.validation(fromMember, toMember);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        memberRepository.update(toId, toMember.getMoney() + money);
    }


    /**
     * 만약 이체 서비스를 이용하는 블랙리스트 대상자라면 RuntimeException <br>
     *
     * @param fromMember
     * @param toMember
     */
    private void validation(Member fromMember, Member toMember) {
        if (fromMember.getMemberId().equals("blackList") && toMember.getMemberId().equals("blackList")) {
            throw new blackListException("이체 서비스를 이용하는 이용자는 블랙리스트 대상자입니다.");
        }
    }
}
