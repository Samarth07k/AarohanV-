package com.artistlink.opportunity;

import com.artistlink.auth.AuthPrincipal;
import com.artistlink.auth.CurrentActor;
import com.artistlink.common.PageResponse;
import com.artistlink.opportunity.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    private final OpportunityService service;

    public OpportunityController(OpportunityService service) {
        this.service = service;
    }

    /** Venue creates an opportunity. */
    @PostMapping
    public ResponseEntity<OpportunityResponse> create(@Valid @RequestBody CreateOpportunityRequest req) {
        return ResponseEntity.ok(service.create(CurrentActor.require(), req));
    }

    /** Discovery feed of OPEN opportunities. */
    @GetMapping
    public ResponseEntity<PageResponse<OpportunityResponse>> discover(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit) {
        // optional auth: present for artists (enables hasApplied), absent allowed
        AuthPrincipal actor = safeActor();
        return ResponseEntity.ok(service.discover(actor, cursor, limit));
    }

    /** Venue's own opportunities. */
    @GetMapping("/mine")
    public ResponseEntity<List<OpportunityResponse>> mine() {
        return ResponseEntity.ok(service.mine(CurrentActor.require()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(safeActor(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponse> update(@PathVariable UUID id,
                                                      @Valid @RequestBody UpdateOpportunityRequest req) {
        return ResponseEntity.ok(service.update(CurrentActor.require(), id, req));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<OpportunityResponse> close(@PathVariable UUID id) {
        return ResponseEntity.ok(service.close(CurrentActor.require(), id));
    }

    private AuthPrincipal safeActor() {
        try {
            return CurrentActor.require();
        } catch (RuntimeException e) {
            return null;
        }
    }
}
