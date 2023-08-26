package com.electratech.ecommerce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.electratech.ecommerce.dao.CustomerRepository;
import com.electratech.ecommerce.dto.PaymentInfo;
import com.electratech.ecommerce.dto.Purchase;
import com.electratech.ecommerce.dto.PurchaseResponse;
import com.electratech.ecommerce.entity.Customer;
import com.electratech.ecommerce.entity.Order;
import com.electratech.ecommerce.entity.OrderItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import jakarta.transaction.Transactional;

@Service
public class CheckoutServiceImpl implements CheckoutService{

	private CustomerRepository customerRepository;
	
	//@Autowired is optional when there is only one constructor
	public CheckoutServiceImpl(CustomerRepository customerRepository, @Value("${stripe.key.secret}")String secretKey) {
		this.customerRepository = customerRepository;
		//initialize stripe api with secret key
		Stripe.apiKey = secretKey;
	}
	
	@Override
	@Transactional //JPA annotation
	public PurchaseResponse placeOrder(Purchase purchase) {
	//retrieve the order info from dto (data transfer object)
		Order order = purchase.getOrder();
		
		//generate tracking number
		String orderTrackingNumber = generateOrderTrackingNumber();
		order.setOrderTrackingNumber(orderTrackingNumber);
		
		//populate order with orderItems
		Set<OrderItem> orderItems = purchase.getOrderItems();
		orderItems.forEach(item -> order.add(item));
		
		//populate order with billing address and shipping address
		order.setBillingAddress(purchase.getBillingAddress());
		order.setShippingAddress(purchase.getShippingAddress());
		
		//populate customer with order
		Customer customer = purchase.getCustomer();
		
		//check if customer is existing customer
		String theEmail = customer.getEmail();
		Customer customerFromDB = customerRepository.findByEmail(theEmail);
		if (customerFromDB != null) {
			customer = customerFromDB;
		}
		
		customer.add(order);
		
		//save to the DB
		customerRepository.save(customer);
		
		
		//return response
		return new PurchaseResponse(orderTrackingNumber);
	}

	private String generateOrderTrackingNumber() {
		// UUID (Universally Unique IDentifier) version-4 (unique & random) 
		//For details see: https://en.wikipedia.org/wiki/Universally_unique_identifier
		// out of 110 trillion possibility of a duplicate is 1 out of a billion;
		return UUID.randomUUID().toString();
	}

	@Override
	public PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException {
		List<String> paymentMethodTypes = new ArrayList<>();
		paymentMethodTypes.add("card");
		
		Map<String, Object> params = new HashMap<>();
		params.put("amount", paymentInfo.getAmount());
		params.put("currency", paymentInfo.getCurrency());
		params.put("payment_method_types", paymentMethodTypes);
		params.put("description", "Luv2Shop ecommerce purchase");
		params.put("receipt_email", paymentInfo.getReceiptEmail());
		
		return PaymentIntent.create(params);
	}

	
}
