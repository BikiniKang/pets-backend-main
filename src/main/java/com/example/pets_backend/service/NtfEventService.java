package com.example.pets_backend.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.pets_backend.entity.Event;
import com.example.pets_backend.entity.NtfEvent;
import com.example.pets_backend.entity.User;
import com.example.pets_backend.repository.NtfEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static com.example.pets_backend.ConstantValues.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NtfEventService {

    private final NtfEventRepository ntfRepo;
    private final SchedulerService schedulerService;
    private final SendMailService sendMailService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);


    public void addNotification(Event event) {
        User user = event.getUser();
        // check whether the event notification setting is 'ON'
        if (!user.isEventNtfOn() || event.getNotifyBefore() == 0) {
            log.info("The Event Notification Setting for User '{}' is OFF, do not notify", user.getEmail());
            return;
        }
        // check whether the event has ended
        if (LocalDateTime.parse(event.getEndDateTime(), formatter).isBefore(LocalDateTime.now())) {
            log.info("Event '{}' has ended, do not notify '{}'", event.getEventId(), user.getEmail());
            return;
        }
        String email = user.getEmail();
        String firstName = user.getFirstName();
        Map<String, String> templateModel = generateTemplateModel(firstName, event);
        LocalDateTime sendTime = LocalDateTime.parse(event.getStartDateTime(), formatter)
                .minus(event.getNotifyBefore(), ChronoUnit.HOURS);

        NtfEvent ntfEvent = new NtfEvent(NanoIdUtils.randomNanoId(),
                user.getUid(),
                event.getEventId(),
                sendTime,
                false);
        ntfRepo.save(ntfEvent);

        String ntfId = ntfEvent.getNtfId();
        addEmailJobToScheduler(ntfId, email, templateModel, TEMPLATE_EVENT, sendTime);
        log.info("Added notification '{}' into scheduler", ntfId);
    }

    public void deleteNotification (String eventId) {
        NtfEvent ntfEvent = ntfRepo.findByEventId(eventId);
        if (ntfEvent == null) {
            log.error("Notification for event '{}' not found", eventId);
            return;
        }
        String ntfId = ntfEvent.getNtfId();
        schedulerService.removeJobFromScheduler(ntfId);
        ntfRepo.deleteByNtfId(ntfId);
        log.info("Deleted notification '{}' from scheduler", ntfId);
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

    private Map<String, String> generateTemplateModel(String firstName, Event event) {
        Map<String, String> templateModel = new HashMap<>();
        templateModel.put("firstName", firstName);
        templateModel.put("petNames", String.join(", ", event.getPetNameList()));
        templateModel.put("eventTitle", event.getEventTitle());
        templateModel.put("eventStartTime", event.getStartDateTime());
        templateModel.put("eventLocation", event.getDescription());
        templateModel.put("petAvatar", (String) event.getPetAbList().get(0).get("petAvatar"));
        return templateModel;
    }
}
