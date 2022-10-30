package com.example.pets_backend.repository;

import com.example.pets_backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    @Query("select b from Booking b where b.pair_bk_id = ?1")
    Booking findBookingByPair_bk_id(String pair_bk_id);
}
