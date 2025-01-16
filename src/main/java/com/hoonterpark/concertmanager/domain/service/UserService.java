package com.hoonterpark.concertmanager.domain.service;


import com.hoonterpark.concertmanager.common.CustomException;
import com.hoonterpark.concertmanager.common.ErrorCode;
import com.hoonterpark.concertmanager.domain.entity.UserEntity;
import com.hoonterpark.concertmanager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 유저 존재하지? id
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 유저입니다."));
    }


    // 포인트 충전
    public UserEntity chargePoint(Long id, Long chargeAmount) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 유저입니다."));
        user.chargePoint(chargeAmount);
        return userRepository.save(user);
    }


    // 결제
    public UserEntity payment(Long id, Long payAmount){
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 유저입니다."));
        user.pay(payAmount);
        return userRepository.save(user);
    }

}//end
