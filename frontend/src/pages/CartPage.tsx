import {MainLayout} from '../components/Layout/MainLayout';
import {CartItemComponent} from '../components/CartItem';
import {useCart} from '../context/CartContext';
import {Button} from '../components/ui/button';
import {Link, useNavigate} from 'react-router-dom';
import {ArrowRight, Package, ShoppingCart} from 'lucide-react';
import {useState} from 'react';

const CartPage = () => {
  const { cart, cartTotal, clearCart } = useCart();
  const navigate = useNavigate();
  const [isCheckingOut, setIsCheckingOut] = useState(false);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  const handleCheckout = () => {
    navigate('/checkout');
  };

  const shippingFee = cartTotal > 100 ? 0 : 10;
  const totalWithShipping = cartTotal + shippingFee;

  if (cart.length === 0) {
    return (
      <MainLayout>
        <div className="container py-16 px-4 md:px-6">
          <h1 className="text-3xl font-bold mb-6">Your Shopping Cart</h1>
          <div className="text-center py-16 space-y-6">
            <div className="flex justify-center">
              <ShoppingCart className="h-24 w-24 text-gray-300" />
            </div>
            <h2 className="text-2xl font-medium">Your cart is empty</h2>
            <p className="text-gray-500 max-w-md mx-auto">
              Looks like you haven't added any items to your cart yet.
              Browse our products and find something you like!
            </p>
            <div>
              <Link to="/products">
                <Button size="lg">
                  Start Shopping
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="container py-8 px-4 md:px-6">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold">Your Shopping Cart</h1>
          <Link to="/orders">
            <Button variant="outline">
              <Package className="mr-2 h-4 w-4" />
              View Orders
            </Button>
          </Link>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-lg border">
              <div className="p-6 border-b">
                <div className="flex justify-between items-center">
                  <h2 className="text-xl font-semibold">
                    Cart Items ({cart.reduce((acc, item) => acc + item.quantity, 0)})
                  </h2>
                  <Button variant="ghost" onClick={clearCart} className="text-red-500 hover:text-red-600">
                    Clear Cart
                  </Button>
                </div>
              </div>

              <div className="divide-y">
                {cart.map(item => (
                  <div key={item.id} className="px-6">
                    <CartItemComponent item={item} />
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div>
            <div className="bg-white rounded-lg border p-6 sticky top-20">
              <h2 className="text-xl font-semibold mb-6">Order Summary</h2>

              <div className="space-y-4 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Subtotal</span>
                  <span className="font-medium">{formatPrice(cartTotal)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Shipping</span>
                  <span className="font-medium">
                    {shippingFee === 0 ? 'Free' : formatPrice(shippingFee)}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Taxes</span>
                  <span className="font-medium">Calculated at checkout</span>
                </div>
                <div className="border-t pt-4 flex justify-between">
                  <span className="text-lg font-bold">Total</span>
                  <span className="text-lg font-bold text-primary">{formatPrice(totalWithShipping)}</span>
                </div>
              </div>

              <div className="mt-6 space-y-3">
                <Button
                  className="w-full"
                  size="lg"
                  onClick={handleCheckout}
                  disabled={isCheckingOut}
                >
                  Proceed to Checkout
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Button>
                <Link to="/products">
                  <Button variant="outline" className="w-full">
                    Continue Shopping
                  </Button>
                </Link>
              </div>

              <div className="mt-6 space-y-2">
                <div className="flex items-center justify-center text-sm text-gray-500">
                  <p>Secure Payment with:</p>
                </div>
                <div className="flex justify-center space-x-2">
                  {['visa', 'mastercard', 'amex', 'paypal'].map((payment) => (
                    <div key={`payment-${payment}`} className="w-10 h-6 bg-gray-200 rounded"></div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default CartPage;
