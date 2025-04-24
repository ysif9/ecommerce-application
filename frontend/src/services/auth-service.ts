import {apiRequest} from './api-client';
import {AuthResponse, LoginRequest, LoginWithEmailRequest, RegisterRequest} from '../types/auth';

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const isEmailLogin = (cred: LoginRequest): cred is LoginWithEmailRequest => {
      return 'email' in cred;
    };

    const endpoint = isEmailLogin(credentials) ? '/users/login/email' : '/users/login/username';
    
    try {
      const response = await apiRequest<any>({
        method: 'POST',
        url: endpoint,
        params: credentials // Changed from params to data for consistency
      });

      if (!response || typeof response !== 'object') {
        throw new Error('Invalid response format from server');
      }

      // Transform the backend response into AuthResponse format
      const userData = response.user || response;
      const token = response.token;
      const expiresAt = response.expiresAt || Date.now() + 3600000; // Default to 1 hour expiration if not provided

      if (!token) {
        throw new Error('Invalid authentication response: missing token');
      }

      // Store the token in localStorage for future requests
      localStorage.setItem('token', token);

      const authResponse = {
        token,
        user: {
          id: Number(userData.id),
          username: String(userData.username),
          email: String(userData.email),
          firstName: String(userData.firstName),
          lastName: String(userData.lastName),
          role: String(userData.role)
        },
        expiresAt
      };

      // Store user data in localStorage
      localStorage.setItem('user', JSON.stringify(authResponse.user));

      return authResponse;
    } catch (error) {
      console.error('Auth service login error:', error);
      throw error;
    }
  },

  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    try {
      const response = await apiRequest<any>({
        method: 'POST',
        url: '/users/register',
        data: userData
      });

      if (!response || typeof response !== 'object') {
        throw new Error('Invalid response format from server');
      }

      // Transform the backend response into AuthResponse format
      const user = response.user || response;
      const token = response.token;
      const expiresAt = response.expiresAt || Date.now() + 3600000; // Default to 1 hour expiration if not provided

      if (!token) {
        throw new Error('Invalid authentication response: missing token');
      }

      // Store the token in localStorage for future requests
      localStorage.setItem('token', token);

      const authResponse = {
        token,
        user: {
          id: Number(user.id),
          username: String(user.username),
          email: String(user.email),
          firstName: String(user.firstName),
          lastName: String(user.lastName),
          role: String(user.role)
        },
        expiresAt
      };

      // Store user data in localStorage
      localStorage.setItem('user', JSON.stringify(authResponse.user));

      return authResponse;
    } catch (error) {
      console.error('Auth service registration error:', error);
      throw error;
    }
  },

  logout: () => {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }
};
