package com.splitwallet.repository;

import com.splitwallet.entity.SplitParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitParticipantRepository extends JpaRepository<SplitParticipant, Long> {
    List<SplitParticipant> findBySplitId(Long splitId);
    List<SplitParticipant> findByUserIdOrderByApprovalStatusAsc(Long userId);
}
