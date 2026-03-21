package com.example.turfBackend.Repository;

import com.example.turfBackend.Entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint,Long> {

    @Query(value = "SELECT * FROM complaints where staff_id = :id",nativeQuery = true)
    List<Complaint> findByStaffId(Long id);
}
