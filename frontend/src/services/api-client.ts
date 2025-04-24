import axios, {AxiosError, AxiosRequestConfig} from 'axios';

// Base API client configuration
const apiClient = axios.create({
  baseURL: (import.meta.env.VITE_API_URL || '/api').trim(),
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request logging
apiClient.interceptors.request.use(
  (config) => {
    // Enhanced logging for search requests
    if (config.url?.includes('/search')) {
      console.log('🔍 Making SEARCH API request:', {
        method: config.method?.toUpperCase(),
        url: config.baseURL + config.url,
        params: config.params,
        fullUrl: `${config.baseURL}${config.url}${config.params ? `?${new URLSearchParams(config.params as Record<string, string>).toString()}` : ''}`,
      });
    } else {
      console.log('🌐 Making API request:', {
        method: config.method?.toUpperCase(),
        url: config.baseURL + config.url,
        data: config.data,
        headers: config.headers,
        params: config.params,
      });
    }

    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error('❌ Request error:', error);
    return Promise.reject(error);
  }
);

// Add response logging
apiClient.interceptors.response.use(
  (response) => {
    console.log('✅ API response:', {
      status: response.status,
      data: response.data,
    });
    return response;
  },
  (error: AxiosError) => {
    console.error('❌ API error:', {
      status: error.response?.status,
      data: error.response?.data,
      message: error.message,
    });

    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;

// Generic API request function with error handling
export async function apiRequest<T>(
  config: AxiosRequestConfig
): Promise<T> {
  try {
    const response = await apiClient(config);
    return response.data;
  } catch (error) {
    const axiosError = error as AxiosError;
    let errorMessage = 'Something went wrong. Please try again.';

    if (axiosError.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      const responseData = axiosError.response.data as any;
      // Handle both string responses and object responses with message property
      if (typeof responseData === 'string') {
        errorMessage = responseData;
      } else {
        errorMessage = responseData.message || `Error: ${axiosError.response.status}`;
      }
    } else if (axiosError.request) {
      // The request was made but no response was received
      errorMessage = 'No response received from server. Please check your connection.';
    }

    throw new Error(errorMessage);
  }
}
