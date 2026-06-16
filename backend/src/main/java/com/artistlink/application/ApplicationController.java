package com.artistlink.application;

import com.artistlink.application.dto.ApplicationResponse;
import com.artistlink.application.dto.CreateApplicationRequest;
import com.artistlink.application.dto.UpdateApplicationStatusRequest;
import com.artistlink.auth.CurrentActor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    /** Artist submits an application. */
    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponse> submit(@Valid @RequestBody CreateApplicationRequest req) {
        return ResponseEntity.ok(service.submit(CurrentActor.require(), req));
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(CurrentActor.require(), id));
    }

    /** Artist withdraws their application. */
    @PostMapping("/applications/{id}/withdraw")
    public ResponseEntity<ApplicationResponse> withdraw(@PathVariable UUID id) {
        return ResponseEntity.ok(service.withdraw(CurrentActor.require(), id));
    }

    /** Venue updates application status (REVIEWING / ACCEPTED / REJECTED). */
    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationResponse> review(@PathVariable UUID id,
                                                      @Valid @RequestBody UpdateApplicationStatusRequest req) {
        return ResponseEntity.ok(service.review(CurrentActor.require(), id, req.status()));
    }

    /** Artist: my applications. */
    @GetMapping("/applications/mine")
    public ResponseEntity<List<ApplicationResponse>> mine() {
        return ResponseEntity.ok(service.mine(CurrentActor.require()));
    }

    /** Venue: all applications received across my opportunities. */
    @GetMapping("/applications/received")
    public ResponseEntity<List<ApplicationResponse>> received() {
        return ResponseEntity.ok(service.receivedByVenue(CurrentActor.require()));
    }

    /** Venue: applications for one opportunity. */
    @GetMapping("/opportunities/{opportunityId}/applications")
    public ResponseEntity<List<ApplicationResponse>> forOpportunity(@PathVariable UUID opportunityId) {
        return ResponseEntity.ok(service.forOpportunity(CurrentActor.require(), opportunityId));
    }
}
