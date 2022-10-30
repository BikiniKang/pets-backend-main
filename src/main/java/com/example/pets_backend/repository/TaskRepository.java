package com.example.pets_backend.repository;


import com.example.pets_backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    Task findByTaskId(String taskId);

    @Modifying
    @Query("delete from Task where taskId = ?1")
    void deleteByTaskId(String taskId);

    @Transactional
    @Modifying
    @Query("update Task t set t.archived = true where t.taskId = ?1")
    void archive(String taskId);

    @Query("select t from Task as t where t.user.uid = ?1 and t.dueDate = ?2 and t.archived = false")
    List<Task> findUpcomingTasks(String uid, String today);

    @Query("select t from Task as t where t.user.uid = ?1 and t.dueDate < ?2 and t.archived = false")
    List<Task> findOverdueTasks(String uid, String today);
}
