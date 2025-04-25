import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {MainLayout} from '../components/Layout/MainLayout';
import {useCart} from '../context/CartContext';
import {useAuth} from '../context/AuthContext';
import {Input} from '../components/ui/input';
import {Button} from '../components/ui/button';
import {CreditCard, Package} from 'lucide-react';
import {Form, FormControl, FormField, FormItem, FormLabel} from '../components/ui/form';
import {useForm} from 'react-hook-form';
import {toast} from '../hooks/use-toast';
import {orderService} from '../services/order-service';
import {paymentService} from '../services/payment-service';

interface CheckoutFormData {
  cardNumber: string;
  expiryDate: string;
  cvv: string;
  cardHolderName: string;
  billingAddress: string;
}

const CheckoutPage = () => {
  const {cart, cartTotal, clearCart} = useCart();
  const navigate = useNavigate();
  const [isProcessing, setIsProcessing] = useState(false);

  const form = useForm<CheckoutFormData>({
    defaultValues: {
      cardNumber: '',
      expiryDate: '',
      cvv: '',
      cardHolderName: '',
      billingAddress: '',
    },
  });

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  const { user } = useAuth();

  const onSubmit = async (data: CheckoutFormData) => {
    try {
      setIsProcessing(true);

      // Step 1: Create an order
      const order = await orderService.createOrder();
      console.log('Order created:', order);

      // Step 2: Process payment
      const paymentData = {
        orderId: order.id, // Now using the mapped id property from order-service
        paymentMethod: 'credit_card', // Hardcoded for now, could be dynamic based on user selection
        amount: totalWithShipping,
        userId: user?.id, // Optional: Include user ID if available
        transactionId: `TX-${Date.now()}` // Generate a simple transaction ID
      };

      const payment = await paymentService.processPayment(paymentData);
      console.log('Payment processed:', payment);

      toast({
        title: "Payment Successful",
        description: "Your order has been placed and payment has been processed successfully.",
      });
      clearCart();

      navigate('/orders');
    } catch (error) {
      console.error('Checkout error:', error);
      toast({
        title: "Payment Failed",
        description: error instanceof Error ? error.message : "There was an error processing your payment. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsProcessing(false);
    }
  };

  const handleExpiryDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
      value = value.slice(0, 2) + '/' + value.slice(2, 4);
    }
    form.setValue('expiryDate', value);
  };

  const shippingFee = cartTotal > 100 ? 0 : 10;
  const totalWithShipping = cartTotal + shippingFee;

  if (cart.length === 0) {
    navigate('/cart');
    return null;
  }

  return (
    <MainLayout>
      <div className="container py-8 px-4 md:px-6">
        <h1 className="text-3xl font-bold mb-6">Checkout</h1>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="space-y-6">
            <div className="bg-white rounded-lg border p-6">
              <h2 className="text-xl font-semibold mb-4">Payment Details</h2>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                  <FormField
                    control={form.control}
                    name="cardHolderName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Cardholder Name</FormLabel>
                        <FormControl>
                          <Input placeholder="John Doe" {...field} required />
                        </FormControl>
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="cardNumber"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Card Number</FormLabel>
                        <FormControl>
                          <Input 
                            placeholder="1234 5678 9012 3456"
                            maxLength={19}
                            {...field}
                            required
                          />
                        </FormControl>
                      </FormItem>
                    )}
                  />

                  <div className="grid grid-cols-2 gap-4">
                    <FormField
                      control={form.control}
                      name="expiryDate"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Expiry Date</FormLabel>
                          <FormControl>
                            <Input 
                              placeholder="MM/YY"
                              maxLength={5}
                              onChange={handleExpiryDateChange}
                              value={field.value}
                              required
                            />
                          </FormControl>
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="cvv"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>CVV</FormLabel>
                          <FormControl>
                            <Input 
                              type="text"
                              maxLength={3}
                              placeholder="123"
                              pattern="[0-9]{3}"
                              title="CVV must be exactly 3 digits"
                              {...field}
                              onChange={(e) => {
                                const value = e.target.value.replace(/\D/g, '');
                                field.onChange(value);
                              }}
                              required
                            />
                          </FormControl>
                        </FormItem>
                      )}
                    />
                  </div>

                  <FormField
                    control={form.control}
                    name="billingAddress"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Billing Address</FormLabel>
                        <FormControl>
                          <Input placeholder="Enter your billing address" {...field} required />
                        </FormControl>
                      </FormItem>
                    )}
                  />

                  <Button
                    type="submit"
                    className="w-full"
                    size="lg"
                    disabled={isProcessing}
                  >
                    <CreditCard className="mr-2 h-4 w-4" />
                    {isProcessing ? 'Processing...' : `Pay ${formatPrice(totalWithShipping)}`}
                  </Button>
                </form>
              </Form>
            </div>
          </div>

          <div className="lg:mt-0 mt-6">
            <div className="bg-white rounded-lg border p-6 sticky top-20">
              <h2 className="text-xl font-semibold mb-6">Order Summary</h2>

              <div className="space-y-4">
                {cart.map((item) => (
                  <div key={item.id} className="flex justify-between items-center">
                    <div>
                      <p className="font-medium">{item.productName}</p>
                      <p className="text-sm text-gray-500">Quantity: {item.quantity}</p>
                    </div>
                    <p className="font-medium">{formatPrice(item.price * item.quantity)}</p>
                  </div>
                ))}
              </div>

              <div className="border-t mt-6 pt-6 space-y-4">
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
                <div className="flex justify-between border-t pt-4">
                  <span className="text-lg font-bold">Total</span>
                  <span className="text-lg font-bold text-primary">
                    {formatPrice(totalWithShipping)}
                  </span>
                </div>
              </div>

              <div className="mt-6 text-sm text-gray-500">
                <div className="flex items-center mb-2">
                  <Package className="h-4 w-4 mr-2" />
                  <span>Free shipping on orders over $100</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default CheckoutPage;
