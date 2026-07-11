package concurrency_Control.Aegis_Ticketing.Services;

import concurrency_Control.Aegis_Ticketing.Entity.Booking;
import concurrency_Control.Aegis_Ticketing.Entity.BookingStatus;
import concurrency_Control.Aegis_Ticketing.Entity.Seat;
import concurrency_Control.Aegis_Ticketing.Entity.SeatStatus;
import concurrency_Control.Aegis_Ticketing.Repositories.BookingRepository;
import concurrency_Control.Aegis_Ticketing.Repositories.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public Booking bookSeat(String seatNumber, String userId) {

        String lockKey = "seat_lock:" + seatNumber;
        RLock lock = redissonClient.getLock(lockKey);

        try {

            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (isLocked) {
                try {

                    Seat seat = seatRepository.findBySeatNumber(seatNumber)
                            .orElseThrow(() -> new RuntimeException("Seat not found"));

                    if (seat.getStatus() != SeatStatus.AVAILABLE) {
                        throw new RuntimeException("Sorry, this seat was just booked by someone else!");
                    }

                    seat.setStatus(SeatStatus.BOOKED);
                    seatRepository.save(seat);

                    Booking booking = new Booking();
                    booking.setSeat(seat);
                    booking.setUserId(userId);
                    booking.setBookingStatus(BookingStatus.CONFIRMED);

                    return bookingRepository.save(booking);

                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Server is busy. Too many users trying to book this seat. Please try again.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Booking process was interrupted");
        }
    }

    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String seatNumber = booking.getSeat().getSeatNumber();
        String lockKey = "seat_lock:" + seatNumber;
        RLock lock = redissonClient.getLock(lockKey);

        try {

            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                try {

                    Seat seat = booking.getSeat();
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seatRepository.save(seat);

                    bookingRepository.delete(booking);

                    log.info("Successfully cancelled booking ID: {} for seat: {}", bookingId, seatNumber);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("System busy, please try cancelling again.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cancellation interrupted");
        }
    }
}