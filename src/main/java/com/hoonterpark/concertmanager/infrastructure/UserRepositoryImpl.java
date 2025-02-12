package com.hoonterpark.concertmanager.infrastructure;


import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;


    @Override
    public UserEntity save(UserEntity user) {
        return userJpaRepository.save(user);
    }


    @Override
    public List<UserEntity> saveAll(List<UserEntity> users) {
        return userJpaRepository.saveAll(users);
    }


    @Override
    public Optional<UserEntity> findById(Long id) {
        return userJpaRepository.findById(id);
    }


    @Override
    public List<UserEntity> findByName(String name) {
        return userJpaRepository.findByName(name);
    }


    @Override
    public Optional<UserEntity> findByIdWithLock(Long id) {
        return userJpaRepository.findByIdWithLock(id);
    }


}
