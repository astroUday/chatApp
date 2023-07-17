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
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "chatUser",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Message> messageList=new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<UserRoom> userRoomList=new ArrayList<>();
}
