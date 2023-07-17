package com.chat.start.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String uuid;

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true,cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Message> messagesList=new ArrayList<>();
    @OneToMany(mappedBy = "room")
    private List<UserRoom> userRoomList=new ArrayList<>();

}

