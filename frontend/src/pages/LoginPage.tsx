import * as React from 'react';
import {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {MainLayout} from '../components/Layout/MainLayout';
import {Button} from '../components/ui/button';
import {Input} from '../components/ui/input';
import {Label} from '../components/ui/label';
import {useAuth} from '../context/AuthContext';
import {useToast} from '@/hooks/use-toast.ts';

const LoginPage = () => {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [loginType, setLoginType] = useState<'email' | 'username'>('email');
  const { login } = useAuth();
  const navigate = useNavigate();
  const { toast } = useToast();
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    
    try {
      await login(identifier, password, loginType === 'email');
      toast({
        title: 'Login successful',
        description: 'Welcome back!',
      });
      navigate('/');
    } catch (error) {
      console.error('Login error:', error);
      toast({
        title: 'Login failed',
        description: 'Invalid email or password. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setIsLoading(false);
    }
  };
  
  return (
    <MainLayout>
      <div className="flex items-center justify-center py-16 px-4 md:px-6">
        <div className="w-full max-w-md space-y-8">
          <div className="text-center">
            <h1 className="text-3xl font-bold">Welcome back</h1>
            <p className="mt-2 text-gray-600">
              Enter your credentials to access your account
            </p>
          </div>
          
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-4">
              <div className="space-y-2">
                <div className="flex justify-between items-center">
                  <Label htmlFor="identifier">{loginType === 'email' ? 'Email' : 'Username'}</Label>
                  <Button
                    type="button"
                    variant="link"
                    className="px-0"
                    onClick={() => setLoginType(loginType === 'email' ? 'username' : 'email')}
                  >
                    Use {loginType === 'email' ? 'username' : 'email'} instead
                  </Button>
                </div>
                <Input
                  id="identifier"
                  type={loginType === 'email' ? 'email' : 'text'}
                  placeholder={loginType === 'email' ? 'name@example.com' : 'username'}
                  required
                  value={identifier}
                  onChange={(e) => setIdentifier(e.target.value)}
                />
              </div>
              
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <Label htmlFor="password">Password</Label>
                  {/*<Link to="/forgot-password" className="text-sm font-medium text-primary hover:underline">*/}
                  {/*  Forgot password?*/}
                  {/*</Link>*/}
                </div>
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>
            </div>
            
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? 'Loading...' : 'Sign In'}
            </Button>
            
            <div className="text-center text-sm">
              Don't have an account?{' '}
              <Link to="/register" className="font-medium text-primary hover:underline">
                Sign up
              </Link>
            </div>
            
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <span className="w-full border-t" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-background px-2 text-gray-500">
                  Or continue with
                </span>
              </div>
            </div>
            
            <div className="grid grid-cols-2 gap-4">
              <Button variant="outline" type="button">
                Google
              </Button>
              <Button variant="outline" type="button">
                Facebook
              </Button>
            </div>
          </form>
        </div>
      </div>
    </MainLayout>
  );
};

export default LoginPage;
