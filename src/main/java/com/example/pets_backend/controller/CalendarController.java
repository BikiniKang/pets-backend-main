package com.example.pets_backend.controller;

import com.example.pets_backend.entity.Booking;
import com.example.pets_backend.entity.Event;
import com.example.pets_backend.entity.Task;
import com.example.pets_backend.entity.User;
import com.example.pets_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class CalendarController {

    private final UserService userService;

    @PostMapping("/user/calendar/date")
    public Map<String, Object> getCalByDate(@RequestBody Map<String, Object> mapIn) {
        String uid = (String) mapIn.get("uid");
        String date = (String) mapIn.get("date");
        User user = userService.findByUid(uid);

        List<Event> eventList = user.getEventsByDate(date);
        List<Task> taskList = user.getTasksByDate(date);
        List<Booking> bookingList = user.getBookingsByDate(date);

        Map<String, Object> mapOut = new HashMap<>();
        mapOut.put("date", date);
        mapOut.put("eventList", eventList);
        mapOut.put("taskList", taskList);
        mapOut.put("bookingList", bookingList);
        return mapOut;
    }

    @PostMapping("/user/calendar/month")
    public List<Map<String, Object>> getCalByMonth(@RequestBody Map<String, Object> mapIn) {
        String uid = (String) mapIn.get("uid");
        String month = (String) mapIn.get("month");
        User user = userService.findByUid(uid);
        return user.getCalByMonth(month);
    }
}
