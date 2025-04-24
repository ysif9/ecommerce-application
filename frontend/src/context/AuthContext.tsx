import {createContext, ReactNode, useContext, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {toast} from '../components/ui/use-toast';
import {authService} from '../services/auth-service';
import {AuthResponse} from '../types/auth';

type AuthContextType = {
  user: AuthResponse['user'] | null;
  login: (identifier: string, password: string, isEmail?: boolean) => Promise<void>;
  register: (userData: {
    username: string;
    password: string;
    email: string;
    firstName: string;
    lastName: string;
    address: string;
    phoneNumber: string;
    role: string;
  }) => Promise<void>;
  logout: () => void;
  updateUser: (userData: Partial<AuthResponse['user']>) => void;
  isAuthenticated: boolean;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState<AuthResponse['user'] | null>(() => {
    try {
      const storedUser = localStorage.getItem('user');
      return storedUser && storedUser !== 'undefined' ? JSON.parse(storedUser) : null;
    } catch (error) {
      console.error('Error parsing stored user data:', error);
      localStorage.removeItem('user');
      return null;
    }
  });


  const login = async (identifier: string, password: string, isEmail: boolean = false) => {
    console.log(`[Auth] Attempting login with ${isEmail ? 'email' : 'username'}: ${identifier}`);
    try {
      const response = await authService.login(isEmail ? { email: identifier, password } : { username: identifier, password });
      console.log('[Auth] Login successful, setting user data and token');
      setUser(response.user);
      console.log('[Auth] User state updated:', response.user);
      try {
        localStorage.setItem('user', JSON.stringify(response.user));
        const storedUser = localStorage.getItem('user');
        console.log('[Auth] Verified localStorage state:', {
          userStored: !!storedUser,
          user: JSON.parse(storedUser || 'null'),
        });
      } catch (storageError) {
        console.error('[Auth] Error storing auth data:', storageError);
      }

      toast({
        title: 'Login Successful',
        description: 'Welcome back!',
      });

      console.log('[Auth] Attempting navigation to home page...');
      navigate('/');
      console.log('[Auth] Navigation completed');
    } catch (error: any) {
      console.error('[Auth] Login failed:', error);
      toast({
        title: 'Login Failed',
        description: error.message || 'Invalid username or password',
        variant: 'destructive',
      });
      throw error;
    }
  };

  const register = async (userData: {
    username: string;
    password: string;
    email: string;
    firstName: string;
    lastName: string;
    address: string;
    phoneNumber: string;
    role: string;
  }) => {
    try {
      const response = await authService.register(userData);
      setUser(response.user);
      localStorage.setItem('user', JSON.stringify(response.user));
      toast({
        title: 'Registration Successful',
        description: 'Welcome to our store!',
      });
      navigate('/');
    } catch (error: any) {
      toast({
        title: 'Registration Failed',
        description: error.message || 'Please try again',
        variant: 'destructive',
      });
      throw error;
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);

    navigate('/login');
  };

  const updateUser = (userData: Partial<AuthResponse['user']>) => {
    if (!user) return;

    const updatedUser = { ...user, ...userData };
    setUser(updatedUser);

    try {
      localStorage.setItem('user', JSON.stringify(updatedUser));
    } catch (error) {
      console.error('[Auth] Error updating user data in localStorage:', error);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        register,
        logout,
        updateUser,
        isAuthenticated: !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
