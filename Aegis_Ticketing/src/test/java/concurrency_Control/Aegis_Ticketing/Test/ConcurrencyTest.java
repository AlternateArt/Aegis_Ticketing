package concurrency_Control.Aegis_Ticketing.Test;

import concurrency_Control.Aegis_Ticketing.DTO.BookingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConcurrencyTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testConcurrentBooking() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        String seatNumber = "A13";

        for (int i = 0; i < numberOfThreads; i++) {
            int userId = i;
            executor.execute(() -> {
                try {
                    BookingRequest request = new BookingRequest();
                    request.setSeatNumber(seatNumber);
                    request.setUserId("User-" + userId);

                    ResponseEntity<String> response = restTemplate.postForEntity("/api/tickets/book", request, String.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, successCount.get(), "Only one user should be able to book the seat!");
        assertEquals(numberOfThreads - 1, failureCount.get(), "The rest should have failed!");
    }
}