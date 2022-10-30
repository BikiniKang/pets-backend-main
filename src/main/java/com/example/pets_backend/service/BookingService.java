package com.example.pets_backend.service;


import com.example.pets_backend.entity.Booking;
import com.example.pets_backend.entity.User;
import com.example.pets_backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.pets_backend.ConstantValues.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final SendMailService sendMailService;
    private final PetService petService;
    private final SchedulerService schedulerService;

    public Booking save(Booking booking) {
        booking = bookingRepository.save(booking);
        log.info("New booking '{}' saved into database", booking.getBooking_id());
        return booking;
    }

    public Booking findById(String booking_id) {
        Optional<Booking> booking = bookingRepository.findById(booking_id);
        if (booking.isPresent()) {
            log.info("Booking '{}' found in database", booking_id);
            return booking.get();
        } else {
            log.info("Booking '{}' not found in database", booking_id);
            throw new EntityNotFoundException("Booking " + booking_id + " not found in database");
        }
    }

    public void sendEmail(Booking booking, String template) {
        Map<String, String> model = basicModel(booking);
        User organizer = booking.getUser();
        switch (template) {
            case TEMPLATE_BOOKING_INVITE -> {
                model.put("accept_link", WEB_PREFIX + "#/user/booking/accept_page?id=" + booking.getBooking_id());
                model.put("reject_link", WEB_PREFIX + "#/user/booking/reject_page?id=" + booking.getBooking_id());
                addSendEmailJob(booking.getAttendee(), model, template, "");
            }
            case TEMPLATE_BOOKING_CONFIRM -> {
                User invitee = userService.findByEmail(booking.getAttendee());
                model.put("cancel_link", WEB_PREFIX + "#/user/booking/cancel_page?id=" + booking.getBooking_id());
                // check if the invitee is an internal user
                if (invitee == null) {
                    model.put("invitee", booking.getAttendee());
                    model.put("avatar_invitee", DEFAULT_IMAGE);
                } else {
                    model.put("invitee", invitee.getFirstName() + " " + invitee.getLastName());
                    model.put("avatar_invitee", invitee.getImage());
                }
                String rawIcs = getRawIcs(booking);
                addSendEmailJob(organizer.getEmail(), model, template, rawIcs);
                addSendEmailJob(booking.getAttendee(), model, template, rawIcs);
            }
            case TEMPLATE_BOOKING_CANCEL -> {
                addSendEmailJob(organizer.getEmail(), model, template, "");
                addSendEmailJob(booking.getAttendee(), model, template, "");
            }
            default -> throw new IllegalArgumentException("Template " + template + " not found");
        }
    }

    private void addSendEmailJob(String to, Map<String, String> templateModel, String templateName, String rawIcs) {
        schedulerService.addJobToScheduler(null, new Runnable() {
            @Override
            public void run() {
                try {
                    sendMailService.sendEmail(to, templateModel, templateName, rawIcs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, LocalDateTime.now());
    }

    private Map<String, String> basicModel(Booking booking) {
        Map<String, String> model = new HashMap<>();
        User user = booking.getUser();
        model.put("sender", user.getFirstName() + " " + user.getLastName());
        model.put("avatar_sender", user.getImage());
        model.put("title", booking.getTitle());
        String start_time = booking.getStart_time();
        String end_time = booking.getEnd_time();
        if (start_time.substring(0, 10).equals(end_time.substring(0, 10))) {
            // if start & end are in the same day, use format "yyyy-MM-dd HH:mm - HH:mm"
            model.put("time_range", start_time + "-" + end_time.substring(11));
        } else {
            // if start & end are in different days, use format "yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm"
            model.put("time_range", start_time + " - " + end_time);
        }
        model.put("location", booking.getLocation());
        List<String> petNameList = new ArrayList<>();
        for (String petId: booking.getPet_id_list()) {
            petNameList.add(petService.findByPetId(petId).getPetName());
        }
        model.put("pets", String.join(", ", petNameList));
        model.put("description", booking.getDescription());
        return model;
    }

    private String getRawIcs(Booking booking) {
        String start = transformTimeFormat(booking.getStart_time());
        String end = transformTimeFormat(booking.getEnd_time());
        String stamp = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern(ICS_TIME_PATTERN));
        String description = booking.getDescription();
        String id = booking.getBooking_id();
        String location = booking.getLocation();
        User organizer = booking.getUser();
        String organizerName = organizer.getFirstName() + " " + organizer.getLastName();
        String organizerEmail = organizer.getEmail();
        return "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//TechLauncher//Pets Pocket//EN\n" +
                "METHOD:PUBLISH\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:" + start + "\n" +
                "DTEND:" + end + "\n" +
                "DTSTAMP:" + stamp + "\n" +
                "SUMMARY:Pet Pocket - Appointment\n" +
                "DESCRIPTION:" + description + "\n" +
                "UID:" + id + "\n" +
                "CATEGORIES:Pet Pocket\n" +
                "LOCATION:" + location + "\n" +
                "CREATED:00010101T000000\n" +
                "LAST-MODIFIED:00010101T000000\n" +
                "ORGANIZER;CN=" + organizerName + ":MAILTO:" + organizerEmail + "\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";
    }

    private String transformTimeFormat(String dateTime) {
        ZonedDateTime utcTime = LocalDateTime.parse(dateTime,
                        DateTimeFormatter.ofPattern(DATETIME_PATTERN))
                .atZone(ZoneId.of("Australia/Sydney"))
                .withZoneSameInstant(ZoneId.of("UTC"));
        return utcTime.format(DateTimeFormatter.ofPattern(ICS_TIME_PATTERN));
    }

}
