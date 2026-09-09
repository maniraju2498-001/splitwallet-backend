package com.splitwallet.repository;

import com.splitwallet.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    List<ExpenseSplit> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);
}
