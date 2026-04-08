# Project Roadmap & Implemented Features

This document provides a comprehensive list of all features added to **Codetwin** from the initial setup to the current state.

## 1. Backend Development (Spring Boot)
- **User Authentication:** Robust JWT (JSON Web Token) authentication for secure login and registration.
- **Refresh Token System:** Automatic token refresh logic to maintain user sessions without frequent logins.
- **Security:** Spring Security implementation with `JwtFilter` and custom `UserDetailsService`.
- **API Endpoints:**
  - `POST /api/users/register` & `/api/users/login`
  - `GET /api/posts`: Fetch feed (with likes and comments).
  - `GET /api/posts/{id}`: Fetch individual post detail.
  - `POST /api/posts`: Create new posts.
  - `POST /api/posts/{id}/like`: Toggle likes.
  - `POST /api/posts/{id}/comment`: Add interactive comments.
  - `GET /api/users/{id}`: Fetch detailed user profile and post statistics.
- **Data Model:** PostgreSQL database schema for Users, Posts, Comments, Likes, and Refresh Tokens.
- **Improved Security Logic:** Refactored `User.java` to handle Spring Security requirements while maintaining custom display names for UI.

## 2. Android Application Development (Kotlin)
- **Modern Networking:** Retrofit integration with a custom `AuthInterceptor` for automatic JWT attachment and 401 retry logic.
- **UI/UX Polish:**
  - **Material 3 Design:** Consistent use of Material components and theming.
  - **Shimmer Loading:** Integrated Facebook Shimmer for professional skeleton loading states.
  - **Relative Timestamps:** Real-time relative date formatting (e.g., "Just now", "2h").
  - **Smart Avatars:** Initial-based color-coded avatars generated from usernames.
- **Core Modules:**
  - **Auth Module:** Login and Register screens with error body parsing.
  - **Home Feed:** Scrollable feed with Like/Unlike and quick comment shortcuts.
  - **Post Details:** A detailed view for reading full content and viewing the comment thread.
  - **Profile Module:** Personal user profiles showing total posts and user details.
  - **Theme Support:** Easy toggle between Light and Dark modes.
- **Comment UI Refactor:** Implemented modern "Message Bubbles" with avatars and relative time for a more social feel.
- **Build System:** Updated AGP to 9.1.0 and Min SDK to 26 for better performance and API support.

## 3. Tooling & Architecture
- **View Binding:** Used for safe and efficient UI interactions.
- **Session Management:** Secure SharedPreferences storage for tokens and user IDs.
- **Repository Pattern:** Clean separation of concerns between API calls and UI logic.
