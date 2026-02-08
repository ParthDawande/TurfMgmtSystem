package com.example.turfBackend.Repository;

import com.example.turfBackend.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Query(value = "SELECT * FROM booking where user_id:=id",nativeQuery = true)
    List<Booking> findAllByUserId(Long id);
}
