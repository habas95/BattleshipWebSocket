package com.eikholm.websocketprep.controller;

import com.eikholm.websocketprep.model.Greeting;
import com.eikholm.websocketprep.model.WebSocketCoordinate;
import com.eikholm.websocketprep.model.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Set;

@Controller
public class GreetingController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Set<String> connectedUsers = new HashSet<>();

    public GreetingController( SimpMessagingTemplate s) {
        simpMessagingTemplate = s;
    }
    
    @MessageMapping("/register")
    public void registerUser(String userName){
        System.out.println("register called with "+ userName);
        if(!connectedUsers.contains(userName)){ // only allow unique usernames
            connectedUsers.add(userName);
        }
    }

    @MessageMapping("/hello")
    public void processMessageFromClient(WebSocketMessage message){
        System.out.println("controller kaldet til: " + message.toWhom + " fra: " + message.fromWho + " message: " + message.message );
        Greeting greeting = new Greeting(message.fromWho + ": " + message.message);
        if (message.toWhom.equals("")) {
            connectedUsers.forEach(user -> {
                simpMessagingTemplate.convertAndSendToUser(user, "/userMessage", greeting); // genial metode !
            });
        }else{
            Greeting whisper = new Greeting(message.fromWho + " whispered to " + message.toWhom + ": " + message.message);
            connectedUsers.forEach(user -> {
                if (user.equals(message.fromWho) || user.equals(message.toWhom)) {
                    simpMessagingTemplate.convertAndSendToUser(user, "/userMessage", whisper);
                }
            });
        }
    }

    @MessageMapping("/userReg")
    public void processUser(WebSocketMessage message){
        System.out.println("processUser kaldet med user: " + message.fromWho);
        Greeting greeting = new Greeting("User " + message.fromWho + " registered");
        connectedUsers.forEach(user -> {
            if (!user.equals(message.fromWho))
                simpMessagingTemplate.convertAndSendToUser(user, "/userMessage", greeting); // genial metode !
        });
    }

    @MessageMapping("/userDis")
    public void processDisconnect(WebSocketMessage message){
        System.out.println("processDisconnect kaldet med user: " + message.fromWho);
        Greeting greeting = new Greeting("User " + message.fromWho + " disconnected");
        connectedUsers.forEach(user -> {
            if (!user.equals(message.fromWho))
                simpMessagingTemplate.convertAndSendToUser(user, "/userMessage", greeting); // genial metode !
        });
    }

    @MessageMapping("/cnt")
    public void processCnt(WebSocketMessage message){
        System.out.println("processCnt kaldet med message: " + message.message + ", fra: " + message.fromWho);
        Greeting greeting = new Greeting(message.message);
        connectedUsers.forEach(user -> {
            if (!user.equals(message.fromWho))
            simpMessagingTemplate.convertAndSendToUser(user, "/userMessage", greeting); // genial metode !
        });
    }

    @MessageMapping("/coordinates")
    public void processCoordinatesFromClient(WebSocketCoordinate coordinates){
        System.out.println("Coordinate controller kaldet til: " + coordinates.letterCoordinate + " : " + coordinates.numberCoordinate);
        Greeting greeting = new Greeting(coordinates.letterCoordinate + "" + coordinates.numberCoordinate);
        connectedUsers.forEach(user -> {
            if (!user.equals(coordinates.fromWho)) {
                simpMessagingTemplate.convertAndSendToUser(user, "/msg", greeting); // genial metode !
            }
        });
    }

    @MessageMapping("/boom")
    public void processBoom(WebSocketMessage message){
        System.out.println("Boom controller kaldet med message: " + message.message);
        Greeting greeting = new Greeting(message.message);
        String userSend = message.fromWho;
        connectedUsers.forEach(user -> {
            if (!user.equals(userSend)) {
                simpMessagingTemplate.convertAndSendToUser(user, "/msg", greeting); // genial metode !
            }
        });
    }
}