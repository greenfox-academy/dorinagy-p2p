package com.greenfox.dorinagy.chatapp.controller;

import com.greenfox.dorinagy.chatapp.model.*;
import com.greenfox.dorinagy.chatapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nagy Dóra on 2017.05.18..
 */
@RestController
public class ChatAppRestController {

  @Autowired
  MessageRepository messagesRepository;

  @Autowired
  ChatAppMessage chatAppMessage;

  @Autowired
  StatusOk statusOk;

  @Autowired
  StatusError statusError;

  @Autowired
  MessageResponse messageResponse;

  RestTemplate restTemplate = new RestTemplate();

  String url = "https://zsiguli-chat-app.herokuapp.com/api/message/receive";

  @GetMapping(value="/api/messages")
  public Object getMessages() {
    return messageResponse;
  }

  @PostMapping(value="/api/messages")
  public Object postMessages() {
    return messageResponse;
  }

  @CrossOrigin("*")
  @RequestMapping(value = "/api/message/receive")
  public Object receiveMessage(@RequestBody JsonReceived jsonReceived) {

    List<String> errors = new ArrayList<>();

    if (StringUtils.isEmpty(jsonReceived.getMessage().getText())) {
      errors.add("message.text");
    }
    if (StringUtils.isEmpty(jsonReceived.getMessage().getUsername())) {
      errors.add("message.username");
    }
    if (StringUtils.isEmpty(jsonReceived.getMessage().getTimestamp())) {
      errors.add("message.timestamp");
    }
    if (StringUtils.isEmpty(jsonReceived.getMessage().getId())) {
      errors.add("message.id");
    }
    if (StringUtils.isEmpty(jsonReceived.getClient().getId())) {
      errors.add("client.id");
    }

    if (!jsonReceived.getClient().getId().equals("dorinagy")) {

      if (errors.size() == 0) {
        statusOk.setStatus("ok");
        messagesRepository.save(jsonReceived.getMessage());
        restTemplate.postForObject(url, jsonReceived, StatusOk.class);
      } else {
        statusError.setStatus("error");
        statusError.setMessage(errors);
      }
    } else {
      statusOk.setStatus("ok");
    }
    return (errors.size() == 0) ? statusOk : statusError;
  }
}
