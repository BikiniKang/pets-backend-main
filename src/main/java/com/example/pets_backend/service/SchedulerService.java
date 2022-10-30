package com.example.pets_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import static com.example.pets_backend.ConstantValues.TIMEZONE;

/**
 * reference: https://allaboutspringframework.com/spring-schedule-tasks-or-cron-jobs-dynamically/#:~:text=Spring%20provides%20Task%20Scheduler%20API,different%20methods%20to%20schedule%20task.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SchedulerService {

    private final TaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> jobsMap = new HashMap<>();


    public void addJobToScheduler(String id, Runnable job, LocalDateTime triggerTime) {
        ScheduledFuture<?> scheduledJob = scheduler.schedule(job, triggerTime.atZone(ZoneId.of(TIMEZONE)).toInstant());
        // todo: figure out better way to manage scheduled job ids
        if (id == null) {
            id = NanoIdUtils.randomNanoId();
        }
        jobsMap.put(id, scheduledJob);
    }

    public void removeJobFromScheduler(String id) {
        ScheduledFuture<?> scheduledJob = jobsMap.get(id);
        if(scheduledJob != null) {
            scheduledJob.cancel(true);
            jobsMap.remove(id);
            log.info("Job '{}' cancelled", id);
        }
    }

    @EventListener({ ContextRefreshedEvent.class })
    public void contextRefreshedEvent() {
        // Get all tasks from DB and reschedule them in case of context restarted
    }

}
