package com.example.pets_backend.repository;


import com.example.pets_backend.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, String> {
    Record findByRecordId(String recordId);

    @Modifying
    @Query("delete from Record where recordId = ?1")
    void deleteByRecordId(String recordId);

    @Query("select r from Record r where r.user.uid = ?1 and r.recordType = ?2")
    List<Record> findAllByUidAndRecordType(String uid, String recordType);
}
