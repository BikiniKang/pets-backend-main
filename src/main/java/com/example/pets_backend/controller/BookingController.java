package com.example.pets_backend.controller;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.pets_backend.entity.Booking;
import com.example.pets_backend.entity.User;
import com.example.pets_backend.service.BookingService;
import com.example.pets_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.example.pets_backend.ConstantValues.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @Transactional
    @PostMapping("/user/booking/invite")
    public Booking inviteBooking(@RequestBody Booking booking) {
        booking.setBooking_id(NanoIdUtils.randomNanoId());
        booking.setUser(userService.findByUid(booking.getUid()));
        booking.setStatus("pending");
        booking = bookingService.save(booking);
        // if the invitee is a user of our app, add the booking into the invitee's calendar
        User invitee = userService.findByEmail(booking.getAttendee());
        if (invitee != null) {
            Booking pair_booking = new Booking(invitee.getUid(), booking.getUser().getEmail(), booking.getTitle(),
                    booking.getStart_time(), booking.getEnd_time(), booking.getLocation(), booking.getDescription(),
                    "pending", false);
            pair_booking.setUser(invitee);
            pair_booking.setPair_bk_id(booking.getBooking_id());
            pair_booking = bookingService.save(pair_booking);
            booking.setPair_bk_id(pair_booking.getBooking_id());
        }
        bookingService.sendEmail(booking, TEMPLATE_BOOKING_INVITE);
        return booking;
    }

    @Transactional
    @RequestMapping("/user/booking/confirm")
    public Booking confirmBooking(@RequestParam String booking_id) {
        Booking booking = bookingService.findById(booking_id);
        if (!booking.getStatus().equals("pending")) {
            throw new IllegalStateException("The booking is not pending");
        }
        booking.setStatus("confirmed");
        // update the booking on the invitee's calendar
        if (booking.getPair_bk_id() != null) {
            Booking pair_booking = bookingService.findById(booking.getPair_bk_id());
            pair_booking.setStatus("confirmed");
        }
        bookingService.sendEmail(booking, TEMPLATE_BOOKING_CONFIRM);
        return booking;
    }

    @Transactional
    @RequestMapping("/user/booking/reject")
    public Booking rejectBooking(@RequestParam String booking_id) {
        Booking booking = bookingService.findById(booking_id);
        if (!booking.getStatus().equals("pending")) {
            throw new IllegalStateException("The booking is not pending");
        }
        booking.setStatus("rejected");
        // update the booking on the invitee's calendar
        if (booking.getPair_bk_id() != null) {
            Booking pair_booking = bookingService.findById(booking.getPair_bk_id());
            pair_booking.setStatus("rejected");
        }
        /*
        Currently, if the invitee rejected the booking request, the booking will just disappear from
        the sender's calendar.
        In the future, we may need to notify the sender (i.e., in-app/email notification).
         */
        return booking;
    }

    @Transactional
    @RequestMapping("/user/booking/cancel")
    public Booking cancelBooking(@RequestParam String booking_id) {
        Booking booking = bookingService.findById(booking_id);
        if (!booking.getStatus().equals("confirmed")) {
            throw new IllegalStateException("The booking is not confirmed");
        }
        booking.setStatus("cancelled");
        // update the booking on the invitee's calendar
        if (booking.getPair_bk_id() != null) {
            Booking pair_booking = bookingService.findById(booking.getPair_bk_id());
            pair_booking.setStatus("cancelled");
        }
        bookingService.sendEmail(booking, TEMPLATE_BOOKING_CANCEL);
        return booking;
    }

    @RequestMapping("/user/booking/get/by_id")
    public Booking get1Booking(@RequestParam String booking_id) {
        return bookingService.findById(booking_id);
    }

    @RequestMapping("/user/booking/get/by_date")
    public List<Booking> getBookingsByDate(@RequestParam String uid, @RequestParam String date) {
        User user = userService.findByUid(uid);
        return user.getBookingsByDate(date)
                .stream()
                .filter(b -> b.getStatus().equals("pending") || b.getStatus().equals("confirmed"))
                .toList();
    }

    @RequestMapping("/user/booking/get")
    public List<Booking> getBookings(@RequestParam String uid) {
        User user = userService.findByUid(uid);
        return user.getBookingList()
                .stream()
                .filter(b -> b.getStatus().equals("pending") || b.getStatus().equals("confirmed"))
                .sorted(Comparator.comparing(Booking::getStart_time))
                .toList();
    }

    @PostMapping("/user/booking/get/by_month")
    public List<Map<String, Object>> getBookingsByMonth(@RequestBody Map<String, String> body) {
        String uid = body.get("uid");
        String month = body.get("month");
        User user = userService.findByUid(uid);
        return user.getBookingsByMonth(month);
    }
}
