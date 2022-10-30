package com.example.pets_backend.service;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.pets_backend.entity.NtfTask;
import com.example.pets_backend.entity.Task;
import com.example.pets_backend.entity.User;
import com.example.pets_backend.repository.NtfTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.pets_backend.ConstantValues.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NtfTaskService {

    private final NtfTaskRepository ntfRepo;
    private final SchedulerService schedulerService;
    private final SendMailService sendMailService;
    private final TaskService taskService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);


    public void addTasksNotification(User user, boolean isOverdue) {
        // check whether the task notification setting is 'ON'
        if (!user.isTaskNtfOn()) {
            log.info("The Task Notification Setting for User '{}' is OFF, do not notify", user.getEmail());
            return;
        }

        // parse the required values for the notification
        String today = LocalDate.now().toString();
        List<Task> taskList = isOverdue ? taskService.findOverdueTasks(user.getUid(), today)
                : taskService.findUpcomingTasks(user.getUid(), today);
        if (taskList.isEmpty()) {
            log.info("No upcoming/overdue tasks detected, do not notify");
            return;
        }
        String ntfType = isOverdue ? "OVERDUE_TASKS":"UPCOMING_TASKS";
        String templateName = isOverdue ? TEMPLATE_OVERDUE_TASKS:TEMPLATE_UPCOMING_TASKS;
        String theTime = isOverdue ? OVERDUE_TASKS_NOTIFY_TIME:user.getTaskNtfTime();
        String email = user.getEmail();
        String firstName = user.getFirstName();
        Map<String, String> templateModel = generateTemplateModel(firstName, taskList);
        LocalDateTime sendTime = LocalDateTime.parse(today + " " + theTime, formatter);

        // save a new NtfTask instance
        NtfTask ntfTask = new NtfTask(NanoIdUtils.randomNanoId(),
                user.getUid(),
                taskList.stream().map(Task::getTaskId).toList(),
                sendTime,
                ntfType,
                today,
                false);
        ntfRepo.save(ntfTask);

        // add the notification job into scheduler
        String ntfId = ntfTask.getNtfId();
        addEmailJobToScheduler(ntfId, email, templateModel, templateName, sendTime);
        log.info("Added notification '{}' into scheduler", ntfId);
    }

    private void addEmailJobToScheduler(String ntfId, String email, Map<String, String> templateModel, String templateName, LocalDateTime sendTime) {
        schedulerService.addJobToScheduler(ntfId, new Runnable() {
            @Override
            public void run() {
                try {
                    sendMailService.sendEmail(email, templateModel, templateName, "");
                    ntfRepo.markAsDone(ntfId);
                    log.info("Job finished, mark Notification '{}' as done", ntfId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, sendTime);
    }


    private Map<String, String> generateTemplateModel(String firstName, List<Task> taskList) {
        Map<String, String> templateModel = new HashMap<>();
        templateModel.put("firstName", firstName);
        templateModel.put("task1", "");
        templateModel.put("task2", "");
        templateModel.put("task3", "");
        templateModel.put("task1Pets", "");
        templateModel.put("task2Pets", "");
        templateModel.put("task3Pets", "");
        if (taskList.size() > 0) {
            templateModel.put("task1", taskList.get(0).getTaskTitle());
            templateModel.put("task1Pets", "(" + String.join(", ", taskList.get(0).getPetNameList()) + ")");
        }
        if (taskList.size() > 1) {
            templateModel.put("task2", taskList.get(1).getTaskTitle());
            templateModel.put("task2Pets", "(" + String.join(", ", taskList.get(1).getPetNameList()) + ")");
        }
        if (taskList.size() > 2) {
            templateModel.put("task3", taskList.get(2).getTaskTitle());
            templateModel.put("task3Pets", "(" + String.join(", ", taskList.get(2).getPetNameList()) + ")");
        }
        return templateModel;
    }

}
