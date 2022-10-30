package com.example.pets_backend.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class NtfEvent {

    @Id
    private String ntfId = NanoIdUtils.randomNanoId();

    @NotNull
    private String uid;

    @NonNull
    private String eventId;

    @NotNull
    private LocalDateTime ntfTime;

    private boolean done = false;
}
