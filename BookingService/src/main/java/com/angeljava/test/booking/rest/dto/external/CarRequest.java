package com.angeljava.test.booking.rest.dto.external;

import java.util.Date;
import lombok.Data;

@Data
public class CarRequest {

    private String modelName;

    private Date pickUpDate;

    private Date returnDate;

    private float price;
}
