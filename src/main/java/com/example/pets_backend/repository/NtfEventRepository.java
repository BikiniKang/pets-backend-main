package com.example.pets_backend.repository;


import com.example.pets_backend.entity.NtfEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NtfEventRepository extends JpaRepository<NtfEvent, String> {
    NtfEvent findByEventId(String eventId);

    @Modifying
    @Query("delete from NtfEvent where ntfId = ?1")
    void deleteByNtfId(String ntfId);

    @Transactional
    @Modifying
    @Query("update NtfEvent n set n.done = true where n.ntfId = ?1")
    void markAsDone(String ntfId);
}
