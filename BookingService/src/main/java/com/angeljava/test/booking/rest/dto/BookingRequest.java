package com.angeljava.test.booking.rest.dto;

import com.angeljava.test.booking.rest.dto.external.CarRequest;
import com.angeljava.test.booking.rest.dto.external.FlightRequest;
import com.angeljava.test.booking.rest.dto.external.HotelRoomRequest;
import com.angeljava.test.booking.rest.dto.external.PaymentRequest;

import lombok.Data;

@Data
public class BookingRequest {
	private String userId;
	private FlightRequest flight;
	private CarRequest car;
	private HotelRoomRequest hotelRoom;
	private PaymentRequest payment;
}
