package com.turfmanager.repository;

import com.turfmanager.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SlotRepository extends JpaRepository<Slot,Long> {
    boolean existsByTurfIdAndSlotDateAndSlotType(Long id, LocalDate date, char c);
}
