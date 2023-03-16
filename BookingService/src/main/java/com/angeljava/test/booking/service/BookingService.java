package com.angeljava.test.booking.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.angeljava.test.booking.domain.Booking;
import com.angeljava.test.booking.domain.Car;
import com.angeljava.test.booking.domain.Flight;
import com.angeljava.test.booking.domain.HotelRoom;
import com.angeljava.test.booking.domain.Payment;
import com.angeljava.test.booking.repository.BookingRepository;
import com.angeljava.test.booking.rest.dto.BookingRequest;
import com.angeljava.test.booking.rest.dto.BookingResponse;
import com.angeljava.test.booking.rest.dto.external.CarResponse;
import com.angeljava.test.booking.rest.dto.external.FlightResponse;
import com.angeljava.test.booking.rest.dto.external.HotelRoomResponse;
import com.angeljava.test.booking.rest.dto.external.PaymentRequest;
import com.angeljava.test.booking.rest.dto.external.PaymentResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookingService {

	@Value("${car.service.address:http://localhost:8082}")
	private String carServiceUrl;

	@Value("${flight.service.address:http://localhost:8084}")
	private String flightServiceUrl;

	@Value("${hotel.service.address:http://localhost:8083}")
	private String hotelServiceUrl;

	@Value("${payment.service.address:http://localhost:8085}")
	private String paymentServiceUrl;

	@Autowired
	private BookingRepository bookingRepository;

	private final RestTemplate restTemplate = new RestTemplate();

	@Transactional
	public BookingResponse createBooking(BookingRequest request) {
		BookingResponse response = new BookingResponse();
		Booking booking = new Booking();
		booking.setStatus("CREATED");
		float total = request.getCar().getPrice() + request.getFlight().getPrice() + request.getHotelRoom().getPrice();
		booking.setTotal(total);
		booking.setUserId(request.getUserId());

		Car car = new Car();
		car.setExternalId(0L);
		car.setModelName(request.getCar().getModelName());
		car.setPickUpDate(request.getCar().getPickUpDate());
		car.setPrice(request.getCar().getPrice());
		car.setReturnDate(request.getCar().getReturnDate());
		car.setStatus("CREATED");
		car.setSerialNumber("");
		booking.setCar(car);

		Flight flight = new Flight();
		flight.setExternalId(0L);
		flight.setArrivalTime(request.getFlight().getArrivalTime());
		flight.setDepartureTime(request.getFlight().getDepartureTime());
		flight.setPrice(request.getFlight().getPrice());
		flight.setSeatClass(request.getFlight().getSeatClass());
		flight.setStatus("CREATED");

		booking.setFlight(flight);

		HotelRoom hotel = new HotelRoom();
		hotel.setExternalId(0L);
		hotel.setCheckInTime(request.getHotelRoom().getCheckInTime());
		hotel.setCheckOutTime(request.getHotelRoom().getCheckOutTime());
		hotel.setRoomType(request.getHotelRoom().getRoomType());
		hotel.setPrice(request.getHotelRoom().getPrice());
		hotel.setStatus("CREATED");

		booking.setHotelRoom(hotel);

		Payment payment = new Payment();
		payment.setExternalId(0L);
		payment.setCardNumber(request.getPayment().getCardNumber());
		payment.setAmount(total);
		payment.setPaymentMethod(request.getPayment().getPaymentMethod());
		payment.setStatus("CREATED");
		payment.setConfirmado(false);
		booking.setPayment(payment);

		bookingRepository.saveAndFlush(booking);
		// try {
		CarResponse carServiceResponse = restTemplate
				.postForEntity(carServiceUrl + "/car", request.getCar(), CarResponse.class).getBody();
		// bookingResponse.setCar(carServiceResponse);
		log.info("Receiving response from car service: {}", carServiceResponse);
		car.setSerialNumber(carServiceResponse.getSerialNumber());
		car.setExternalId(carServiceResponse.getId());
		car.setStatus("COMMIT");
		booking.setCar(car);
		// } catch (Exception ex) {
		// log.info(ex.getLocalizedMessage());
		// log.info(ex.getMessage());
		// log.info(ex.toString());
		// }
		Booking savedBooking = bookingRepository.saveAndFlush(booking);

		//

		bookingRepository.saveAndFlush(booking);
		// try {
		FlightResponse flightServiceResponse = restTemplate
				.postForEntity(flightServiceUrl + "/flight", request.getFlight(), FlightResponse.class).getBody();
		// bookingResponse.setCar(carServiceResponse);
		log.info("Receiving response from flight service: {}", flightServiceResponse);
		flight.setExternalId(flightServiceResponse.getId());
		flight.setStatus("COMMIT");
		booking.setFlight(flight);
		// } catch (Exception ex) {
		// log.info(ex.getLocalizedMessage());
		// log.info(ex.getMessage());
		// log.info(ex.toString());
		// }
		savedBooking = bookingRepository.saveAndFlush(booking);

		bookingRepository.saveAndFlush(booking);
		// try {
		HotelRoomResponse hotelServiceResponse = restTemplate
				.postForEntity(hotelServiceUrl + "/hotel", request.getHotelRoom(), HotelRoomResponse.class).getBody();
		// bookingResponse.setCar(carServiceResponse);
		log.info("Receiving response from hotel service: {}", hotelServiceResponse);
		hotel.setExternalId(hotelServiceResponse.getId());
		hotel.setStatus("COMMIT");
		booking.setHotelRoom(hotel);
//		} catch (Exception ex) {
//			log.info(ex.getLocalizedMessage());
//			log.info(ex.getMessage());
//			log.info(ex.toString());
//		}
		savedBooking = bookingRepository.saveAndFlush(booking);

		bookingRepository.saveAndFlush(booking);
		try {
			PaymentRequest paymentRequest = request.getPayment();
			paymentRequest.setAmount(total);
			PaymentResponse paymentServiceResponse = restTemplate
					.postForEntity(paymentServiceUrl + "/payment", paymentRequest, PaymentResponse.class).getBody();
			// bookingResponse.setCar(carServiceResponse);
			log.info("Receiving response from payment service: {}", paymentServiceResponse);
			payment.setExternalId(paymentServiceResponse.getId());
			payment.setStatus("COMMIT");
			payment.setConfirmado(paymentServiceResponse.isConfirmado());
			booking.setPayment(payment);
		} catch (Exception ex) {
			log.info(ex.getLocalizedMessage());
			log.info(ex.getMessage());
			log.info(ex.toString());
			payment.setStatus("ROLLBACK");
			payment.setConfirmado(false);
			throw new RuntimeException("Rollback de payment");
		}
		savedBooking = bookingRepository.saveAndFlush(booking);

		response.setId(savedBooking.getId());
		response.setStatus(savedBooking.getStatus());
		response.setTotal(savedBooking.getTotal());
		response.setUserId(savedBooking.getUserId());
//		response.setCreationDate(savedBooking.getCreationDate());
//		response.setUpdatedDate(savedBooking.getUpdatedDate());

		CarResponse carResponse = new CarResponse();
		carResponse.setId(savedBooking.getCar().getId());
		carResponse.setExternalId(savedBooking.getCar().getExternalId());
		carResponse.setStatus(savedBooking.getCar().getStatus());
		carResponse.setSerialNumber(savedBooking.getCar().getSerialNumber());
		carResponse.setModelName(savedBooking.getCar().getModelName());
		carResponse.setPickUpDate(savedBooking.getCar().getPickUpDate());
		carResponse.setReturnDate(savedBooking.getCar().getReturnDate());
		carResponse.setPrice(savedBooking.getCar().getPrice());
		response.setCar(carResponse);

		FlightResponse flightResponse = new FlightResponse();
		flightResponse.setId(savedBooking.getFlight().getId());
		flightResponse.setExternalId(savedBooking.getFlight().getExternalId());
		flightResponse.setDepartureTime(savedBooking.getFlight().getDepartureTime());
		flightResponse.setArrivalTime(savedBooking.getFlight().getArrivalTime());
		flightResponse.setSeatClass(savedBooking.getFlight().getSeatClass());
		flightResponse.setPrice(savedBooking.getFlight().getPrice());
		flightResponse.setStatus(savedBooking.getFlight().getStatus());

		response.setFlight(flightResponse);

		HotelRoomResponse hotelResponse = new HotelRoomResponse();
		hotelResponse.setId(savedBooking.getHotelRoom().getId());
		hotelResponse.setExternalId(savedBooking.getHotelRoom().getExternalId());
		hotelResponse.setCheckInTime(savedBooking.getHotelRoom().getCheckInTime());
		hotelResponse.setCheckOutTime(savedBooking.getHotelRoom().getCheckOutTime());
		hotelResponse.setPrice(savedBooking.getHotelRoom().getPrice());
		hotelResponse.setRoomType(savedBooking.getHotelRoom().getRoomType());
		hotelResponse.setStatus(savedBooking.getHotelRoom().getStatus());

		response.setHotelRoom(hotelResponse);

		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setId(savedBooking.getPayment().getId());
		paymentResponse.setExternalId(savedBooking.getPayment().getExternalId());
		paymentResponse.setAmount(savedBooking.getPayment().getAmount());
		paymentResponse.setPaymentMethod(savedBooking.getPayment().getPaymentMethod());
		paymentResponse.setStatus(savedBooking.getPayment().getStatus());
		paymentResponse.setConfirmado(savedBooking.getPayment().isConfirmado());

		response.setPayment(paymentResponse);
		return response;
	}

	public List<BookingResponse> getAllBookings() {
		// TODO Auto-generated method stub

		List<BookingResponse> response = new ArrayList<BookingResponse>();
		List<Booking> bookingList = bookingRepository.findAll();
		for (Booking booking : bookingList) {
			BookingResponse bookingResponse = new BookingResponse();
			bookingResponse.setId(booking.getId());
			bookingResponse.setStatus(booking.getStatus());
			bookingResponse.setTotal(booking.getTotal());
			bookingResponse.setUserId(booking.getUserId());
//			bookingResponse.setCreationDate(booking.getCreationDate());
//			bookingResponse.setUpdatedDate(booking.getUpdatedDate());

			CarResponse carResponse = new CarResponse();
			carResponse.setId(booking.getCar().getId());
			carResponse.setExternalId(booking.getCar().getExternalId());
			carResponse.setStatus(booking.getCar().getStatus());
			carResponse.setSerialNumber(booking.getCar().getSerialNumber());
			carResponse.setModelName(booking.getCar().getModelName());
			carResponse.setPickUpDate(booking.getCar().getPickUpDate());
			carResponse.setReturnDate(booking.getCar().getReturnDate());
			carResponse.setPrice(booking.getCar().getPrice());
			bookingResponse.setCar(carResponse);

			FlightResponse flightResponse = new FlightResponse();
			flightResponse.setId(booking.getFlight().getId());
			flightResponse.setExternalId(booking.getFlight().getExternalId());
			flightResponse.setDepartureTime(booking.getFlight().getDepartureTime());
			flightResponse.setArrivalTime(booking.getFlight().getArrivalTime());
			flightResponse.setSeatClass(booking.getFlight().getSeatClass());
			flightResponse.setPrice(booking.getFlight().getPrice());
			flightResponse.setStatus(booking.getFlight().getStatus());

			bookingResponse.setFlight(flightResponse);

			HotelRoomResponse hotelResponse = new HotelRoomResponse();
			hotelResponse.setId(booking.getHotelRoom().getId());
			hotelResponse.setExternalId(booking.getHotelRoom().getExternalId());
			hotelResponse.setCheckInTime(booking.getHotelRoom().getCheckInTime());
			hotelResponse.setCheckOutTime(booking.getHotelRoom().getCheckOutTime());
			hotelResponse.setPrice(booking.getHotelRoom().getPrice());
			hotelResponse.setRoomType(booking.getHotelRoom().getRoomType());
			hotelResponse.setStatus(booking.getHotelRoom().getStatus());

			bookingResponse.setHotelRoom(hotelResponse);

			PaymentResponse paymentResponse = new PaymentResponse();
			paymentResponse.setId(booking.getPayment().getId());
			paymentResponse.setExternalId(booking.getPayment().getExternalId());
			paymentResponse.setAmount(booking.getPayment().getAmount());
			paymentResponse.setPaymentMethod(booking.getPayment().getPaymentMethod());
			paymentResponse.setStatus(booking.getPayment().getStatus());
			paymentResponse.setConfirmado(booking.getPayment().isConfirmado());

			bookingResponse.setPayment(paymentResponse);

			response.add(bookingResponse);
		}
		return response;

	}

}
