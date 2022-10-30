package com.example.pets_backend.service;


import com.example.pets_backend.entity.Task;
import com.example.pets_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    public Task save(Task task) {
        log.info("New task '{}' saved into database", task.getTaskId());
        return taskRepository.save(task);
    }

    public Task findByTaskId(String taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        checkTaskInDB(task, taskId);
        return task;
    }

    public void deleteByTaskId(String taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        checkTaskInDB(task, taskId);
        taskRepository.deleteByTaskId(taskId);
        log.info("Task '{}' deleted from database", taskId);
    }

    public void archive(String taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        checkTaskInDB(task, taskId);
        taskRepository.archive(taskId);
    }

    public List<Task> findUpcomingTasks(String uid, String today) {
        return taskRepository.findUpcomingTasks(uid, today);
    }

    public List<Task> findOverdueTasks(String uid, String today) {
        return taskRepository.findOverdueTasks(uid, today);
    }

    private void checkTaskInDB(Task task, String taskId) {
        if (task == null) {
            log.error("Task '{}' not found in the database", taskId);
            throw new EntityNotFoundException("Task '" + taskId + "' not found in database");
        } else {
            log.info("Task '{}' found in the database", taskId);
        }
    }
}
