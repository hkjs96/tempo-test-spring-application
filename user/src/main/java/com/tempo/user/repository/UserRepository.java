package com.tempo.user.repository;

import com.tempo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 이메일과 이름으로 사용자 조회
    Optional<User> findByEmailAndName(String email, String name);

    // 이름으로 사용자 목록 조회
    List<User> findByName(String name);

    // 특정 ID 목록에 해당하는 사용자들 조회
    List<User> findByIdIn(List<Long> ids);
}