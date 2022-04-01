package com.skcc.spring.repository;

import com.skcc.spring.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = { "boards" }) // n+1 성능 이슈 해결 하나의 조인으로 데이터 조회
    List<User> findAll();

    User findByUsername(String username);

    @Query("select u from User u where u.username like %?1%")
    List<User> findByUsernameQuery(String username);

    @Query(value = "select * from User u where u.username like %?1%", nativeQuery = true)
    List<User> findByUsernameNativeQuery(String username);
}
