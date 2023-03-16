package com.angeljava.test.booking.rest.dto.external;

import lombok.Data;

@Data
public class PaymentResponse {
	private Long id;

	private Long externalId;

	private String paymentMethod;

	private float amount;

	private String status;

	private boolean confirmado;
}
