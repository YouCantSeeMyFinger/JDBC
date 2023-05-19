package com.example.exceptiontranslator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionTranslator {

    DataSource dataSource;


    @BeforeEach
    void init() {
        this.dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    @DisplayName("BadGrammar")
    void badGrammar() {
        String sql = """
                select bad grammar
                """;

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            // 42122 => H2 DB BadGrammar exception code Number
            assertThat(e.getErrorCode()).isEqualTo(42122);

            SQLExceptionTranslator sqlExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException 작업 = sqlExceptionTranslator.translate("작업", sql, e);
            assertThat(작업.getClass()).isEqualTo(BadSqlGrammarException.class);
        } finally {
            JdbcUtils.closeStatement(pstmt);
            JdbcUtils.closeConnection(con);
        }
    }
}
