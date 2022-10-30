package com.example.pets_backend.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    private String taskId = NanoIdUtils.randomNanoId();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "uid", nullable = false, foreignKey = @ForeignKey(name = "fk_task_uid"))
    private User user;

    @NonNull
    @ElementCollection
    @CollectionTable(
            name = "task_petidlist",
            joinColumns = @JoinColumn(name = "task_id")
    )
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<String> petIdList = new ArrayList<>();

    @NonNull
    @Column(length = 64)
    private String taskTitle;

    @NonNull
    @Column(length = 16)
    private String dueDate;         // YYYY-MM-DD HH:mm

    @NonNull
    private boolean checked;

    private boolean archived = false;

    public List<LinkedHashMap<String, Object>> getPetAbList() {
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        for (String petId:this.petIdList) {
            list.add(this.user.getPetByPetId(petId).getPetAb());
        }
        return list;
    }

    @JsonIgnore
    public LinkedHashMap<String, Object> getTaskAb() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("taskId", this.getTaskId());
        map.put("taskTitle", this.getTaskTitle());
        return map;
    }

    @JsonIgnore
    public List<String> getPetNameList() {
        List<String> petNameList = new ArrayList<>();
        for (String petId:this.petIdList) {
            petNameList.add(user.getPetByPetId(petId).getPetName());
        }
        return petNameList;
    }
}
