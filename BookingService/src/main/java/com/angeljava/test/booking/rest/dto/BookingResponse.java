package com.angeljava.test.booking.rest.dto;

import java.util.Date;

import com.angeljava.test.booking.rest.dto.external.CarResponse;
import com.angeljava.test.booking.rest.dto.external.FlightResponse;
import com.angeljava.test.booking.rest.dto.external.HotelRoomResponse;
import com.angeljava.test.booking.rest.dto.external.PaymentResponse;

import lombok.Data;

@Data
public class BookingResponse {

	private Long id;

	private String status;

	private float total;

	private String userId;

	private Date creationDate;

	private Date updatedDate;

	private FlightResponse flight;

	private CarResponse car;

	private HotelRoomResponse hotelRoom;

	private PaymentResponse payment;
}
