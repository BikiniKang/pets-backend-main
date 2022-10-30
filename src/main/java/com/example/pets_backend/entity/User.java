package com.example.pets_backend.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.*;

import static com.example.pets_backend.ConstantValues.*;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Slf4j
public class User {

    @Id
    private final String uid = NanoIdUtils.randomNanoId();

    @NonNull
    @Column(length = 32)  //TODO: might can be defined as 'unique' instead of manually check duplicates in user service
    private String email;

    @JsonIgnore
    private boolean email_verified = false;

    @JsonIgnore
    private String verify_token = NanoIdUtils.randomNanoId();

    @NonNull
    private String password;

    @NonNull
    @Column(length = 32)
    private String firstName;

    @NonNull
    @Column(length = 32)
    private String lastName;

    @Column(length = 16)
    private String phone = DEFAULT_PHONE;

    private String address = DEFAULT_ADDRESS;

    private String image = DEFAULT_IMAGE;

    private boolean isPetSitter = false;

    private boolean eventNtfOn = true;

    private boolean taskNtfOn = true;

    @Column(length = 5)
    private String taskNtfTime  = "18:00";

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Pet> petList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> taskList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Event> eventList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Record> recordList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookingList = new ArrayList<>();

    public List<LinkedHashMap<String, Object>> getPetAbList() {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        for (Pet pet:this.petList) {
            list.add(pet.getPetAb());
        }
        return list;
    }

    public List<LinkedHashMap<String, Object>> getEventAbList() {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        for (Event event:this.eventList) {
            list.add(event.getEventAb());
        }
        return list;
    }

    public List<LinkedHashMap<String, Object>> getTaskAbList() {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        for (Task task:this.taskList) {
            list.add(task.getTaskAb());
        }
        return list;
    }

    public List<LinkedHashMap<String, Object>> getRecordAbList() {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        for (Record record:this.recordList) {
            list.add(record.getRecordAb());
        }
        return list;
    }

    @JsonIgnore
    public List<String> getPetIdList() {
        List<String> petIdList = new ArrayList<>();
        for (Pet pet:petList) {
            petIdList.add(pet.getPetId());
        }
        return petIdList;
    }

