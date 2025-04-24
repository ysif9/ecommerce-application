import {useEffect, useState} from 'react';
import {MainLayout} from '../components/Layout/MainLayout';
import {ProductCard} from '../components/ProductCard';
import {Input} from '../components/ui/input';
import {Button} from '../components/ui/button';
import {Search} from 'lucide-react';
import {useProducts} from '../hooks/use-products';
import {Skeleton} from '../components/ui/skeleton';
import {productService} from '../services/product-service';
import {useLocation, useNavigate} from 'react-router-dom';

const ProductsPage = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // Get search query from URL if it exists
  const queryParams = new URLSearchParams(location.search);
  const searchFromUrl = queryParams.get('search') || '';

  const [selectedCategory, setSelectedCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState(searchFromUrl);
  const [debouncedSearchQuery, setDebouncedSearchQuery] = useState(searchFromUrl);
  const [priceRange, setPriceRange] = useState({ min: 0, max: 1000 });
  const [debouncedPriceRange, setDebouncedPriceRange] = useState({ min: 0, max: 1000 });
  const [categories, setCategories] = useState<string[]>(['All']);

  // Debounce search query without updating URL
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchQuery(searchQuery);
    }, 500); // 500ms delay

    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Debounce price range
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedPriceRange(priceRange);
    }, 500); // 500ms delay

    return () => clearTimeout(timer);
  }, [priceRange]);

  // Create filters object for API request
  const filters = {
    category: selectedCategory !== 'All' ? selectedCategory : undefined,
    name: debouncedSearchQuery || undefined, // For direct backend API compatibility
    search: debouncedSearchQuery || undefined, // For compatibility with our updated product service
    minPrice: debouncedPriceRange.min,
    maxPrice: debouncedPriceRange.max || undefined,
  };

  // Log filters for debugging
  useEffect(() => {
    console.log('🔍 Search filters:', filters);
    console.log('🔍 Search query:', debouncedSearchQuery);
  }, [filters, debouncedSearchQuery]);

  // Fetch products with filters
  const { data, isLoading, error } = useProducts(filters);
  // Update to handle array response directly
  const products = data || [];

  // Fetch all products to extract categories
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const allProducts = await productService.getProducts();
        // Extract unique categories from products
        const uniqueCategories = ['All', ...new Set(allProducts.map(product => product.category))];
        setCategories(uniqueCategories);
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };

    fetchCategories();
  }, []);

  // Add debugging logs
  useEffect(() => {
    console.log('📦 Products data:', data);
    console.log('🔄 Loading state:', isLoading);
    console.log('❌ Error state:', error);
    console.log('🎯 Products array:', products);
    console.log('🏷️ Categories:', categories);
  }, [data, isLoading, error, products, categories]);

  if (isLoading) {
    return (
      <MainLayout>
        <div className="container py-8 px-4 md:px-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[...Array(6)].map((_, i) => (
              <Skeleton key={i} className="h-[400px] w-full" />
            ))}
          </div>
        </div>
      </MainLayout>
    );
  }

  if (error) {
    return (
      <MainLayout>
        <div className="container py-8 px-4 md:px-6">
          <div className="text-center text-red-500">
            Error loading products: {error.message}
          </div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="container py-8 px-4 md:px-6">
        <h1 className="text-3xl font-bold mb-8">All Products</h1>

        {/* Filters and Search */}
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 mb-8">
          {/* Sidebar filters */}
          <div className="lg:col-span-1 space-y-6">
            <div className="rounded-lg border bg-card p-4">
              <h3 className="font-medium mb-3">Categories</h3>
              <div className="space-y-2">
                {categories.map((category) => (
                  <Button
                    key={category}
                    variant={selectedCategory === category ? "default" : "ghost"}
                    className="justify-start w-full"
                    onClick={() => setSelectedCategory(category)}
                  >
                    {category}
                  </Button>
                ))}
              </div>
            </div>

            <div className="rounded-lg border bg-card p-4">
              <h3 className="font-medium mb-3">Price Range</h3>
              <div className="space-y-3">
                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label htmlFor="min-price" className="text-sm text-muted-foreground">
                      Min
                    </label>
                    <Input
                      id="min-price"
                      type="number"
                      value={priceRange.min}
                      onChange={(e) => setPriceRange({ ...priceRange, min: Number(e.target.value) })}
                      className="mt-1"
                    />
                  </div>
                  <div>
                    <label htmlFor="max-price" className="text-sm text-muted-foreground">
                      Max
                    </label>
                    <Input
                      id="max-price"
                      type="number"
                      value={priceRange.max}
                      onChange={(e) => setPriceRange({ ...priceRange, max: Number(e.target.value) })}
                      className="mt-1"
                    />
                  </div>
                </div>
                <Button
                  className="w-full"
                  onClick={() => setPriceRange({ min: 0, max: 1000 })}
                  variant="outline"
                >
                  Reset Price
                </Button>
              </div>
            </div>
          </div>

          {/* Products grid */}
          <div className="lg:col-span-3">
            {/* Search bar */}
            <div className="mb-6 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <Input
                placeholder="Search products..."
                className="pl-10"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>

            {/* Results count */}
            {!isLoading && !error && (
              <p className="text-sm text-gray-500 mb-4">
                Showing {products.length} product{products.length !== 1 ? 's' : ''}
              </p>
            )}

            {/* Products grid */}
            {!isLoading && !error && (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {products.map((product) => (
                  <ProductCard key={product.productID} product={product} />
                ))}
              </div>
            )}

            {/* No results message */}
            {!isLoading && !error && products.length === 0 && (
              <div className="text-center py-12">
                <h3 className="text-lg font-semibold mb-2">No products found</h3>
                <p className="text-gray-500">
                  Try adjusting your search or filter criteria
                </p>
                <Button
                  className="mt-4"
                  onClick={() => {
                    setSearchQuery('');
                    setSelectedCategory('All');
                    setPriceRange({ min: 0, max: 1000 });
                  }}
                >
                  Clear All Filters
                </Button>
              </div>
            )}
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default ProductsPage;
