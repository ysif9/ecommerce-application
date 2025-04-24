import {MainLayout} from '../components/Layout/MainLayout';
import {useOrders} from '../hooks/use-orders';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow,} from "@/components/ui/table";
import {Button} from "@/components/ui/button";
import {Ban, Package, PackageOpen, Truck} from 'lucide-react';
import {format} from 'date-fns';
import {Link} from 'react-router-dom';

const OrderStatusIcon = ({ status }: { status: string }) => {
  switch (status) {
    case 'pending':
      return <Package className="h-4 w-4" />;
    case 'processing':
      return <PackageOpen className="h-4 w-4 text-blue-500" />;
    case 'shipped':
      return <Truck className="h-4 w-4 text-green-500" />;
    case 'cancelled':
      return <Ban className="h-4 w-4 text-red-500" />;
    default:
      return <Package className="h-4 w-4" />;
  }
};

const OrdersPage = () => {
  const { data: ordersData, isLoading, error } = useOrders();

  // Ensure ordersData exists and has the expected structure
  const orders = ordersData?.orders || [];

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

  if (error) {
    return (
      <MainLayout>
        <div className="container py-8">
          <div className="text-center text-red-500">
            Error loading orders. Please try again later.
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

  return (
    <MainLayout>
      <div className="container py-8">
        <h1 className="text-3xl font-bold mb-6">My Orders</h1>
        
        {orders.length === 0 ? (
          <div className="text-center py-12">
            <Package className="mx-auto h-12 w-12 text-gray-400 mb-4" />
            <h3 className="text-lg font-medium">No orders yet</h3>
            <p className="text-gray-500 mt-2">Start shopping to place your first order!</p>
            <Link to="/products">
              <Button className="mt-4">Browse Products</Button>
            </Link>
          </div>
        ) : (
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Order ID</TableHead>
                  <TableHead>Date</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Total</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {orders.map((order) => (
                  <TableRow key={order.id}>
                    <TableCell>{order.id}</TableCell>
                    <TableCell>
                      {format(new Date(order.createdAt), 'MMM dd, yyyy')}
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2">
                        <OrderStatusIcon status={order.status} />
                        <span className="capitalize">{order.status}</span>
                      </div>
                    </TableCell>
                    <TableCell>{formatPrice(order.totalAmount)}</TableCell>
                    <TableCell>
                      <Link to={`/orders/${order.id}`}>
                        <Button variant="outline" size="sm">
                          View Details
                        </Button>
                      </Link>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        )}
      </div>
    </MainLayout>
  );
};

export default OrdersPage;
