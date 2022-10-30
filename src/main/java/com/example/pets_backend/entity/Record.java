package com.example.pets_backend.entity;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashMap;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Record {

    @Id
    private String recordId = NanoIdUtils.randomNanoId();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "uid", nullable = false, foreignKey = @ForeignKey(name = "fk_record_uid"))
    private User user;

    @NonNull
    @Column(length = 16)
    private String recordType;      // Invoice/Medication/Vaccination

    @NonNull
    @Column(length = 64)
    private String recordTitle;

    @NonNull
    @Column(length = 16)
    private String date;        // yyyy-MM-dd

    @Column
    private String fileDir;

    @Column(length = 8)
    private String fileFormat;      // pdf/img

    @Column(length = 64)
    private String vacType;     // vaccination type

    @NonNull
    @Column
    private String petId;

    @Column(length = 32)
    private String petName;

    @Column
    private String petAvatar;

    @JsonIgnore
    public LinkedHashMap<String, Object> getRecordAb() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("recordId", recordId);
        map.put("recordType", recordType);
        map.put("recordTitle", recordTitle);
        return map;
    }
}
