import {Link} from 'react-router-dom';
import {Button} from './ui/button';

export const Hero = () => {
  return (
    <section className="bg-gradient-to-br from-primary/10 to-gray-50">
      <div className="container flex flex-col md:flex-row items-center px-4 md:px-6 py-12 md:py-24 gap-8 md:gap-16">
        {/* Text content */}
        <div className="flex-1 space-y-6 text-center md:text-left">
          <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold tracking-tight">
            Shop Smart, <span className="text-primary">Live Better</span>
          </h1>
          <p className="text-lg text-gray-600 max-w-prose">
            Discover a curated selection of high-quality products at competitive prices. From electronics to home essentials, we've got you covered.
          </p>
          <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4 justify-center md:justify-start">
            <Link to="/products">
              <Button size="lg" className="w-full sm:w-auto">
                Shop Now
              </Button>
            </Link>
            <Link to="/products">
              <Button size="lg" variant="outline" className="w-full sm:w-auto">
                Browse Categories
              </Button>
            </Link>
          </div>
        </div>
        
        {/* Hero image */}
        <div className="flex-1">
          <div className="relative">
            <img 
              src="https://images.unsplash.com/photo-1607082350899-7e105aa886ae?q=80&w=2670&auto=format&fit=crop" 
              alt="EchoCart Shopping Experience" 
              className="w-full h-auto object-cover rounded-lg shadow-xl"
            />
            <div className="absolute -bottom-6 -right-6 bg-white p-4 md:p-6 shadow-lg rounded-lg hidden md:block">
              <p className="font-bold text-primary text-xl md:text-2xl">30% OFF</p>
              <p className="text-sm text-gray-600">Selected items</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
