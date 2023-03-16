package com.angeljava.test.booking.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "car")
public class Car {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;

	private Long externalId;

	private String modelName;

	private Date pickUpDate;

	private Date returnDate;

	private float price;

	@Column(name = "status", nullable = true)
	private String status;

	@Column(name = "serialNumber", nullable = true)
	private String serialNumber;
}
