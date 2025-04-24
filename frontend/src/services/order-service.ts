import {Order} from '@/types';
import {apiRequest} from './api-client';

interface OrdersResponse {
  orders: Order[];
  total: number;
  page: number;
  limit: number;
}

interface OrderFilters {
  status?: 'pending' | 'processing' | 'shipped' | 'delivered' | 'cancelled';
  page?: number;
  limit?: number;
  sort?: string;
}

export const orderService = {
  // Get all orders for current user
  getOrders: (filters: OrderFilters = {}) => {
    return apiRequest<Order[] | OrdersResponse>({
      method: 'GET',
      url: '/orders',
      params: filters,
    }).then(response => {
      // Handle both paginated and non-paginated responses
      if (Array.isArray(response)) {
        // Backend returns a simple array of orders
        // Map orderDate to createdAt for each order
        const mappedOrders = response.map(order => ({
          ...order,
          // Map orderID to id if it exists
          id: order.orderID?.toString() || order.id,
          // Map totalPrice to totalAmount if it exists
          totalAmount: order.totalPrice || order.totalAmount,
          // Use orderDate as createdAt if it exists, otherwise keep createdAt
          createdAt: order.orderDate ? new Date(order.orderDate) : order.createdAt
        }));

        return {
          orders: mappedOrders,
          total: mappedOrders.length,
          page: 1,
          limit: mappedOrders.length
        };
      }

      // Backend returns a paginated response
      // Map orderDate to createdAt for each order
      const mappedResponse = {
        ...response,
        orders: response.orders.map(order => ({
          ...order,
          // Map orderID to id if it exists
          id: order.orderID?.toString() || order.id,
          // Map totalPrice to totalAmount if it exists
          totalAmount: order.totalPrice || order.totalAmount,
          // Use orderDate as createdAt if it exists, otherwise keep createdAt
          createdAt: order.orderDate ? new Date(order.orderDate) : order.createdAt
        }))
      };

      return mappedResponse;
    });
  },

  // Get single order by ID
  getOrderById: (id: string) => {
    return apiRequest<Order>({
      method: 'GET',
      url: `/orders/${id}`,
    }).then(order => {
      // Map backend fields to frontend fields
      return {
        ...order,
        // Map orderID to id if it exists
        id: order.orderID?.toString() || order.id,
        // Map totalPrice to totalAmount if it exists
        totalAmount: order.totalPrice || order.totalAmount,
        // Use orderDate as createdAt if it exists, otherwise keep createdAt
        createdAt: order.orderDate ? new Date(order.orderDate) : order.createdAt
      };
    });
  },

  // Create new order from current cart
  createOrder: () => {
    return apiRequest<Order>({
      method: 'POST',
      url: '/orders',
    }).then(order => {
      // Map backend fields to frontend fields
      return {
        ...order,
        // Map orderID to id if it exists
        id: order.orderID?.toString() || order.id,
        // Map totalPrice to totalAmount if it exists
        totalAmount: order.totalPrice || order.totalAmount,
        // Use orderDate as createdAt if it exists, otherwise keep createdAt
        createdAt: order.orderDate ? new Date(order.orderDate) : order.createdAt
      };
    });
  },

  // Update order status
  updateOrder: (id: string, updates: Partial<Order>) => {
    return apiRequest<Order>({
      method: 'PUT',
      url: `/orders/${id}`,
      data: updates,
    });
  },

  // Cancel order
  cancelOrder: (id: string) => {
    return apiRequest<{ success: boolean }>({
      method: 'DELETE',
      url: `/orders/${id}`,
    });
  },
};
