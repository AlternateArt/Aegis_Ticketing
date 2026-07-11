package concurrency_Control.Aegis_Ticketing.Repositories;

import concurrency_Control.Aegis_Ticketing.Entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findBySeatNumber(String seatNumber);
}