package concurrency_Control.Aegis_Ticketing.Repositories;

import concurrency_Control.Aegis_Ticketing.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}