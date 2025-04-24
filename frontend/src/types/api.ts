export interface CartResponse {
  id: string;
  userId: string;
  items: CartItemResponse[];
}

export interface CartItemResponse {
  id: string;
  productId: string;
  productName: string;
  quantity: number;
  price: number;
} 