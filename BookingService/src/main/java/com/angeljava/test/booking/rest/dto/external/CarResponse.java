package com.angeljava.test.booking.rest.dto.external;

import java.util.Date;
import lombok.Data;

@Data
public class CarResponse {
	private Long id;

	private Long externalId;
	
    private String modelName;

    private Date pickUpDate;

    private Date returnDate;

    private float price;
    
	private String status;

	private String serialNumber;
}
