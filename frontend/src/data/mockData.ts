import {Order, Product, User} from '../types';

export const mockUsers: User[] = [
  {
    id: '1',
    name: 'John Doe',
    email: 'john@example.com',
    role: 'user',
    createdAt: new Date('2023-01-15')
  },
  {
    id: '2',
    name: 'Jane Smith',
    email: 'jane@example.com',
    role: 'admin',
    createdAt: new Date('2023-02-20')
  }
];

export const mockProducts: Product[] = [
  {
    productID: 1,
    name: 'Wireless Headphones',
    description: 'Premium noise-cancelling wireless headphones with 30-hour battery life and comfortable ear cushions for extended wear.',
    price: 249.99,
    category: 'Electronics',
    imageURL: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=2670&auto=format&fit=crop',
    quantity: 45,
    rating: 4.8,
    stock: 45,
    featured: true,
  },
  {
    productID: 2,
    name: 'Smart Watch',
    description: 'Feature-packed smartwatch with health monitoring, notifications, and customizable watch faces. Water resistant and long battery life.',
    price: 199.99,
    category: 'Electronics',
    imageURL: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=2799&auto=format&fit=crop',
    quantity: 30,
    rating: 4.6,
    stock: 30,
    featured: true,
  },
  {
    productID: 3,
    name: 'Ergonomic Office Chair',
    description: 'Comfortable ergonomic chair with lumbar support, adjustable height, and breathable mesh back. Perfect for long work days.',
    price: 299.99,
    category: 'Furniture',
    imageURL: 'https://images.unsplash.com/photo-1578500494198-246f612d3b3d?q=80&w=2670&auto=format&fit=crop',
    quantity: 15,
    rating: 4.5,
    stock: 15,
    featured: false,
  },
  {
    productID: 4,
    name: 'Ultra HD 4K Monitor',
    description: 'Crystal clear 32-inch 4K monitor with HDR support, adjustable stand, and multiple connectivity options including USB-C.',
    price: 399.99,
    category: 'Electronics',
    imageURL: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=2670&auto=format&fit=crop',
    quantity: 20,
    rating: 4.7,
    stock: 20,
    featured: true,
  },
  {
    productID: 5,
    name: 'Mechanical Gaming Keyboard',
    description: 'Responsive mechanical keyboard with RGB backlighting, programmable keys, and durable construction for serious gamers.',
    price: 149.99,
    category: 'Electronics',
    imageURL: 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?q=80&w=2665&auto=format&fit=crop',
    quantity: 25,
    rating: 4.4,
    stock: 25,
    featured: false,
  },
  {
    productID: 6,
    name: 'Leather Wallet',
    description: 'Genuine leather wallet with RFID protection, multiple card slots, and sleek minimalist design.',
    price: 59.99,
    category: 'Accessories',
    imageURL: 'https://images.unsplash.com/photo-1627123424574-724758594e93?q=80&w=2787&auto=format&fit=crop',
    quantity: 50,
    rating: 4.3,
    stock: 50,
    featured: false,
  },
  {
    productID: 7,
    name: 'Bluetooth Speaker',
    description: 'Portable Bluetooth speaker with 360-degree sound, waterproof design, and 20-hour battery life. Perfect for outdoor adventures.',
    price: 129.99,
    category: 'Electronics',
    imageURL: 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?q=80&w=2670&auto=format&fit=crop',
    quantity: 35,
    rating: 4.5,
    stock: 35,
    featured: true,
  },
  {
    productID: 8,
    name: 'Coffee Maker',
    description: 'Programmable coffee maker with thermal carafe, adjustable brew strength, and auto-shutoff feature.',
    price: 89.99,
    category: 'Kitchen',
    imageURL: 'https://images.unsplash.com/photo-1525088553748-ac8f6b239b7a?q=80&w=2748&auto=format&fit=crop',
    quantity: 40,
    rating: 4.2,
    stock: 40,
    featured: false,
  }
];

export const mockOrders: Order[] = [
  {
    id: '1',
    userId: '1',
    items: [
      {
        id: '1',
        productId: '1',
        productName: 'Wireless Headphones',
        quantity: 1,
        price: 249.99,
        product: mockProducts[0]
      },
      {
        id: '2',
        productId: '2',
        productName: 'Smart Watch',
        quantity: 1,
        price: 199.99,
        product: mockProducts[1]
      }
    ],
    status: 'delivered',
    shippingAddress: {
      street: '123 Main St',
      city: 'New York',
      state: 'NY',
      zipCode: '10001',
      country: 'USA'
    },
    totalAmount: 449.98,
    paymentMethod: 'credit_card',
    createdAt: new Date('2023-06-15')
  },
  {
    id: '2',
    userId: '1',
    items: [
      {
        id: '3',
        productId: '3',
        productName: 'Ergonomic Office Chair',
        quantity: 1,
        price: 299.99,
        product: mockProducts[2]
      }
    ],
    status: 'shipped',
    shippingAddress: {
      street: '123 Main St',
      city: 'New York',
      state: 'NY',
      zipCode: '10001',
      country: 'USA'
    },
    totalAmount: 299.99,
    paymentMethod: 'paypal',
    createdAt: new Date('2023-07-10')
  }
];

export const categories = [
  'All',
  'Electronics',
  'Furniture',
  'Accessories',
  'Kitchen',
  'Clothing'
];
