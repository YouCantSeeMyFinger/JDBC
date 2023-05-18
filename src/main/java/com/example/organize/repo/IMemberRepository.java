package com.example.organize.repo;

import com.example.jdbc.domain.Member;

/**
 * 핵심 : SqlException -> RuntimeException
 */

public interface IMemberRepository {

    /**
     * 회원 저장
     *
     * @param member
     * @return Member
     */
    Member save(Member member) ;

    /**
     * 회원 찾기
     *
     * @param memberId
     * @return Member
     */
    Member findById(String memberId);

    /**
     * 회원 업데이트
     *
     * @param memberId
     * @param money
     */

    void update(String memberId, int money) ;

    /**
     * 회원 삭제
     *
     * @param memberId
     */

    void delete(String memberId);

}
