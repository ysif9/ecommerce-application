import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {LoginRequest, RegisterRequest, ResetPasswordRequest, userService} from '../services/user-service';
import {toast} from '@/hooks/use-toast.ts';
import {useAuth} from '../context/AuthContext';
import {useNavigate} from 'react-router-dom';
import {authService} from '../services/auth-service';

// Hook for user registration
export function useRegister() {
  const navigate = useNavigate();
  const { register } = useAuth();

  return useMutation({
    mutationFn: async (userData: RegisterRequest) => {
      // Use the register function from AuthContext instead of calling authService.register directly
      // This ensures that the user state in AuthContext is properly updated after registration
      await register(userData);
      return userData;
    },
    onSuccess: (data) => {
      // The registration is handled by the register function from AuthContext
      // which already updates the user state, stores the user data in localStorage,
      // shows a toast notification, and handles navigation
      // No need to do anything else here
    },
    onError: (error: Error) => {
      toast({
        title: 'Registration Failed',
        description: error.message,
        variant: 'destructive',
      });
    },
  });
}

// Hook for user login
export function useLogin() {
  const navigate = useNavigate();
  const { login: setUserAuthenticated } = useAuth();

  return useMutation({
    mutationFn: async (credentials: LoginRequest) => {
      try {
        if (credentials.email) {
          return await authService.login({ 
            email: credentials.email, 
            password: credentials.password 
          });
        } else if (credentials.username) {
          return await authService.login({ 
            username: credentials.username, 
            password: credentials.password 
          });
        }
        throw new Error('Email or username is required');
      } catch (error) {
        console.error('Login error:', error);
        throw error;
      }
    },
    onSuccess: (data) => {
      // Token is now handled within authService.login
      setUserAuthenticated(data.user.email, "");
      toast({
        title: 'Login Successful',
        description: 'Welcome back!',
      });
      navigate('/');
    },
    onError: (error: Error) => {
      toast({
        title: 'Login Failed',
        description: error.message,
        variant: 'destructive',
      });
    },
  });
}

// Hook for password reset
export function useResetPassword() {
  return useMutation({
    mutationFn: (data: ResetPasswordRequest) => 
      userService.resetPassword(data.email),
    onSuccess: () => {
      toast({
        title: 'Password Reset Email Sent',
        description: 'Please check your email for instructions to reset your password.',
      });
    },
    onError: (error: Error) => {
      toast({
        title: 'Error',
        description: error.message,
        variant: 'destructive',
      });
    },
  });
}

// Hook for getting the current authenticated user's details
export function useCurrentUser() {
  return useQuery({
    queryKey: ['currentUser'],
    queryFn: () => userService.getCurrentUser(),
    retry: false,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

// Hook for getting a user by ID (admin only)
export function useUser(userId: string | undefined) {
  return useQuery({
    queryKey: ['user', userId],
    queryFn: () => userService.getUserById(userId as string),
    enabled: !!userId,
  });
}

// Hook for updating user profile
export function useUpdateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, userData }: { id: string, userData: any }) => 
      userService.updateUserProfile(id, userData),
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['user', variables.id] });
      if (JSON.parse(localStorage.getItem('user') || '{}').id === variables.id) {
        queryClient.invalidateQueries({ queryKey: ['currentUser'] });
        localStorage.setItem('user', JSON.stringify({
          ...JSON.parse(localStorage.getItem('user') || '{}'),
          ...variables.userData
        }));
      }
      toast({
        title: 'Profile Updated',
        description: 'Your profile has been updated successfully.',
      });
    },
    onError: (error: Error) => {
      toast({
        title: 'Error',
        description: error.message,
        variant: 'destructive',
      });
    },
  });
}

// Hook for deleting a user (admin only)
export function useDeleteUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userId: string) => 
      userService.deleteUser(userId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast({
        title: 'User Deleted',
        description: 'The user has been deleted successfully.',
      });
    },
    onError: (error: Error) => {
      toast({
        title: 'Error',
        description: error.message,
        variant: 'destructive',
      });
    },
  });
}
