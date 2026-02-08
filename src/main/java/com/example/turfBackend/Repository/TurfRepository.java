package com.example.turfBackend.Repository;

import com.example.turfBackend.Entity.Turf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurfRepository extends JpaRepository<Turf,Long> {

    @Query(value = "SELECT * FROM turf where owner_id := id",nativeQuery = true)
    List<Turf> getByOwnerId(Long id);

}
