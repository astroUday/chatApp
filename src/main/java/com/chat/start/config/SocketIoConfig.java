package com.chat.start.config;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.corundumstudio.socketio.Configuration;

@CrossOrigin("*")
@Component
@Log4j2
@RequiredArgsConstructor
public class SocketIoConfig {

    @Value("${socket.port}")
    private int serverPort;
    @Value("${socket.host}")
    private String serverHost;

    private SocketIOServer server;
    @Bean
    public SocketIOServer server(){
        Configuration configuration = new Configuration();
        configuration.setPort(serverPort);
        configuration.setHostname(serverHost);
        server=new SocketIOServer(configuration);
        server.start();
        return server;
    }
    @PreDestroy
    public void stopSocketIoServer(){
        this.server.stop();
    }
}
