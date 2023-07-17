package com.chat.start.repo;

import com.chat.start.model.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoomRepo extends JpaRepository<UserRoom,Long> {
}
