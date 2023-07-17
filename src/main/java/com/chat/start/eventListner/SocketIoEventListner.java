package com.chat.start.eventListner;

import com.chat.start.service.SocketIoService;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketIoEventListner {

    private final SocketIOServer server;
    private final SocketIoService service;
    private Gson gson=new Gson();
    SocketIoEventListner(SocketIOServer server,SocketIoService service){
        this.service=service;
        this.server=server;

        // on connect
        server.addConnectListener(onConnected());
        // on disconnected
        server.addDisconnectListener(onDisconnected());
        // on chat send
        server.addEventListener("chat",String.class,sendChat());
        // on joining room
        server.addEventListener("join-room",String.class,joinRoom());
    }

    private DataListener<String> sendChat() {
        return (socketIOClient, payload, ackRequest) -> service.sendChat(socketIOClient,payload,ackRequest);
    }

    private DataListener<String> joinRoom() {
        return (socketIOClient, payload, ackRequest) -> service.joinedRoom(socketIOClient,payload,ackRequest);
    }

    private DisconnectListener onDisconnected() {
        return socketIOClient -> service.onDisconnect(socketIOClient);
    }

    private ConnectListener onConnected() {
        return socketIOClient -> service.onConnect(socketIOClient);
    }
}
