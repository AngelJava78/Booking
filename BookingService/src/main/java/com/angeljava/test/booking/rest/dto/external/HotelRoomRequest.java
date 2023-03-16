package com.angeljava.test.booking.rest.dto.external;

import java.util.Date;
import lombok.Data;

@Data
public class HotelRoomRequest {

	private Date checkInTime;

	private Date checkOutTime;

	private String roomType;

	private float price;
}
