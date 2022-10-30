package com.example.pets_backend.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class NtfTask {

    @Id
    private String ntfId = NanoIdUtils.randomNanoId();

    @NotNull
    private String uid;

    @NonNull
    @ElementCollection
    private List<String> taskIdList = new ArrayList<>();

    @NotNull
    private LocalDateTime ntfTime;

    @NotNull
    @Column(length = 32)
    private String ntfType;

    @NonNull
    @Column(length = 10)
    private String ntfDate;     // yyyy-MM-dd

    private boolean done = false;
}
