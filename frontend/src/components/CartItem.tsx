import {MinusCircle, PlusCircle, Trash2} from 'lucide-react';
import {CartItem as CartItemType} from '../types';
import {useCart} from '../context/CartContext';
import {Button} from './ui/button';

interface CartItemProps {
  item: CartItemType;
}

export const CartItemComponent = ({ item }: CartItemProps) => {
  const { updateQuantity, removeFromCart } = useCart();
  
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };
  
  return (
    <div className="flex items-center justify-between py-6 border-b">
      {/* Product Info */}
      <div className="ml-4 flex-grow">
        <h3 className="text-md font-medium">{item.productName}</h3>
        <p className="text-primary font-semibold mt-1">{formatPrice(item.price)}</p>
      </div>
      
      {/* Quantity Controls */}
      <div className="flex items-center space-x-2">
        <Button 
          variant="outline" 
          size="icon" 
          className="h-8 w-8"
          onClick={() => updateQuantity(item.id, item.quantity - 1)}
          disabled={item.quantity <= 1}
        >
          <MinusCircle className="h-4 w-4" />
        </Button>
        
        <span className="w-10 text-center">{item.quantity}</span>
        
        <Button 
          variant="outline" 
          size="icon" 
          className="h-8 w-8"
          onClick={() => updateQuantity(item.id, item.quantity + 1)}
        >
          <PlusCircle className="h-4 w-4" />
        </Button>
      </div>
      
      {/* Subtotal and Remove */}
      <div className="ml-6 text-right">
        <p className="font-bold">
          {formatPrice(item.price * item.quantity)}
        </p>
        <Button 
          variant="ghost" 
          size="sm" 
          className="text-red-500 hover:text-red-600"
          onClick={() => removeFromCart(item.id)}
        >
          <Trash2 className="h-4 w-4 mr-1" />
          Remove
        </Button>
      </div>
    </div>
  );
};
