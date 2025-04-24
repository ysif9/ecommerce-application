import {Button} from './ui/button';
import {Link} from 'react-router-dom';

interface CategoryProps {
  categories: string[];
}

export const CategorySection = ({ categories }: CategoryProps) => {
  // Filter out the 'All' category
  const displayCategories = categories.filter(cat => cat !== 'All');
  
  return (
    <section className="py-16">
      <div className="container px-4 md:px-6">
        <div className="flex flex-col items-center justify-center space-y-4 text-center mb-10">
          <h2 className="text-3xl font-bold tracking-tight text-gray-900 md:text-4xl">Shop by Category</h2>
          <p className="max-w-[700px] text-gray-600">
            Browse our wide range of products by category to find exactly what you're looking for.
          </p>
        </div>
        
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4 md:gap-6">
          {displayCategories.map((category) => (
            <Link key={category} to={`/categories/${category.toLowerCase()}`} className="group">
              <div className="bg-white rounded-lg shadow-sm p-6 h-full flex flex-col items-center justify-center text-center transition-all hover:shadow-md hover:-translate-y-1 border border-gray-200">
                <h3 className="text-lg font-semibold mb-2 group-hover:text-primary transition-colors">{category}</h3>
                <Button variant="link" className="text-sm group-hover:text-primary">
                  View Products
                </Button>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
};
