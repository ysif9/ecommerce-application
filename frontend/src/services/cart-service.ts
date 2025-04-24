import {apiRequest} from './api-client';
import {Cart, CartItem} from '@/types';

interface CartResponse {
  id: string;
  userId: string;
  items: CartItemResponse[];
}

interface CartItemResponse {
  id: string;
  productId: string;
  productName: string;
  quantity: number;
  price: number;
}

const mapCartItemResponseToCartItem = (item: CartItemResponse): CartItem => ({
  id: item.id,
  productId: item.productId,
  productName: item.productName,
  quantity: item.quantity,
  price: item.price
});

const mapCartResponseToCart = (response: CartResponse): Cart => ({
  id: response.id,
  userId: response.userId,
  items: response.items.map(mapCartItemResponseToCartItem)
});

export interface AddToCartRequest {
  productId: string;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

export const cartService = {
  // Get current user's cart
  getCart: async () => {
    console.log('🛒 Fetching cart...');
    try {
      const response = await apiRequest<Cart>({
        method: 'GET',
        url: '/cart',
      });
      console.log('📦 Cart data received:', response);
      return response;
    } catch (error) {
      console.error('❌ Error fetching cart:', error);
      throw error;
    }
  },

  // Add item to cart
  addToCart: async (item: AddToCartRequest) => {
    console.log('➕ Adding item to cart:', item);
    try {
      const response = await apiRequest<Cart>({
        method: 'POST',
        url: '/cart/items',
        params: {
          productId: item.productId,
          quantity: item.quantity
        }
      });
      console.log('✅ Item added successfully:', response);
      return response;
    } catch (error) {
      console.error('❌ Error adding item to cart:', error);
      throw error;
    }
  },

  // Update cart item quantity
  updateCartItem: async (itemId: string, data: UpdateCartItemRequest): Promise<Cart> => {
    const response = await apiRequest<CartResponse>({
      method: 'PATCH',
      url: `/cart/items/${itemId}`,
      params: {
        quantity: data.quantity
      }
    });
    return mapCartResponseToCart(response);
  },

  // Remove item from cart
  removeCartItem: async (itemId: string) => {
    console.log('🗑️ Removing item from cart:', itemId);
    try {
      const response = await apiRequest<{ success: boolean }>({
        method: 'DELETE',
        url: `/cart/items/${itemId}`,
      });
      console.log('✅ Item removed successfully:', response);
      return response;
    } catch (error) {
      console.error('❌ Error removing cart item:', error);
      throw error;
    }
  },

  // Clear entire cart
  clearCart: async () => {
    console.log('🧹 Clearing cart...');
    try {
      const response = await apiRequest<{ success: boolean }>({
        method: 'DELETE',
        url: '/cart',
      });
      console.log('✅ Cart cleared successfully:', response);
      return response;
    } catch (error) {
      console.error('❌ Error clearing cart:', error);
      throw error;
    }
  },
};
