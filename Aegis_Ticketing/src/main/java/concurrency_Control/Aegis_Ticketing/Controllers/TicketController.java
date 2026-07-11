package concurrency_Control.Aegis_Ticketing.Controllers;

import concurrency_Control.Aegis_Ticketing.Entity.Booking;
import concurrency_Control.Aegis_Ticketing.Services.TicketService;
import concurrency_Control.Aegis_Ticketing.DTO.BookingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest request) {

        log.info("Attempting to book seat: {} for user: {}", request.getSeatNumber(), request.getUserId());

        try {
            Booking booking = ticketService.bookSeat(request.getSeatNumber(), request.getUserId());

            log.info("Successfully booked seat: {} for user: {}", request.getSeatNumber(), request.getUserId());

            return ResponseEntity.ok(booking);

        } catch (RuntimeException e) {

            log.warn("Booking failed for user {}: {}", request.getUserId(), e.getMessage());

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelTicket(@PathVariable Long bookingId) {
        try {
            ticketService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully.");
        } catch (RuntimeException e) {
            log.warn("Cancellation failed for ID {}: {}", bookingId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}