import {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {Menu, Search, ShoppingCart, User, X} from 'lucide-react';
import {Button} from '../ui/button';
import {Input} from '../ui/input';
import {Badge} from '../ui/badge';
import {Sheet, SheetContent, SheetTrigger} from '../ui/sheet';
import {useCart} from '../../context/CartContext';
import {useAuth} from '../../context/AuthContext';

export const Header = () => {
  const { cartItemCount } = useCart();
  const { isAuthenticated, user, logout } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const navigate = useNavigate();

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      const query = searchQuery.trim();
      console.log('🔍 Submitting search from navbar:', query);
      navigate(`/products?search=${encodeURIComponent(query)}`);
      setIsSearchOpen(false);
    }
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-16 items-center justify-between">
        {/* Logo */}
        <div className="flex items-center">
          <Link to="/" className="flex items-center">
            <span className="text-xl font-bold text-primary">EchoCart</span>
          </Link>
        </div>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex items-center space-x-6">
          <Link to="/" className="text-sm font-medium transition-colors hover:text-primary">
            Home
          </Link>
          <Link to="/products" className="text-sm font-medium transition-colors hover:text-primary">
            Products
          </Link>
          <Link to="/about" className="text-sm font-medium transition-colors hover:text-primary">
            About
          </Link>
        </nav>

        {/* Search, Cart and Profile */}
        <div className="flex items-center gap-2">
          {/* Search Sheet */}
          <Sheet open={isSearchOpen} onOpenChange={setIsSearchOpen}>
            <SheetTrigger asChild>
              <Button variant="ghost" size="icon" className="mr-2">
                <Search className="h-5 w-5" />
                <span className="sr-only">Search</span>
              </Button>
            </SheetTrigger>
            <SheetContent side="top" className="w-full p-0">
              <div className="container py-10">
                <form onSubmit={handleSearchSubmit}>
                  <div className="relative">
                    <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input 
                      placeholder="Search products..." 
                      className="pl-8" 
                      value={searchQuery}
                      onChange={handleSearchChange}
                      autoFocus
                    />
                    <Button 
                      type="submit" 
                      className="absolute right-0 top-0 h-full px-4"
                      disabled={!searchQuery.trim()}
                    >
                      Search
                    </Button>
                  </div>
                </form>
              </div>
            </SheetContent>
          </Sheet>

          {/* Cart Button */}
          <Link to="/cart">
            <Button variant="ghost" size="icon" className="relative">
              <ShoppingCart className="h-5 w-5" />
              {cartItemCount > 0 && (
                <Badge className="absolute -top-1 -right-1 h-5 w-5 flex items-center justify-center p-0 bg-primary">
                  {cartItemCount}
                </Badge>
              )}
              <span className="sr-only">Cart</span>
            </Button>
          </Link>

          {/* Profile Button / Auth */}
          {isAuthenticated ? (
            <div className="relative group">
              <Button variant="ghost" size="icon">
                <User className="h-5 w-5" />
                <span className="sr-only">Profile</span>
              </Button>
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 ease-in-out transform translate-y-1 group-hover:translate-y-0">
                <p className="px-4 py-2 text-sm text-gray-700 border-b">
                  Hi, {user?.firstName ? `${user.firstName} ${user.lastName}` : 'Guest'}
                </p>
                <Link to="/profile" className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                  Your Profile
                </Link>
                <Link to="/orders" className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                  Your Orders
                </Link>
                <button 
                  onClick={logout}
                  className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                >
                  Sign Out
                </button>
              </div>
            </div>
          ) : (
            <Link to="/login">
              <Button variant="ghost" size="sm">
                Login
              </Button>
            </Link>
          )}

          {/* Mobile Menu Button */}
          <Button 
            variant="ghost" 
            size="icon" 
            className="md:hidden" 
            onClick={toggleMobileMenu}
          >
            {mobileMenuOpen ? (
              <X className="h-5 w-5" />
            ) : (
              <Menu className="h-5 w-5" />
            )}
            <span className="sr-only">Menu</span>
          </Button>
        </div>
      </div>

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden w-full bg-background border-t">
          <div className="container py-4 flex flex-col space-y-3">
            <Link to="/" className="text-sm font-medium py-2 hover:text-primary" onClick={toggleMobileMenu}>
              Home
            </Link>
            <Link to="/products" className="text-sm font-medium py-2 hover:text-primary" onClick={toggleMobileMenu}>
              Products
            </Link>
            <Link to="/about" className="text-sm font-medium py-2 hover:text-primary" onClick={toggleMobileMenu}>
              About
            </Link>
          </div>
        </div>
      )}
    </header>
  );
};
