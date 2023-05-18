package com.example.organize.repo;


import com.example.jdbc.domain.Member;
import com.example.organize.cumstomexception.MyDbException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@Slf4j
@Repository
@RequiredArgsConstructor

/**
 *
 * SqlException으로 인해 JDBC에 의존하는 상황<br>
 * throw new RuntimeException(e) => SqlException의 stackTrace를 RuntimeException에 넘겨주었고 런타임 Exception이 발생<br>
 * 위의 방법대로 sqlException을 처리함으로써 SqlException이 RuntimeException으로 치환되었고 RuntimeException을 뱉어낼 때 <br>
 * Caused By절에 SqlException의 문제로 인해서 RuntimeException이 발생했다 라고 알려준다.<br>
 * 이렇게 코드를 짜는 이유는 SqlException이 각 메소드마다 붙는다면 JDBC에 의존하는 상황이 연출이 되기 때문이다.<br>
 * 추후에 JPA 혹은 MongoDB와 같이 다른 DB로 변경해야하는 경우 SqlExceptino이 붙은 메소드를 하나하나 다 바꾸어 줘야하기 때문이다.<br>
 *
 * 정리 : 예외 누수 문제를 해결했다.
 * 방법 : checkException을 RuntimeException으로 감싸고 Caused By 로 SqlException때문에 발생한 예외라고 선언
 *
 */
public class MemberRepo implements IMemberRepository {

    public final DataSource dataSource;

    /**
     * 회원저장 메소드 <br><br>
     * 문제 <br>
     * 1. SQLException으로 인해 종속적인 문제 <br>
     * 2. 반복적인 try-catch , connection , pstmt<br>
     *
     * @param member
     * @return Member
     * @throws SQLException
     */
    @Override
    public Member save(Member member) {
        String sql = """
                insert into member(member_Id , money) values(? , ?)
                """;
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = this.connection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("DB error ", e);
            throw new MyDbException(e);
        } finally {
            this.close(con, pstmt, null);
        }
    }

    /**
     * 회원의 id찾기 메소드
     * @param memberId
     * @return
     */
    @Override
    public Member findById(String memberId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = """
                select * from member where member_id=?
                """;

        try {
            con = this.connection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // ResultSet에는 커서가 존재한다.
                // rs.next()를하면 ResultSet에 값이 있다면 true를 반환하고 커서가 해당 값을 가르킨다.
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("해당 아이디는 존재하지 않습니다.");
            }
        } catch (SQLException e) {
            throw new MyDbException(e);
        } finally {
            this.close(con, pstmt, rs);
        }
    }

    /**
     * 회원의 돈 수정 메소드
     * @param memberId
     * @param money
     */
    @Override
    public void update(String memberId, int money) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = """
                update member set money = ? where member_id = ?
                """;
        try {
            con = this.connection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            log.error("DB error", e);
            throw new MyDbException(e);
        } finally {
            this.close(con, pstmt, null);
        }
    }

    /**
     * 회원삭제 메소드
     * @param memberId
     */
    @Override
    public void delete(String memberId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = """
                delete from member where member_id = ?
                """;
        try {
            con = this.connection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("DB error", e);
            throw new MyDbException(e);
        } finally {
            this.close(con, pstmt, null);
        }
    }

    /**
     * DataSourceUtill.getConnection을 사용하지 않으면 Parameter로 connection을 계속 넘겨줘야한다.
     *
     * @return connection
     */
    private Connection connection() {
        return DataSourceUtils.getConnection(this.dataSource);
    }

    /**
     * con , pstmt rs close
     *
     * @param con
     * @param pstmt
     */
    private void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
        JdbcUtils.closeStatement(pstmt);
        JdbcUtils.closeConnection(con);
        JdbcUtils.closeResultSet(rs);
    }

}
