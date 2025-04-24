import {Facebook, Instagram, Twitter} from 'lucide-react';
import {Link} from 'react-router-dom';

export const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-gray-100">
      <div className="container py-12 px-4 md:px-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Logo and about */}
          <div>
            <Link to="/" className="text-xl font-bold text-primary">
              EchoCart
            </Link>
            <p className="mt-4 text-sm text-gray-600 leading-relaxed">
              Your one-stop shop for all your needs. Quality products, competitive prices, and excellent customer service.
            </p>
          </div>

          {/* Quick links */}
          <div>
            <h3 className="font-semibold text-sm tracking-wider uppercase mb-4">Shop</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/products" className="text-sm text-gray-600 hover:text-primary transition-colors">
                  All Products
                </Link>
              </li>
              <li>
                <Link to="/products" className="text-sm text-gray-600 hover:text-primary transition-colors">
                  Categories
                </Link>
              </li>
              <li>
                <Link to="/products" className="text-sm text-gray-600 hover:text-primary transition-colors">
                  Featured Items
                </Link>
              </li>
              <li>
                <Link to="/products" className="text-sm text-gray-600 hover:text-primary transition-colors">
                  Sale
                </Link>
              </li>
            </ul>
          </div>

          {/* Company info */}
          <div>
            <h3 className="font-semibold text-sm tracking-wider uppercase mb-4">Company</h3>
            <ul className="space-y-2">
              <li>
                <Link to="/about" className="text-sm text-gray-600 hover:text-primary transition-colors">
                  About Us
                </Link>
              </li>
              <li>
                <Link to="/" className="text-sm text-gray-600 hover:text-primary transition-colors">
                  Contact
                </Link>
              </li>
              <li>
                <Link to="/" className="text-sm text-gray-600 hover:text-primary transition-colors">
                  Terms & Conditions
                </Link>
              </li>
              <li>
                <Link to="/" className="text-sm text-gray-600 hover:text-primary transition-colors">
                  Privacy Policy
                </Link>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom section */}
        <div className="mt-12 pt-8 border-t border-gray-200 flex flex-col md:flex-row justify-between items-center">
          <p className="text-sm text-gray-600">
            © {currentYear} EchoCart. All rights reserved.
          </p>

          <div className="flex space-x-6 mt-4 md:mt-0">
            <a href="#" className="text-gray-500 hover:text-primary transition-colors">
              <Facebook size={20} />
              <span className="sr-only">Facebook</span>
            </a>
            <a href="#" className="text-gray-500 hover:text-primary transition-colors">
              <Instagram size={20} />
              <span className="sr-only">Instagram</span>
            </a>
            <a href="#" className="text-gray-500 hover:text-primary transition-colors">
              <Twitter size={20} />
              <span className="sr-only">Twitter</span>
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
};
