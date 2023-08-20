package com.electratech.ecommerce.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.electratech.ecommerce.dao.CustomerRepository;
import com.electratech.ecommerce.dto.Purchase;
import com.electratech.ecommerce.dto.PurchaseResponse;
import com.electratech.ecommerce.entity.Customer;
import com.electratech.ecommerce.entity.Order;
import com.electratech.ecommerce.entity.OrderItem;

import jakarta.transaction.Transactional;

@Service
public class CheckoutServiceImpl implements CheckoutService{

	private CustomerRepository customerRepository;
	
	//@Autowired is optional when there is only one constructor
	public CheckoutServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
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

	
}
