package com.example.jdbctemplate.repo.member;

import com.example.jdbc.domain.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * JDBCTEMPLATE , SQLExceptionTranslator <BR><BR>
 * JDBC 종속 제거<BR><BR>
 */

@Repository
public class MemberRepository implements IMemberRepository {
    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = """
                insert into member(member_id , money) values(? , ?)                
                """;
        this.jdbcTemplate.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    @Override
    public void update(String memberId, int money) {
        String sql = """
                 update member set money=? where member_id = ?
                """;
        this.jdbcTemplate.update(sql, money, memberId);
    }

    @Override
    public void delete(String memberId) {
        String sql = """
                delete from member where member_id = ?
                """;
        this.jdbcTemplate.update(sql, memberId);
    }

    @Override
    public Member findById(String memberId) {
        String sql = """
                select * from member where member_id=?                
                """;
        return this.jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return ((rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        });
    }
}
