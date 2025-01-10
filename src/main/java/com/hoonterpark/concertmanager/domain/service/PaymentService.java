package com.hoonterpark.concertmanager.domain.service;

import com.hoonterpark.concertmanager.domain.entity.PaymentEntity;
import com.hoonterpark.concertmanager.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentEntity makePayment(Long reservationId, Long amount){
        PaymentEntity payment = PaymentEntity.builder()
                .reservationId(reservationId)
                .amount(amount)
                .build();
        return paymentRepository.save(payment);
    }//make

}//end
