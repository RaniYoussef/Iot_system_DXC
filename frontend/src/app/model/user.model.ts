export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  role: string;
  location: string;
  profilePicture?: string;
  profileCompletion: number;
  lastUpdated: string;
  profilePhoto?: string; // 👈 Add this
  oauthUser?: boolean; // ✅ new


}


export interface PasswordState {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
  isVerified: boolean;
  showPasswordFields: boolean;
}

export interface ResetOptions {
  type: 'email' | 'phone';
  value: string;
}