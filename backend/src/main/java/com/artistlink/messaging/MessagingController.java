package com.artistlink.messaging;

import com.artistlink.auth.CurrentActor;
import com.artistlink.messaging.dto.ConversationResponse;
import com.artistlink.messaging.dto.MessageResponse;
import com.artistlink.messaging.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversations")
public class MessagingController {

    private final MessagingService service;

    public MessagingController(MessagingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> mine() {
        return ResponseEntity.ok(service.myConversations(CurrentActor.require()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getConversation(CurrentActor.require(), id));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageResponse>> messages(@PathVariable UUID id) {
        return ResponseEntity.ok(service.messages(CurrentActor.require(), id));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> send(@PathVariable UUID id,
                                                @Valid @RequestBody SendMessageRequest req) {
        return ResponseEntity.ok(service.send(CurrentActor.require(), id, req.content()));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable UUID id) {
        service.markRead(CurrentActor.require(), id);
        return ResponseEntity.noContent().build();
    }
}
