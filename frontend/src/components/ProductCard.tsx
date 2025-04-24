import {Product} from '../types';
import {useCart} from '../context/CartContext';
import {Button} from './ui/button';
import {Card, CardContent, CardFooter, CardHeader} from './ui/card';
import {useState} from 'react';
import {ShoppingCart} from 'lucide-react';
import {Link} from 'react-router-dom';

interface ProductCardProps {
  product: Product;
}

export const ProductCard = ({ product }: ProductCardProps) => {
  const [isHovering, setIsHovering] = useState(false);
  const { addToCart } = useCart();

  // Format price to currency
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  return (
    <Card 
      className="overflow-hidden product-card-transition"
      onMouseEnter={() => setIsHovering(true)}
      onMouseLeave={() => setIsHovering(false)}
    >
      <div className="relative">
        <Link to={`/products/${product.productID}`}>
          <div className="h-64 overflow-hidden">
            <img 
              src={product.imageURL} 
              alt={product.name} 
              className="w-full h-full object-cover transition-transform duration-500 ease-in-out hover:scale-110" 
            />
          </div>
        </Link>
        
        {product.featured && (
          <span className="absolute top-2 right-2 badge badge-primary">
            Featured
          </span>
        )}
      </div>
      
      <CardHeader className="pb-2">
        <div className="flex justify-between items-start">
          <Link to={`/products/${product.productID}`} className="text-lg font-semibold hover:text-primary transition-colors">
            {product.name}
          </Link>
          <div className="flex items-center">
            <span className="text-sm font-medium">{product.rating}</span>
          </div>
        </div>
        <p className="text-sm text-gray-500">{product.category}</p>
      </CardHeader>
      <CardContent className="pb-4">
        <p className="text-sm text-gray-600 line-clamp-2">{product.description}</p>
        <p className="text-lg font-bold mt-2">{formatPrice(product.price)}</p>
      </CardContent>
      <CardFooter className="pt-0">
        <Button 
          className="w-full" 
          onClick={() => addToCart(product.productID.toString(), 1)}
        >
          <ShoppingCart className="mr-2 h-4 w-4" />
          Add to Cart
        </Button>
      </CardFooter>
    </Card>
  );
};
