package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;




@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final AccountService accountService;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountService accountService) {
        this.messageRepository = messageRepository;
        this.accountService = accountService;
    }


    public Optional<Message> findById(Integer messageId){
        return messageRepository.findById(messageId);
    }

    public List<Message> findAll(){
        return messageRepository.findAll();
    }

    public ResponseEntity<Message> createMessage(Message message) {
        validateMessageForCreation(message);
    
        if (accountService.findById(message.getPostedBy()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user (posted_by) specified in the message");
        }
    
        Message createdMessage = messageRepository.save(message);
    
        return ResponseEntity.ok(createdMessage);
    }

    public ResponseEntity<Integer> updateMessageText(Integer messageId, String newMessageText) {
        try {
            Optional<Message> optionalMessage = messageRepository.findById(messageId);

            if (optionalMessage.isPresent()) {
                Message existingMessage = optionalMessage.get();

                validateMessageText(newMessageText);

                existingMessage.setMessageText(newMessageText);

                messageRepository.save(existingMessage);

                return ResponseEntity.ok(1);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message ID");
            }
        } catch (Exception e) {
            logger.error("Failed to update message text for messageId: " + messageId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
        }
    }


    

    private void validateMessageForCreation(Message message) {
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be blank");
        }

        if (message.getMessageText().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text must be under 255 characters");
        }
    }
    private void validateMessageText(String newMessageText) {
        if (newMessageText == null || newMessageText.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New message text cannot be blank");
        }
        if (newMessageText.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New message text must be under 255 characters");
        }
    }


    public class DeleteResponse {
        private int rowsUpdated;

        public DeleteResponse(int rowsUpdated) {
            this.rowsUpdated = rowsUpdated;
        }

        public int getRowsUpdated() {
            return rowsUpdated;
        }
    }

    public Integer deleteById(Integer messageId) {
        try {
            messageRepository.deleteById(messageId);
            return 1;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    } 


    public List<Message> findAllMessagesByUser(Integer userId){
        if(accountService.findById(userId).isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return messageRepository.findByPostedBy(userId);
    }


}
