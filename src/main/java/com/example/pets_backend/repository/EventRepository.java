package com.example.pets_backend.repository;


import com.example.pets_backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    Event findByEventId(String eventId);

    @Modifying
    @Query("delete from Event where eventId = ?1")
    void  deleteByEventId(String eventId);
}
