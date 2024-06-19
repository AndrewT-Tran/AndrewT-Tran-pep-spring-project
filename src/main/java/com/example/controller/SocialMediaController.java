package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use
 * the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations.
 * You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
@RestController
@RequestMapping()
public class SocialMediaController {

    private final AccountService accountService;

    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        try {
            Account savedAccount = accountService.save(account);
            return ResponseEntity.ok(savedAccount);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatus()).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        try {
            Account loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
            return ResponseEntity.ok(loggedInAccount);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatus()).body(null);
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        try {
            return messageService.createMessage(message);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).body(null);
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.findAll();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{message_id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("message_id") Integer messageId) {
        return messageService.findById(messageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok().build());
    }

    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<String> deleteMessageById(@PathVariable("message_id") Integer messageId) {
        try {
            Integer rowsUpdated = messageService.deleteById(messageId);

            if (rowsUpdated > 0) {
                return ResponseEntity.ok(rowsUpdated.toString());
            } else {
                return ResponseEntity.ok("");
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<Integer> updateById(@PathVariable Integer message_id, @RequestBody Message message) {
        if (messageService.updateById(message_id, message) == 1) {
            return new ResponseEntity<Integer>(1, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable("account_id") Integer userId) {
        List<Message> messages = messageService.findAllMessagesByUser(userId);
        return ResponseEntity.ok(messages);
    }

}
