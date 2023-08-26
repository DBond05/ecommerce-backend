package com.electratech.ecommerce.service;

import com.electratech.ecommerce.dto.PaymentInfo;
import com.electratech.ecommerce.dto.Purchase;
import com.electratech.ecommerce.dto.PurchaseResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface CheckoutService {

	PurchaseResponse placeOrder(Purchase purchase);
	
	PaymentIntent createPaymentIntent (PaymentInfo paymentInfo) throws StripeException;
}
