package com.example.jdbctemplate.repo.member;

import com.example.jdbc.domain.Member;

public interface IMemberRepository {

    public abstract Member save(Member member);

    public abstract void update(String memberId, int money);

    public abstract void delete(String memberId);

    public abstract Member findById(String memberId);

}
