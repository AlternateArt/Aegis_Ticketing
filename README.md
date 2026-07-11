# AegisTicket: Concurrency-Safe Booking System

A robust, high-concurrency seat reservation engine built to handle simultaneous user requests while ensuring absolute data integrity. This project demonstrates the implementation of **distributed pessimistic locking** to eliminate race conditions in a microservices-compatible architecture.

## 🚀 The Engineering Problem
In a high-traffic ticketing environment, "Double-Booking" (two users booking the same seat at the same millisecond) is a critical failure. Standard database transactions alone are often insufficient when scaling horizontally. 

**AegisTicket** solves this by offloading the locking mechanism to **Redis** (using Redisson), ensuring that only one thread can acquire the lock for a specific seat at any given time, thus enforcing atomic operations across the system.

## 🛠 Tech Stack
* **Java 21**
* **Spring Boot 3.3.0**
* **MySQL** (Relational Data Persistence)
* **Redis + Redisson** (Distributed Locking Mechanism)
* **Spring Data JPA** (ORM)
* **Lombok** (Boilerplate reduction)
* **JUnit 5 / TestRestTemplate** (Concurrency Testing)

## 🔑 Key Engineering Highlights
* **Distributed Pessimistic Locking:** Implemented Redisson locks to prevent race conditions at the application level, ensuring thread safety before hitting the database.
* **Atomic Transactions:** Optimized MySQL schema and JPA transactions to enforce strict consistency for `Booking` and `Seat` resources.
* **Concurrency-Safe API:** Developed a RESTful API that handles high-throughput requests gracefully, rejecting conflicting bookings in real-time.
* **Integration Testing:** Authored automated concurrency tests using `ExecutorService` and `CountDownLatch` to simulate 10+ simultaneous users, proving the system effectively handles high-traffic contention.

## 📦 Getting Started

### Prerequisites
* Docker (for Redis)
* JDK 21
* MySQL Server

### Setup
1. **Start Redis Container:**
   ```bash
   docker run -d --name aegis-redis -p 6379:6379 redis:alpine
