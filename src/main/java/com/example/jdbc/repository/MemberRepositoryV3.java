package com.example.jdbc.repository;


import com.example.jdbc.connection.DBConnectionUtil;
import com.example.jdbc.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

import static com.example.jdbc.connection.ConnectionConst.*;


@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV3 {

    /**
     * 트랜잭션 매니저를 사용한다. <br>
     * 기존 코드에서는 다음과 같은 문제점이 있었다. <br>
     * 1. 트랜잭션을 하기위해 자바의 예외처리문의 반복적인 코드 <br>
     * 2. 트랜잭션 이라는 단위로 서비스 로직을 처리 해야 하기 때문에 connection을 계속 파라미터로 끌고와야 한다.
     */

    private final DataSource dataSource;

    /**
     * 회원저장
     *
     * @param member
     * @return
     * @throws SQLException
     */
    public Member save(Member member) throws SQLException {
        String sql = """
                insert into member(member_id , money) values(? , ?)
                """;

        // 현재 아래 코드의 단점은 save를 할 때마다 con , pstmt를 계속 해주어야 한다는 단점이 존재한다.
        // 추후 공부를 하면서 이 점을 보완할 기술이 있다면 적용하자.
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = this.getConnection();
            pstmt = con.prepareStatement(sql);
            // pstmt를 이용하여 바인딩 변수의 값을 넣고 있다.
            // 참고로 pstmt의 바인딩 인덱스는 1부터 시작이다.
            // id는 String 이기때문에 setString
            // money = notNull ,
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (Exception e) {
            log.error("db error", e);
            throw e;
        } finally {
            this.close(con, pstmt, null);
        }

    }

    /**
     * 회원찾기
     *
     * @param memberId
     * @return
     * @throws SQLException
     */
    public Member findById(String memberId) throws SQLException {
        String sql = """
                select * from member where member_Id = ?
                """;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId =" + memberId);
            }

            // rs를 통해 찾을 db의 데이터를 Member 인스턴스 생성 후 바인딩을 통해 값을 설정한 뒤에 다시 return

        } catch (Exception e) {
            log.error("error", e);
            throw e;
        } finally {
            this.close(con, pstmt, rs);
        }
    }

    public void update(String member_id, int money) throws SQLException {
        String sql = """
                update member set money=? where member_id= ?
                """;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = this.getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, money);
            pstmt.setString(2, member_id);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize : {}", resultSize);

        } catch (Exception e) {
            log.error("DB error", e);
            throw e;
        } finally {
            this.close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        // 주의 ! 트랜잭션 동기화를 사용하려면 DataSourceUtills를 사용해야한다.
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    public void deleteMember(String memberId) throws SQLException {
        String sql = """
                delete from member where member_id = ?
                """;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = this.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            log.error("DB error ", e);
            throw e;
        } finally {
            this.close(con, pstmt, rs);
        }
    }

    private Connection getConnection() throws SQLException {
        // 주의 ! 트랜잭션 동기화를 사용하려면 DataSourceUtills를 사용해야한다.
        Connection conn = DataSourceUtils.getConnection(dataSource);
        log.info("get connection : {}", "class : {}", conn, conn.getClass());
        return conn;

    }
}
