package concurrency_Control.Aegis_Ticketing.DTO;

import lombok.Data;

@Data
public class BookingRequest {
    private String seatNumber;
    private String userId;
}