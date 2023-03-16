package com.angeljava.test.booking.rest.dto.external;

import lombok.Data;

@Data
public class PaymentRequest {

	private String cardNumber;

	private String paymentMethod;

	private float amount;
}
