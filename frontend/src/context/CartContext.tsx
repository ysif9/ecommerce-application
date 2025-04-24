import {createContext, ReactNode, useContext} from 'react';
import {CartItem} from '@/types';
import {toast} from '@/hooks/use-toast.ts';
import {cartService} from '../services/cart-service';
import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {useAuth} from '../context/AuthContext';

type CartContextType = {
  cart: CartItem[];
  addToCart: (productId: string, quantity: number) => void;
  removeFromCart: (itemId: string) => void;
  updateQuantity: (itemId: string, quantity: number) => void;
  clearCart: () => void;
  cartTotal: number;
  cartItemCount: number;
  isLoading: boolean;
  error: Error | null;
};

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
  const queryClient = useQueryClient();
  const { login } = useAuth();
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  // Fetch cart data
  const { data: cartData, isLoading, error } = useQuery({
    queryKey: ['cart'],
    queryFn: () => cartService.getCart(),
    enabled: !!user
  });

  // Add to cart mutation
  const addToCartMutation = useMutation({
    mutationFn: (item: { productId: string; quantity: number }) => 
      cartService.addToCart(item),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
      toast({
        title: 'Added to Cart',
        description: 'Item added to your cart',
        duration: 2000,
      });
    },
  });

  // Update quantity mutation
  const updateQuantityMutation = useMutation({
    mutationFn: ({ itemId, quantity }: { itemId: string; quantity: number }) => 
      cartService.updateCartItem(itemId, { quantity }), // Fixed: Pass an object with quantity property
    onMutate: async ({ itemId, quantity }) => {
      // Cancel any outgoing refetches
      await queryClient.cancelQueries({ queryKey: ['cart'] });

      // Snapshot the previous value
      const previousCart = queryClient.getQueryData(['cart']);

      // Optimistically update to the new value
      queryClient.setQueryData(['cart'], (old: any) => {
        if (!old) return old;
        return {
          ...old,
          items: old.items.map((item: CartItem) =>
            item.id === itemId ? { ...item, quantity } : item
          ),
        };
      });

      return { previousCart };
    },
    onError: (err, newData, context) => {
      // If the mutation fails, use the context returned from onMutate to roll back
      queryClient.setQueryData(['cart'], context?.previousCart);
    },
    onSettled: () => {
      // Always refetch after error or success to ensure data is in sync
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
  });

  // Remove item mutation
  const removeItemMutation = useMutation({
    mutationFn: (itemId: string) => cartService.removeCartItem(itemId),
    onMutate: async (itemId) => {
      await queryClient.cancelQueries({ queryKey: ['cart'] });
      const previousCart = queryClient.getQueryData(['cart']);

      queryClient.setQueryData(['cart'], (old: any) => {
        if (!old) return old;
        return {
          ...old,
          items: old.items.filter((item: CartItem) => item.id !== itemId),
        };
      });

      return { previousCart };
    },
    onError: (err, newData, context) => {
      queryClient.setQueryData(['cart'], context?.previousCart);
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
  });

  // Clear cart mutation
  const clearCartMutation = useMutation({
    mutationFn: () => cartService.clearCart(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
  });

  const addToCart = (productId: string, quantity: number) => {
    addToCartMutation.mutate({ productId, quantity });
  };

  const removeFromCart = (itemId: string) => {
    removeItemMutation.mutate(itemId);
  };

  const updateQuantity = (itemId: string, quantity: number) => {
    updateQuantityMutation.mutate({ itemId, quantity });
  };

  const clearCart = () => {
    clearCartMutation.mutate();
  };

  const cartTotal = cartData?.items.reduce((total, item) => total + (item.price * item.quantity), 0) || 0;
  const cartItemCount = cartData?.items.reduce((count, item) => count + item.quantity, 0) || 0;

  return (
    <CartContext.Provider
      value={{
        cart: cartData?.items || [],
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        cartTotal,
        cartItemCount,
        isLoading,
        error,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = (): CartContextType => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
