import {Product} from '../types';
import {ProductCard} from './ProductCard';

interface FeaturedProductsProps {
  products: Product[];
}

export const FeaturedProducts = ({ products }: FeaturedProductsProps) => {
  const featuredProducts = products.filter(product => product.featured);
  
  return (
    <section className="py-12 bg-gray-50">
      <div className="container px-4 md:px-6">
        <div className="flex flex-col items-center justify-center space-y-4 text-center mb-10">
          <h2 className="text-3xl font-bold tracking-tight text-gray-900 md:text-4xl">Featured Products</h2>
          <p className="max-w-[700px] text-gray-600">
            Discover our handpicked selection of premium products that stand out for their quality, innovation, and excellence.
          </p>
        </div>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {featuredProducts.map((product) => (
            <ProductCard key={product.productID} product={product} />
          ))}
        </div>
      </div>
    </section>
  );
};
