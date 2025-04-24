export interface LoginWithUsernameRequest {
  username: string;
  password: string;
}

export interface LoginWithEmailRequest {
  email: string;
  password: string;
}

export type LoginRequest = LoginWithUsernameRequest | LoginWithEmailRequest;

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  address: string;
  phoneNumber: string;
  role: string;
}

export interface AuthResponse {
  token: string;
  user: {
    id: number;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
  };
  expiresAt: number;
}
