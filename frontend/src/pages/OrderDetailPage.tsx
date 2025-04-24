import {Link, useParams} from 'react-router-dom';
import {MainLayout} from '../components/Layout/MainLayout';
import {useCancelOrder, useOrder} from '../hooks/use-orders';
import {Button} from '@/components/ui/button';
import {ArrowLeft} from 'lucide-react';
import {format} from 'date-fns';

const OrderDetailPage = () => {
  const { id } = useParams();
  const { data: order, isLoading, error } = useOrder(id);
  const cancelOrderMutation = useCancelOrder();

  if (isLoading) {
    return (
      <MainLayout>
        <div className="container py-8">
          <div className="flex items-center justify-center h-64">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        </div>
      </MainLayout>
    );
  }

  if (error || !order) {
    return (
      <MainLayout>
        <div className="container py-8">
          <div className="text-center text-red-500">
            Error loading order. Please try again later.
          </div>
        </div>
      </MainLayout>
    );
  }

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  const handleCancelOrder = () => {
    if (window.confirm('Are you sure you want to cancel this order?')) {
      cancelOrderMutation.mutate(order.id);
    }
  };

  return (
    <MainLayout>
      <div className="container py-8">
        <div className="mb-6">
          <Link to="/orders" className="flex items-center text-gray-600 hover:text-gray-900">
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Orders
          </Link>
        </div>

        <div className="grid gap-6 lg:grid-cols-3">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-lg border p-6">
              <h1 className="text-2xl font-bold mb-4">Order Details</h1>
              <div className="grid gap-4">
                <div className="flex justify-between">
                  <span className="text-gray-600">Order ID:</span>
                  <span className="font-medium">{order.id}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Date:</span>
                  <span className="font-medium">
                    {format(new Date(order.createdAt), 'PPP')}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Status:</span>
                  <span className="font-medium capitalize">{order.status}</span>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg border p-6">
              <h2 className="text-xl font-bold mb-4">Items</h2>
              <div className="divide-y">
                {order.items.map((item) => (
                  <div key={item.id} className="py-4 flex justify-between">
                    <div>
                      <p className="font-medium">{item.productName}</p>
                      <p className="text-sm text-gray-500">Quantity: {item.quantity}</p>
                      {item.product && (
                        <p className={`text-xs ${item.product.quantity > 0 ? 'text-green-600' : 'text-red-600'}`}>
                          {item.product.quantity > 0 
                            ? `In Stock (${item.product.quantity} available)` 
                            : 'Out of Stock'}
                        </p>
                      )}
                    </div>
                    <p className="font-medium">{formatPrice(item.price * item.quantity)}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div>
            <div className="bg-white rounded-lg border p-6 sticky top-20">
              <h2 className="text-xl font-bold mb-4">Order Summary</h2>
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-gray-600">Total</span>
                  <span className="font-bold">{formatPrice(order.totalAmount)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Payment Method</span>
                  <span className="font-medium">{order.paymentMethod}</span>
                </div>
              </div>

              {order.status === 'pending' && (
                <div className="mt-6">
                  <Button 
                    variant="destructive" 
                    className="w-full"
                    onClick={handleCancelOrder}
                    disabled={cancelOrderMutation.isPending}
                  >
                    Cancel Order
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default OrderDetailPage;
