package com.example.turfBackend.Repository;

import com.example.turfBackend.Entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<Slot,Long> {
}
