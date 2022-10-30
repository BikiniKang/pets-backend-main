package com.example.pets_backend;

import com.example.pets_backend.entity.Booking;
import com.example.pets_backend.entity.Event;
import com.example.pets_backend.entity.Task;
import com.example.pets_backend.entity.User;
import com.example.pets_backend.repository.UserRepository;
import com.example.pets_backend.service.NtfTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.pets_backend.ConstantValues.DATETIME_PATTERN;

@RequiredArgsConstructor
@Component
public class ScheduledJobs {

    private final UserRepository userRepository;
    private final NtfTaskService ntfTaskService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    @Scheduled(cron = "0 0 4 * * *")    // repeat 4am everyday
    public void ntfUpcomingTasks() {
        for (User user:userRepository.findAll()) {
            ntfTaskService.addTasksNotification(user, false);
        }
    }

    @Scheduled(cron = "0 0 9 * * *")    // repeat 9am everyday
    public void ntfOverdueTasks() {
        for (User user:userRepository.findAll()) {
            ntfTaskService.addTasksNotification(user, true);
        }
    }

    // update sample user
    @Scheduled(cron = "0 0 0 * * *")    // repeat 0am everyday
    @Transactional
    public void updateSampleUser() {
        User sampleUser = userRepository.findByUid("4EL4hp_qRUYMzzal_G29f");
        for (Event event:sampleUser.getEventList()) {
            event.setStartDateTime(LocalDateTime.parse(event.getStartDateTime(), formatter).plusDays(1).format(formatter));
            event.setEndDateTime(LocalDateTime.parse(event.getEndDateTime(), formatter).plusDays(1).format(formatter));
        }
        for (Task task:sampleUser.getTaskList()) {
            task.setDueDate(LocalDate.parse(task.getDueDate()).plusDays(1).toString());
        }
        for (Booking booking:sampleUser.getBookingList()) {
            booking.setStart_time(LocalDateTime.parse(booking.getStart_time(), formatter).plusDays(1).format(formatter));
            booking.setStart_time(LocalDateTime.parse(booking.getEnd_time(), formatter).plusDays(1).format(formatter));
        }
    }
}
