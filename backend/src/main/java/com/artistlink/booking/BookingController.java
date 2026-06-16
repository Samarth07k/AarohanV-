package com.artistlink.booking;

import com.artistlink.auth.CurrentActor;
import com.artistlink.booking.dto.BookingResponse;
import com.artistlink.booking.dto.UpdateBookingStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping("/mine")
    public ResponseEntity<List<BookingResponse>> mine() {
        return ResponseEntity.ok(service.mine(CurrentActor.require()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(CurrentActor.require(), id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(@PathVariable UUID id,
                                                        @Valid @RequestBody UpdateBookingStatusRequest req) {
        return ResponseEntity.ok(service.updateStatus(CurrentActor.require(), id, req.status()));
    }
}
