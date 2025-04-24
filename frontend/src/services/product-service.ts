import {Product} from '../types';
import {apiRequest} from './api-client';

// Update to match actual API response
export type ProductsResponse = Product[];

interface ProductFilters {
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  search?: string;
  name?: string;  // Added for compatibility with backend API
  page?: number;
  limit?: number;
  sort?: string;
}

export const productService = {
  // Get all products with optional filtering
  getProducts: async (filters: ProductFilters = {}) => {
    // If category is specified, use the category endpoint
    if (filters.category && filters.category !== 'All') {
      return apiRequest<ProductsResponse>({
        method: 'GET',
        url: `/products/category/${filters.category}`,
      });
    }

    // If search query is specified (either as name or search), use the search endpoint
    if (filters.name || filters.search) {
      const searchQuery = filters.name || filters.search;
      console.log('Searching for products with query:', searchQuery);

      // Make sure we're calling the correct endpoint with the correct parameter
      const searchResults = await apiRequest<ProductsResponse>({
        method: 'GET',
        url: '/products/search',
        params: { name: searchQuery },
      });

      // If price range is also specified, filter the search results by price
      if (filters.maxPrice !== undefined) {
        const minPrice = filters.minPrice !== undefined ? filters.minPrice : 0;
        return searchResults.filter(
          product => product.price >= minPrice && product.price <= filters.maxPrice!
        );
      }

      return searchResults;
    }

    // If only price range is specified (no search), use the price range endpoint
    if (filters.maxPrice !== undefined) {
      const minPrice = filters.minPrice !== undefined ? filters.minPrice : 0;
      return apiRequest<ProductsResponse>({
        method: 'GET',
        url: `/products/${minPrice}/${filters.maxPrice}`,
      });
    }

    // Default: get all products
    return apiRequest<ProductsResponse>({
      method: 'GET',
      url: '/products',
    });
  },

  // Get a single product by ID
  getProductById: (id: string) => {
    return apiRequest<Product>({
      method: 'GET',
      url: `/products/${id}`,
    });
  },

  // Create a new product (admin only)
  createProduct: (productData: Omit<Product, 'id' | 'createdAt'>) => {
    return apiRequest<Product>({
      method: 'POST',
      url: '/products',
      data: productData,
    });
  },

  // Update an existing product
  updateProduct: (id: string, productData: Partial<Omit<Product, 'id' | 'createdAt'>>) => {
    return apiRequest<Product>({
      method: 'PUT',
      url: `/products/${id}`,
      data: productData,
    });
  },

  // Delete a product
  deleteProduct: (id: string) => {
    return apiRequest<{ success: boolean }>({
      method: 'DELETE',
      url: `/products/${id}`,
    });
  },
};
