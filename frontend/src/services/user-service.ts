import {User} from '../types';
import {apiRequest} from './api-client';
import {RegisterRequest as AuthRegisterRequest} from '../types/auth';

export interface RegisterRequest extends AuthRegisterRequest {
  // Using the type from auth.ts for consistency
}

export interface LoginRequest {
  email?: string;
  username?: string;
  password: string;
}

export interface ResetPasswordRequest {
  email: string;
}

export interface UpdatePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

interface LoginResponse {
  user: User;
  token: string;
}

export const userService = {
  // Register new user - Delegate to authService instead
  register: (userData: RegisterRequest) => {
    return apiRequest<LoginResponse>({
      method: 'POST',
      url: '/users/register',
      data: userData,
    });
  },

  // Login with email
  loginWithEmail: (credentials: { email: string; password: string }) => {
    return apiRequest<LoginResponse>({
      method: 'POST',
      url: '/users/login/email',
      data: credentials,
    });
  },

  // Login with username
  loginWithUsername: (credentials: { username: string; password: string }) => {
    return apiRequest<LoginResponse>({
      method: 'POST',
      url: '/users/login/username',
      data: credentials,
    });
  },

  // Get current user profile - Note: This endpoint needs to be implemented on the backend
  getCurrentUser: () => {
    // Fallback to getting user from localStorage until backend endpoint is available
    const user = localStorage.getItem('user');
    if (user) {
      return Promise.resolve(JSON.parse(user) as User);
    }
    return Promise.reject(new Error('User not found'));
  },

  // Get user by ID (admin only)
  getUserById: (id: string) => {
    return apiRequest<User>({
      method: 'GET',
      url: `/users/display-UserDetails/${id}`,
    });
  },

  // Update user profile
  updateUserProfile: (id: string, userData: Partial<User>) => {
    return apiRequest<User>({
      method: 'PUT',
      url: `/users/update-profile/${id}`,
      data: userData,
    });
  },

  // Reset password
  resetPassword: (email: string, oldPassword: string, newPassword: string) => {
    return apiRequest<{ success: boolean; message: string }>({
      method: 'PUT',
      url: '/users/reset-password',
      data: { email, oldPassword, newPassword },
    });
  },

  // Delete user account
  deleteUser: (id: string) => {
    return apiRequest<{ success: boolean }>({
      method: 'DELETE',
      url: `/users/delete/${id}`,
    });
  },

  // Get all users (admin only)
  getAllUsers: () => {
    return apiRequest<User[]>({
      method: 'GET',
      url: '/users/allUsers',
    });
  },
};
