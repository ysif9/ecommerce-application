
export interface User {
  id: string;
  email: string;
  name: string;
  role: 'user' | 'admin';
  createdAt: Date;
}

export interface Product {
  id?: string;          // For compatibility with existing code
  productID: number;
  name: string;
  description: string;
  price: number;
  category: string;
  image?: string;       // For compatibility with existing code
  imageURL: string;
  quantity: number;
  featured?: boolean;
  rating?: number;
  stock?: number;       // For compatibility with existing code
  createdAt?: Date;     // For compatibility with existing code
}

export interface CartItem {
  id: string;
  productId: string;
  productName: string;
  quantity: number;
  price: number;
  orderId?: string;     // Added to store reference to order
  product?: Product;    // For compatibility with existing code
}

export interface Cart {
  id: string;
  userId: string;
  items: CartItem[];
}

export interface Order {
  id: string;
  userId: string;
  items: CartItem[];
  status: 'pending' | 'processing' | 'shipped' | 'delivered' | 'cancelled';
  shippingAddress: Address;
  totalAmount: number;
  paymentMethod: string;
  createdAt: Date;
}

export interface Payment {
  id: string;
  orderId: string;
  amount: number;
  paymentMethod: string; // Changed from method to paymentMethod to match backend
  status: 'pending' | 'completed' | 'failed' | 'cancelled'; // Updated to match backend PaymentStatus enum
  createdAt: Date;
}

export interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
}
