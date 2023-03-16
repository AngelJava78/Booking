package com.angeljava.test.booking.rest.dto.external;
import java.util.Date;
import lombok.Data;

@Data
public class FlightResponse {
	private Long id;

	private Long externalId;
	
	private Date departureTime;

	private Date arrivalTime;

	private String seatClass;

	private float price;

	private String status;
}
