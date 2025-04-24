import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {paymentService, ProcessPaymentRequest} from '../services/payment-service';
import {toast} from '../components/ui/use-toast';

// Hook for fetching payment details for an order
export function usePayment(orderId: string | undefined) {
  return useQuery({
    queryKey: ['payment', orderId],
    queryFn: () => paymentService.getPaymentByOrderId(orderId as string),
    enabled: !!orderId,
  });
}

// Hook for processing a payment
export function useProcessPayment() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (paymentData: ProcessPaymentRequest) => 
      paymentService.processPayment(paymentData),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['payment', variables.orderId] });
      queryClient.invalidateQueries({ queryKey: ['order', variables.orderId] }); // Order status might change
      toast({
        title: 'Payment Successful',
        description: 'Your payment has been processed successfully.',
      });
    },
    onError: (error: Error) => {
      toast({
        title: 'Payment Failed',
        description: error.message,
        variant: 'destructive',
      });
    },
  });
}

// Hook for updating a payment
export function useUpdatePayment() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, updates }: { id: string, updates: any }) => 
      paymentService.updatePayment(id, updates),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['payment', variables.id] });
      toast({
        title: 'Payment Updated',
        description: 'The payment details have been updated.',
      });
    },
    onError: (error: Error) => {
      toast({
        title: 'Error',
        description: error.message,
        variant: 'destructive',
      });
    },
  });
}
