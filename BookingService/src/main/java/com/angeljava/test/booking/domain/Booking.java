package com.angeljava.test.booking.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "booking")
public class Booking {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;
	
	private String status;

	private float total;

	private String userId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "carId", referencedColumnName = "id")
	private Car car;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "flightId", referencedColumnName = "id") 
	private Flight flight;
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hotelRoomId", referencedColumnName = "id")
	private HotelRoom hotelRoom;
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "paymentId", referencedColumnName = "id") 
	private Payment payment;

}
