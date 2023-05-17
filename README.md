# 목표
    1. JDBC를 이용한 CRUD 작업을 만든다.
    2. JDBC를 이용하면서 실제로 문제점이 무엇인지 파악하고 MyBatis , JPA등을 이용하여 개선한다.

# 진행상황
    1. JDBC를 이용한 회원 CRUD작업

# JDBC의 문제점
    1. JDBC를 사용하면 매번 Connection , PreparedStatement , ResultSet등을 이용하기 때문에 반복적이다.
    2. 현재 코드는 JDBC에 너무 의존해 있다.
    3. 2번의 이유로 인해 나중에 JPA 및 MONGO DB와 같은 다른 DB를 이용할 때 SqlException과 같은 예외처리에서 다량의 오류가 발생한다.
    4. 서비스 계층은 순수해야되는대 트랜잭션을 적용하면서 JDBC 구현 기술이 누수되었다.
    5. 반복적인 try catch finally문
# 해결방안
    1. 커넥션 풀을 이용한다.
