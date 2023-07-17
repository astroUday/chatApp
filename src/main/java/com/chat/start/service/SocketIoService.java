package com.chat.start.service;

import com.chat.start.model.Message;
import com.chat.start.model.Room;
import com.chat.start.model.UserRoom;
import com.chat.start.model.Users;
import com.chat.start.repo.MessageRepo;
import com.chat.start.repo.RoomRepo;
import com.chat.start.repo.UserRepo;
import com.chat.start.repo.UserRoomRepo;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SocketIoService {

    private final UserRepo userRepo;
    private final UserRoomRepo userRoomRepo;
    private final RoomRepo roomRepo;
    private final MessageRepo messageRepo;
    private final SocketIOServer server;
    private Gson gson=new Gson();

    // methods
    public void onConnect(SocketIOClient socketIOClient) {

        var id= socketIOClient.getSessionId();
        socketIOClient.sendEvent("active",new HashMap<>().put(id,true));
    }

    public void onDisconnect(SocketIOClient socketIOClient) {

        var id= socketIOClient.getSessionId();
        socketIOClient.sendEvent("active",new HashMap<>().put(id,false));
    }

    public void joinedRoom(SocketIOClient socketIOClient, String payload, AckRequest ackRequest) {

        // get data from payload and client
        JsonObject jsonObject=gson.fromJson(payload, JsonObject.class);
        Long id= jsonObject.get("id").getAsLong();
        Long roomId= jsonObject.get("roomId").getAsLong();
//        String message=jsonObject.get("message").getAsString();
//        String hostId=jsonObject.get("room-host-id").getAsString();

        socketIOClient.joinRoom(roomId.toString());
        ackRequest.sendAckData("ok");
        server.getRoomOperations(roomId.toString()).sendEvent("messages","User "+id+ " joined");

    }

    public void sendChat(SocketIOClient socketIOClient, String payload, AckRequest ackRequest) {

        // finding the roomId and message form payload
        JsonObject jsonObject=gson.fromJson(payload, JsonObject.class);
        Long id= jsonObject.get("id").getAsLong();
        Long roomId= jsonObject.get("roomId").getAsLong();
        String message=jsonObject.get("message").getAsString();
        System.out.println("roomId is :" + roomId.toString()+"k");
        AtomicBoolean flag= new AtomicBoolean(true);

        // checking if the user is joined to the room and broadcasting message to that room
        socketIOClient.getAllRooms().forEach(room->{
            System.out.println("room is :" +room+"k");
            if(roomId.toString().equals(room)){

                //saving the info
                try{
                    saveMessageInfo(id,message,roomId);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
               // broadcasting message to room
                server.getRoomOperations(roomId.toString()).sendEvent("message",payload);
                flag.set(false);
            }
        });
        if(flag.get()) throw new IllegalArgumentException("user is not a part of room with roomId :"+roomId);
    }
    @Transactional
    private void saveMessageInfo(Long senderId, String content, Long roomId) {
        Users users=userRepo.findById(senderId).get();
        Room room=roomRepo.findById(roomId).get();

        Message message=new Message();
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setChatRoom(room);
        message.setChatUser(users);
        messageRepo.save(message);

        //adding to user
            List<Message> messageList= users.getMessageList();
            messageList.add(message);
            users.setMessageList(messageList);
            userRepo.save(users);

        //adding to room
            List<Message> messagesList=room.getMessagesList();
            messagesList.add(message);
            room.setMessagesList(messagesList);
            roomRepo.save(room);

        //saving using userRoom
            UserRoom userRoom=new UserRoom();
            userRoom.setUser(users);
            userRoom.setRoom(room);
            userRoomRepo.save(userRoom);

    }
}
