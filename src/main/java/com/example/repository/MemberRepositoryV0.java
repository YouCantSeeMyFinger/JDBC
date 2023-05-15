package com.example.repository;


import com.example.domain.Member;
import com.example.jdbc.connection.DBConnectionUtil;
import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * Driver Manager 사용<br>
 * <p>
 * Level - low Level 방법
 */
@Slf4j
public class MemberRepositoryV0 {

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

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }
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

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
