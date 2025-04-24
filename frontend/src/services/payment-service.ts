import {Payment} from '../types';
import {apiRequest} from './api-client';

export interface ProcessPaymentRequest {
  orderId: string;
  paymentMethod: string; // Changed from method to paymentMethod to match backend
  amount: number;
  userId?: string; // Added to match backend
  transactionId?: string; // Added to match backend
  // Payment details will be handled by the backend based on the payment method
}

export const paymentService = {
  // Get payment details for an order
  getPaymentByOrderId: (orderId: string) => {
    return apiRequest<Payment>({
      method: 'GET',
      url: `/payments/${orderId}`,
    }).then(payment => {
      // Ensure createdAt is a proper Date object
      return {
        ...payment,
        createdAt: payment.createdAt ? new Date(payment.createdAt) : new Date()
      };
    });
  },

  // Process a payment
  processPayment: (paymentData: ProcessPaymentRequest) => {
    // Ensure the data is in the format expected by the backend
    const requestData = {
      orderId: paymentData.orderId,
      paymentMethod: paymentData.paymentMethod,
      amount: paymentData.amount,
      userId: paymentData.userId,
      transactionId: paymentData.transactionId || null
    };

    return apiRequest<Payment>({
      method: 'POST',
      url: '/payments',
      data: requestData,
    }).then(payment => {
      // Ensure createdAt is a proper Date object
      return {
        ...payment,
        createdAt: payment.createdAt ? new Date(payment.createdAt) : new Date()
      };
    });
  },

  // Update payment status
  updatePayment: (id: string, updates: Partial<Payment>) => {
    return apiRequest<Payment>({
      method: 'PUT',
      url: `/payments/${id}`,
      data: updates,
    }).then(payment => {
      // Ensure createdAt is a proper Date object
      return {
        ...payment,
        createdAt: payment.createdAt ? new Date(payment.createdAt) : new Date()
      };
    });
  },
};
