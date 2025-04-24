import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {orderService} from '../services/order-service';
import {toast} from '@/hooks/use-toast.ts';

// Hook for fetching user orders with optional filters
export function useOrders(filters = {}) {
  return useQuery({
    queryKey: ['orders', filters],
    queryFn: () => orderService.getOrders(filters),
  });
}

// Hook for fetching a single order by ID
export function useOrder(orderId: string | undefined) {
  return useQuery({
    queryKey: ['order', orderId],
    queryFn: () => orderService.getOrderById(orderId as string),
    enabled: !!orderId,
  });
}

// Hook for creating a new order
export function useCreateOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => 
      orderService.createOrder(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['cart'] }); // Cart should be empty after order
      toast({
        title: 'Order Placed',
        description: 'Your order has been placed successfully.',
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

// Hook for updating an order
export function useUpdateOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, updates }: { id: string, updates: never }) => 
      orderService.updateOrder(id, updates),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['order', variables.id] });
      toast({
        title: 'Order Updated',
        description: 'The order has been updated successfully.',
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

// Hook for cancelling an order
export function useCancelOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (orderId: string) => 
      orderService.cancelOrder(orderId),
    onSuccess: (data, orderId) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['order', orderId] });
      toast({
        title: 'Order Cancelled',
        description: 'Your order has been cancelled.',
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
