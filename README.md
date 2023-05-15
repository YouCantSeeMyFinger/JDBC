# 목표
    1. JDBC를 이용한 CRUD 작업을 만든다.
    2. JDBC를 이용하면서 실제로 문제점이 무엇인지 파악하고 MyBatis , JPA등을 이용하여 개선한다.

# 진행상황
    1. JDBC를 이용한 회원 CRUD작업

# JDBC의 문제점
    1. JDBC를 사용하면 매번 Connection , PreparedStatement , ResultSet등을 이용하기 때문에 반복적이다.

# 해결방안
    1. 커넥션 풀을 이용한다.