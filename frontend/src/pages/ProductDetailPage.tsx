import {Link, useNavigate, useParams} from 'react-router-dom';
import {useState} from 'react';
import {MainLayout} from '../components/Layout/MainLayout';
import {Button} from '../components/ui/button';
import {ChevronRight, MinusCircle, PlusCircle, ShoppingCart} from 'lucide-react';
import {useAddToCart} from '../hooks/use-cart';
import {Tabs, TabsContent, TabsList, TabsTrigger} from '../components/ui/tabs';
import {Badge} from '../components/ui/badge';
import {useProduct} from '../hooks/use-products';
import {Skeleton} from '../components/ui/skeleton';
import {Alert, AlertDescription, AlertTitle} from '../components/ui/alert';

const ProductDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const [quantity, setQuantity] = useState(1);
  const navigate = useNavigate();

  // Fetch product data
  const { data: product, isLoading, error } = useProduct(id);

  // Add to cart mutation
  const { mutate: addToCart, isPending: isAddingToCart } = useAddToCart();

  const handleIncreaseQuantity = () => {
    if (product && product.quantity && quantity < product.quantity) {
      setQuantity(quantity + 1);
    }
  };

  const handleDecreaseQuantity = () => {
    if (quantity > 1) {
      setQuantity(quantity - 1);
    }
  };

  const handleAddToCart = () => {
    if (product) {
      addToCart({
        productId: product.productID.toString(),
        quantity: quantity
      });
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  // Loading state
  if (isLoading) {
    return (
      <MainLayout>
        <div className="container py-8 px-4 md:px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
            <Skeleton className="aspect-square rounded-lg" />
            <div className="space-y-6">
              <Skeleton className="h-10 w-3/4" />
              <Skeleton className="h-6 w-1/4" />
              <Skeleton className="h-20 w-full" />
              <Skeleton className="h-10 w-1/3" />
              <div className="pt-4 border-t border-gray-200">
                <Skeleton className="h-12 w-full" />
              </div>
            </div>
          </div>
        </div>
      </MainLayout>
    );
  }

  // Error state
  if (error || !product) {
    return (
      <MainLayout>
        <div className="container py-16 px-4 md:px-6">
          <Alert variant="destructive" className="mb-6">
            <AlertTitle>Error</AlertTitle>
            <AlertDescription>
              {(error as Error)?.message || "Product not found"}
            </AlertDescription>
          </Alert>

          <div className="text-center">
            <h2 className="text-2xl font-bold mb-4">Product Not Found</h2>
            <p className="mb-6">The product you're looking for doesn't exist or has been removed.</p>
            <Link to="/products">
              <Button>Return to Products</Button>
            </Link>
          </div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="container py-8 px-4 md:px-6">
        {/* Breadcrumb */}
        <nav className="flex mb-6 text-sm text-gray-500">
          <Link to="/" className="hover:text-primary">Home</Link>
          <ChevronRight className="mx-2 h-4 w-4" />
          <Link to="/products" className="hover:text-primary">Products</Link>
          <ChevronRight className="mx-2 h-4 w-4" />
          <Link to={`/products`} className="hover:text-primary">
            {product.category}
          </Link>
          <ChevronRight className="mx-2 h-4 w-4" />
          <span className="text-gray-900 font-medium">{product.name}</span>
        </nav>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Product Image */}
          <div>
            <div className="aspect-square rounded-lg overflow-hidden bg-gray-100">
              <img 
                src={product.imageURL} 
                alt={product.name} 
                className="w-full h-full object-cover"
              />
            </div>
          </div>

          {/* Product Info */}
          <div className="space-y-6">
            <div>
              {product.featured && (
                <Badge className="mb-2">Featured</Badge>
              )}
              <h1 className="text-3xl font-bold">{product.name}</h1>
            </div>

            <div>
              <p className="text-3xl font-bold text-primary">{formatPrice(product.price)}</p>
              <p className={`text-sm mt-2 ${product.quantity > 0 ? 'text-green-600' : 'text-red-600'}`}>
                Quantity: {product.quantity}
              </p>
            </div>

            <p className="text-gray-600">{product.description}</p>

            <div className="pt-4 border-t border-gray-200">
              <div className="flex items-center space-x-4">
                <div className="flex items-center border rounded-md">
                  <Button 
                    variant="ghost" 
                    size="icon" 
                    className="h-10 w-10 rounded-none"
                    onClick={handleDecreaseQuantity}
                    disabled={quantity <= 1}
                  >
                    <MinusCircle className="h-4 w-4" />
                  </Button>

                  <span className="w-12 text-center">{quantity}</span>

                  <Button 
                    variant="ghost" 
                    size="icon" 
                    className="h-10 w-10 rounded-none"
                    onClick={handleIncreaseQuantity}
                    disabled={quantity >= product.quantity}
                  >
                    <PlusCircle className="h-4 w-4" />
                  </Button>
                </div>

                <Button 
                  className="flex-1 gap-2" 
                  size="lg"
                  onClick={handleAddToCart}
                  disabled={(product.quantity <= 0) || isAddingToCart}
                >
                  {isAddingToCart ? (
                    "Adding..."
                  ) : (
                    <>
                      <ShoppingCart className="h-5 w-5" />
                      Add to Cart
                    </>
                  )}
                </Button>
              </div>
            </div>
          </div>
        </div>

        {/* Product details tabs */}
        <div className="mt-16">
          <Tabs defaultValue="description">
            <TabsList className="grid grid-cols-2 w-full max-w-md">
              <TabsTrigger value="description">Description</TabsTrigger>
              <TabsTrigger value="specifications">Specifications</TabsTrigger>
            </TabsList>

            <TabsContent value="description" className="mt-6 space-y-4">
              <h3 className="text-xl font-semibold">Product Description</h3>
              <p className="text-gray-600">{product.description}</p>
              {/*<p className="text-gray-600">*/}
              {/*  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla quam velit, vulputate eu pharetra nec, mattis ac neque. Duis vulputate commodo lectus, ac blandit elit tincidunt id. Sed rhoncus, tortor sed eleifend tristique, tortor mauris molestie elit.*/}
              {/*</p>*/}
              {/*<p className="text-gray-600">*/}
              {/*  Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus.*/}
              {/*</p>*/}
            </TabsContent>

            {/*<TabsContent value="specifications" className="mt-6 space-y-4">*/}
            {/*  <h3 className="text-xl font-semibold">Specifications</h3>*/}
            {/*  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">*/}
            {/*    <div className="space-y-3">*/}
            {/*      <div className="grid grid-cols-2 border-b pb-2">*/}
            {/*        <p className="text-gray-500">Category</p>*/}
            {/*        <p className="font-medium">{product.category}</p>*/}
            {/*      </div>*/}
            {/*      <div className="grid grid-cols-2 border-b pb-2">*/}
            {/*        <p className="text-gray-500">Brand</p>*/}
            {/*        <p className="font-medium">EchoCart</p>*/}
            {/*      </div>*/}
            {/*      <div className="grid grid-cols-2 border-b pb-2">*/}
            {/*        <p className="text-gray-500">Weight</p>*/}
            {/*        <p className="font-medium">0.5 kg</p>*/}
            {/*      </div>*/}
            {/*      <div className="grid grid-cols-2 border-b pb-2">*/}
            {/*        <p className="text-gray-500">Dimensions</p>*/}
            {/*        <p className="font-medium">10 × 10 × 10 cm</p>*/}
            {/*      </div>*/}
            {/*    </div>*/}
            {/*    <div className="space-y-3">*/}
            {/*      <div className="grid grid-cols-2 border-b pb-2">*/}
            {/*        <p className="text-gray-500">Material</p>*/}
            {/*        <p className="font-medium">Metal/Plastic</p>*/}
            {/*      </div>*/}
            {/*      <div className="grid grid-cols-2 border-b pb-2">*/}
            {/*        <p className="text-gray-500">Color</p>*/}
            {/*        <p className="font-medium">Black</p>*/}
            {/*      </div>*/}
            {/*      <div className="grid grid-cols-2 border-b pb-2">*/}
            {/*        <p className="text-gray-500">Warranty</p>*/}
            {/*        <p className="font-medium">1 Year</p>*/}
            {/*      </div>*/}
            {/*    </div>*/}
            {/*  </div>*/}
            {/*</TabsContent>*/}

          </Tabs>
        </div>
      </div>
    </MainLayout>
  );
};

export default ProductDetailPage;
