package com.angeljava.test.booking.rest.dto.external;

import java.util.Date;
import lombok.Data;

@Data
public class FlightRequest {
	private Date departureTime;

	private Date arrivalTime;

	private String seatClass;

	private float price;
}
