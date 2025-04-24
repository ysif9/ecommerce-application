import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {AddToCartRequest, cartService, UpdateCartItemRequest} from '../services/cart-service';
import {toast} from '@/hooks/use-toast.ts';

// Hook for fetching the current user's cart
export function useCart() {
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  
  return useQuery({
    queryKey: ['cart'],
    queryFn: () => cartService.getCart(),
    enabled: !!user, // Only fetch cart if user is authenticated
  });
}

// Hook for adding an item to the cart
export function useAddToCart() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (item: AddToCartRequest) => 
      cartService.addToCart(item),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast({
        title: 'Item Added',
        description: 'The item has been added to your cart.',
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

// Hook for updating an item in the cart
export function useUpdateCartItem() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ itemId, data }: { itemId: string, data: UpdateCartItemRequest }) => 
      cartService.updateCartItem(itemId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast({
        title: 'Cart Updated',
        description: 'The cart item has been updated.',
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

// Hook for removing an item from the cart
export function useRemoveCartItem() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (itemId: string) => 
      cartService.removeCartItem(itemId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast({
        title: 'Item Removed',
        description: 'The item has been removed from your cart.',
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

// Hook for clearing the entire cart
export function useClearCart() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: () => cartService.clearCart(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast({
        title: 'Cart Cleared',
        description: 'Your cart has been cleared.',
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
