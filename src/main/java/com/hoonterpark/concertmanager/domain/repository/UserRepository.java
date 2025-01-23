package com.hoonterpark.concertmanager.domain.repository;


import com.hoonterpark.concertmanager.domain.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    // 단일생성
    UserEntity save(UserEntity user);

    // 복수생성
    List<UserEntity> saveAll(List<UserEntity> users);

    // id로 검색
    Optional<UserEntity> findById(Long id);

    // 이름으로 검색
    List<UserEntity> findByName(String name);

    Optional<UserEntity> findByIdWithLock(Long id);
}
