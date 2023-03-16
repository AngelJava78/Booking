package com.angeljava.test.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.angeljava.test.booking.domain.Booking;

public interface BookingRepository extends JpaRepository<Booking, String> {

}
