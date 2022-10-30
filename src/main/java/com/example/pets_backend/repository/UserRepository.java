package com.example.pets_backend.repository;

import com.example.pets_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);
    User findByUid(String uid);
    void deleteByEmail(String email);
}
