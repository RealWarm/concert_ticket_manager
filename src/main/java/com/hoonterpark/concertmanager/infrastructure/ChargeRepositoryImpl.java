package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ChargeEntity;
import com.hoonterpark.concertmanager.domain.repository.ChargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChargeRepositoryImpl implements ChargeRepository {
    private  final ChargeJpaRepository chargeJpaRepository;


    @Override
    public ChargeEntity save(ChargeEntity chargeHistory) {
        return chargeJpaRepository.save(chargeHistory);
    }


    @Override
    public List<ChargeEntity> saveAll(List<ChargeEntity> chargeHistories) {
        return chargeJpaRepository.saveAll(chargeHistories);
    }

}