package com.example.pets_backend.service;

import com.example.pets_backend.entity.Event;
import com.example.pets_backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final NtfEventService ntfService;

    public Event save(Event event) {
        event = eventRepository.save(event);
        log.info("New event '{}' saved into database", event.getEventId());
        // add a notification
        ntfService.addNotification(event);
        return event;
    }

    public Event findByEventId(String eventId) {
        Event event = eventRepository.findByEventId(eventId);
        checkEventInDB(event, eventId);
        return event;
    }

    public void deleteByEventId(String eventId) {
        Event event = eventRepository.findByEventId(eventId);
        checkEventInDB(event, eventId);
        eventRepository.deleteByEventId(eventId);
        // delete the notification
        ntfService.deleteNotification(eventId);
    }

    public Event editEvent(String eventId, Event eventNew) {
        Event event = eventRepository.findByEventId(eventId);
        // update all attributes except eventId, user
        event.setEventType(eventNew.getEventType());
        event.setEventTitle(eventNew.getEventTitle());
        event.setPetIdList(eventNew.getPetIdList());
        event.setDescription(eventNew.getDescription());
        event.setStartDateTime(eventNew.getStartDateTime());
        event.setEndDateTime(eventNew.getEndDateTime());
        // delete old notification for the event, add a new notification
        ntfService.deleteNotification(eventId);
        ntfService.addNotification(event);
        return event;
    }

    private void checkEventInDB(Event event, String eventId) {
        if (event == null) {
            log.error("Event '{}' not found in the database", eventId);
            throw new EntityNotFoundException("Event '" + eventId + "' not found in database");
        } else {
            log.info("Event '{}' found in the database", eventId);
        }
    }

}
