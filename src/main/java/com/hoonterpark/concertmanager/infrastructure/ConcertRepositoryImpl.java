package com.hoonterpark.concertmanager.infrastructure;

import com.hoonterpark.concertmanager.domain.entity.ConcertEntity;
import com.hoonterpark.concertmanager.domain.enums.ConcertStatus;
import com.hoonterpark.concertmanager.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository concertJpaRepository;


    @Override
    public ConcertEntity save(ConcertEntity concert) {
        return concertJpaRepository.save(concert);
    }

    @Override
    public List<ConcertEntity> saveAll(List<ConcertEntity> concerts) {
        return concertJpaRepository.saveAll(concerts);
    }

    @Override
    public Optional<ConcertEntity> findById(Long id) {
        return concertJpaRepository.findById(id);
    }

    @Override
    public Optional<ConcertEntity> findByConcertName(String concertName) {
        return concertJpaRepository.findByConcertName(concertName);
    }

    @Override
    public List<ConcertEntity> findByStatus(ConcertStatus status) {
        return concertJpaRepository.findByStatus(status);
    }

}