    @JsonIgnore
    public LinkedHashMap<String, Object> getNotificationSettings() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("eventNtfOn", eventNtfOn);
        map.put("taskNtfOn", taskNtfOn);
        map.put("taskNtfTime", taskNtfTime);
        return map;
    }

    /**
     * Get the Pet object by petName
     * @param petName the name of the pet
     * @return a Pet object
     */
    @JsonIgnore
    public Pet getPetByPetName(String petName) {
        for (Pet pet:this.petList) {
            if (pet.getPetName().equals(petName)) {
                log.info("Pet with name '{}' found in User '{}'", petName, this.uid);
                return pet;
            }
        }
        return null;
    }

    /**
     * Get the Pet object by petId
     * @param petId the id of the pet
     * @return a Pet object
     */
    @JsonIgnore
    public Pet getPetByPetId(String petId) {
        for (Pet pet:this.petList) {
            if (pet.getPetId().equals(petId)) {
                log.info("Pet '{}' found in User '{}'", pet.getPetId(), this.uid);
                return pet;
            }
        }
        throw new EntityNotFoundException("Pet '" + petId + "' not found in User '" + this.uid + "'");
    }

    /**
     * Get the Event object by eventId
     * @param eventId the id of the event
     * @return an Event object
     */
    @JsonIgnore
    public Event getEventByEventId(String eventId) {
        for (Event event:this.eventList) {
            if (event.getEventId().equals(eventId)) {
                log.info("Event '{}' found in User '{}'", eventId, this.uid);
                return event;
            }
        }
        throw new EntityNotFoundException("Event '" + eventId + "' not found in User '" + this.uid + "'");
    }

    /**
     * Get the Task object by taskId
     * @param taskId the id of the task
     * @return a Task object
     */
    @JsonIgnore
    public Task getTaskByTaskId(String taskId) {
        for (Task task:this.taskList) {
            if (task.getTaskId().equals(taskId)) {
                log.info("Task '{}' found in User '{}'", taskId, this.uid);
                return task;
            }
        }
        throw new EntityNotFoundException("Task '" + taskId + "' not found in User '" + this.uid + "'");
    }

    /**
     * Get the Record object by recordId
     * @param recordId the id of the record
     * @return a Record object
     */
    @JsonIgnore
    public Record getRecordByRecordId(String recordId) {
        for (Record record:this.recordList) {
            if (record.getRecordId().equals(recordId)) {
                log.info("Record '{}' found in User '{}'", recordId, this.uid);
                return record;
            }
        }
        throw new EntityNotFoundException("Record '" + recordId + "' not found in User '" + this.uid + "'");
    }

    /**
     * Get all Event objects in a given date
     * @param date 'yyyy-MM-dd'
     * @return a list of Event objects
     */
    @JsonIgnore
    public List<Event> getEventsByDate(String date) {
        List<Event> eventList = new ArrayList<>();
        for (Event event:this.eventList) {
            String from = event.getStartDateTime().substring(0, DATE_PATTERN.length());
            String to = event.getEndDateTime().substring(0, DATE_PATTERN.length());
            if (from.compareTo(date) <= 0 && to.compareTo(date) >= 0) {
                eventList.add(event);
            }
        }
        eventList.sort(Comparator.comparing(Event::getStartDateTime));
        return eventList;
    }

    /**
     * Get all Task objects in a given date
     * @param date 'yyyy-MM-dd'
     * @return a list of Task objects
     */
    @JsonIgnore
    public List<Task> getTasksByDate(String date) {
        List<Task> taskList = new ArrayList<>();
        for (Task task:this.taskList) {
            if (task.getDueDate().equals(date)) {
                taskList.add(task);
            }
        }
        taskList.sort(Comparator.comparing(Task::getDueDate));
        return taskList;
    }

    /**
     * Get all Booking objects in a given date
     * @param date 'yyyy-MM-dd'
     * @return a list of Booking objects
     */
    public List<Booking> getBookingsByDate(String date) {
        List<Booking> bookingList = new ArrayList<>();
        for (Booking booking:this.bookingList) {
            if (date.equals(booking.getStart_time().substring(0, DATE_PATTERN.length())) ||
                    date.equals(booking.getEnd_time().substring(0, DATE_PATTERN.length()))) {
                bookingList.add(booking);
            }
        }
        bookingList.sort(Comparator.comparing(Booking::getStart_time));
        return bookingList;
    }

    @JsonIgnore
    public List<Map<String, Object>> getBookingsByMonth(String month) {
        // get all dates in the given month
        List<String> dateSpan = getDateSpanOfMonth(month);
        Map<String, List<Booking>> cal = new HashMap<>();
        for (String date:dateSpan) {
            cal.put(date, new ArrayList<>());
        }
        for (Booking b:this.bookingList) {
            String startDate = b.getStart_time().substring(0, DATE_PATTERN.length());
            String endDate = b.getEnd_time().substring(0, DATE_PATTERN.length());
            if (cal.containsKey(startDate)) {
                cal.get(startDate).add(b);
            }
            if (!endDate.equals(startDate) && cal.containsKey(endDate)) {
                cal.get(endDate).add(b);
            }
        }
        // create a list of map, each map contains 'date' and 'bookingList' for a specific day in the given month
        List<Map<String, Object>> list = new ArrayList<>();
        for (String date:dateSpan) {
            Map<String, Object> map = new HashMap<>();
            List<Booking> bookingList = cal.get(date);
            bookingList.sort(Comparator.comparing(Booking::getStart_time));
            map.put("date", date);
            map.put("bookingList", bookingList);
            list.add(map);
        }
        return list;
    }

    /**
     * Get a list of Calendar objects containing information of events and tasks
     * @param month 'yyyy-MM'
     * @return a list of maps, where each map represents a calendar day in the given month
     *          and contains a date('yyyy-MM-dd'), an eventList(a list of Event objects),
     *          a taskList(a list of Task objects)
     */
    @JsonIgnore
    public List<Map<String, Object>> getCalByMonth(String month) {
        // get all dates in the given month
        List<String> dateSpan = getDateSpanOfMonth(month);
        Map<String, List<Event>> eventCal = new HashMap<>();
        Map<String, List<Task>> taskCal = new HashMap<>();
        Map<String, List<Booking>> bookingCal = new HashMap<>();
        // initialize the event and task calendar map (key: date, value: a list of event/task entities)
        for (String date:dateSpan) {
            eventCal.put(date, new ArrayList<>());
            taskCal.put(date, new ArrayList<>());
            bookingCal.put(date, new ArrayList<>());
        }
        for (Event event:eventList) {
            // extract 'yyyy-MM-dd' from startDateTime and endDateTime
            String from = event.getStartDateTime().substring(0, DATE_PATTERN.length());
            String to = event.getEndDateTime().substring(0, DATE_PATTERN.length());
            // get the date span of this event within the given month
            List<String> eventDateSpan = getDateSpan(from, to, month);
            // add the event into each day it spans
            for (String d:eventDateSpan) {
                eventCal.get(d).add(event);
            }
        }
        for (Task task:taskList) {
            String dueDate = task.getDueDate();
            if (taskCal.containsKey(dueDate)) {
                taskCal.get(dueDate).add(task);
            }
        }
        for (Booking booking:bookingList) {
            String startDate = booking.getStart_time().substring(0, DATE_PATTERN.length());
            String endDate = booking.getEnd_time().substring(0, DATE_PATTERN.length());
            if (bookingCal.containsKey(startDate)) {
                bookingCal.get(startDate).add(booking);
            }
            if (!endDate.equals(startDate) && bookingCal.containsKey(endDate)) {
                bookingCal.get(endDate).add(booking);
            }
        }
        // create a list of map, each map contains 'date', 'eventList', and 'taskList' for a specific day in the given month
        List<Map<String, Object>> list = new ArrayList<>();
        for (String date:dateSpan) {
            Map<String, Object> map = new HashMap<>();
            List<Event> eventList = eventCal.get(date);
            List<Task> taskList = taskCal.get(date);
            List<Booking> bookingList = bookingCal.get(date);
            // sort the events in this day by startDateTime
            eventList.sort(Comparator.comparing(Event::getStartDateTime));
            // sort the tasks in this day by dueDate
            taskList.sort(Comparator.comparing(Task::getDueDate));
            // sort the bookings in this day by start_time
            bookingList.sort(Comparator.comparing(Booking::getStart_time));
            map.put("date", date);
            map.put("eventList", eventList);
            map.put("taskList", taskList);
            map.put("bookingList", bookingList);
            list.add(map);
        }
        return list;
    }

    /**
     * Get all unchecked tasks due before today
     * @param today 'yyyy-MM-dd'
     * @return a list of overdue Task objects
     */
    @JsonIgnore
    public List<Task> getOverdueTasks(String today) {
        List<Task> list = new ArrayList<>();
        for (Task task:taskList) {
            if (task.getDueDate().compareTo(today) < 0 && !task.isChecked()) {
                list.add(task);
            }
        }
        return list;
    }

    /**
     * Get all unchecked tasks due today
     * @param today 'yyyy-MM-dd'
     * @return a list of upcoming Task objects
     */
    @JsonIgnore
    public List<Task> getUpcomingTasks(String today) {
        List<Task> list = new ArrayList<>();
        for (Task task:taskList) {
            if (task.getDueDate().compareTo(today) == 0 && !task.isArchived()) {
                list.add(task);
            }
        }
        return list;
    }
}
