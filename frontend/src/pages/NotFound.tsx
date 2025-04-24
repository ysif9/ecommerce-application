import {Link, useLocation} from "react-router-dom";
import {useEffect} from "react";
import {Button} from '../components/ui/button';

const NotFound = () => {
  const location = useLocation();

  useEffect(() => {
    console.error(
      "404 Error: User attempted to access non-existent route:",
      location.pathname
    );
  }, [location.pathname]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <div className="text-center max-w-md">
        <h1 className="text-9xl font-bold text-primary mb-4">404</h1>
        <p className="text-2xl font-medium text-gray-800 mb-6">Page Not Found</p>
        <p className="text-gray-600 mb-8">
          The page you are looking for doesn't exist or has been moved. 
          Let's get you back on track.
        </p>
        <div className="space-y-3">
          <Link to="/">
            <Button className="w-full">
              Return to Home
            </Button>
          </Link>
          <Link to="/products">
            <Button variant="outline" className="w-full">
              Browse Products
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default NotFound;
