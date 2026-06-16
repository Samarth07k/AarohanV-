package com.artistlink.negotiation;

import com.artistlink.auth.CurrentActor;
import com.artistlink.negotiation.dto.NegotiationResponse;
import com.artistlink.negotiation.dto.SendOfferRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/negotiations")
public class NegotiationController {

    private final NegotiationService service;

    public NegotiationController(NegotiationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<NegotiationResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(CurrentActor.require(), id));
    }

    @GetMapping("/by-application/{applicationId}")
    public ResponseEntity<NegotiationResponse> byApplication(@PathVariable UUID applicationId) {
        return ResponseEntity.ok(service.getByApplication(CurrentActor.require(), applicationId));
    }

    @PostMapping("/{id}/offers")
    public ResponseEntity<NegotiationResponse> sendOffer(@PathVariable UUID id,
                                                         @Valid @RequestBody SendOfferRequest req) {
        return ResponseEntity.ok(service.sendOffer(CurrentActor.require(), id, req));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<NegotiationResponse> accept(@PathVariable UUID id) {
        return ResponseEntity.ok(service.accept(CurrentActor.require(), id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<NegotiationResponse> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(service.reject(CurrentActor.require(), id));
    }
}
