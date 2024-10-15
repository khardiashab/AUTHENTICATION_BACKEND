

# 🔐 OAuth Authentication Backend

Welcome to the **OAuth Authentication Backend** project! This backend service integrates **Google** and **GitHub** OAuth 2.0 authentication, using custom JWT validation and role-based access control powered by **AspectJ**. It follows a structured and organized project layout, with a focus on security and error handling.

## 🌟 Features

- **Google and GitHub OAuth 2.0** Authentication
- **JWT Token** Generation and Validation
- **Role-Based Access Control** with custom `@HasRole` annotation using **AspectJ**
- **Aspect-Oriented Programming (AOP)** with **AspectJ** for custom annotations and validations
- **Structured Error Handling** for reliable and clear error messages
- **Environment Variable Configuration** with `.env` for secure credentials management

## 🏗️ Project Structure

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.backend
│   │   │       ├── security
│   │   │       │   ├── oauth
│   │   │       │   │   ├── google
│   │   │       │   │   │   ├── GoogleAuthController.java
│   │   │       │   │   │   ├── GoogleAuthHelper.java
│   │   │       │   │   │   └── dtos
│   │   │       │   │   │       ├── AccessToken.java
│   │   │       │   │   │       └── UserInfo.java
│   │   │       │   │   ├── github
│   │   │       │   │   │   ├── GitHubAuthController.java
│   │   │       │   │   │   ├── GitHubAuthHelper.java
│   │   │       │   │   │   └── dtos
│   │   │       │   │   │       ├── GitHubAccessToken.java
│   │   │       │   │   │       └── GitHubUserInfo.java
│   │   │       │   │   ├── exceptions
│   │   │       │   │   │   ├── CustomOauthException.java
│   │   │       │   │   │   └── OauthExceptionHandler.java
│   │   │       │   ├── user
│   │   │       │   │   ├── UserController.java
│   │   │       │   │   ├── UserService.java
│   │   │       │   │   ├── UserRepository.java
│   │   │       │   │   ├── entity
│   │   │       │   │   │   ├── User.java
│   │   │       │   │   │   └── Authority.java
│   │   │       │   │   └── dtos
│   │   │       │   │       └── UserInfoResponse.java
│   │   │       │   ├── jwt
│   │   │       │   │   ├── JwtValidator.java
│   │   │       │   │   ├── JwtTokenProvider.java
│   │   │       │   │   ├── JwtSignKeyProvider.java
│   │   │       │   │   ├── JwtValidated.java
│   │   │       │   │   └── exceptions
│   │   │       │   │       └── CustomJwtException.java
│   │   │       │   ├── service
│   │   │       │   │   ├── HasRole.java
│   │   │       │   │   ├── HasRoleAspect.java
│   │   │       │   │   ├── SecurityConfig.java
│   │   │       │   │   └── SecuritySerivce.java
│   │   │       ├── config
│   │   │       │   └── AppConfig.java
│   │   │       ├── exceptions
│   │   │       │   ├── CustomException.java
│   │   │       │   └── FailedToLoadEnvException.java
│   │   │       ├── BackendApplication.java
│   │   │       └── DemoEndPoint.java
│   │   └── resources
│   │       ├── application.yml
│   │       └── META-INF
│   └── test
│       └── java
│           └── com.backend
│               └── BackendApplicationTests.java
```

## 🛠️ Technologies Used

- **Spring Boot**
- **OAuth 2.0** with Google and GitHub
- **JWT (JSON Web Token)** for authentication
- **AspectJ** for AOP (Aspect-Oriented Programming)
- **Lombok** for reducing boilerplate code
- **Spring Security** for authentication and security
- **Environment Variables** with **Dotenv**
- **RestTemplate** for HTTP requests

## 🚀 Getting Started

### 1. Prerequisites

Ensure the following are installed:
- Java 17+
- Maven
- Git
- Google and GitHub OAuth credentials
- `.env` file for environment variables

### 2. Clone the Repository

```bash
git clone https://github.com/khardiashab/AUTHENTICATION_BACKEND.git
cd AUTHENTICATION_BACKEND
```

### 3. Set Up Environment Variables

Create a `.env` file at the root of your project:

```
GOOGLE_AUTH_CLIENT_ID=your-google-client-id
GOOGLE_AUTH_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
JWT_SECRET=your-jwt-secret
```

### 4. Run the Application

To start the application, run:

```bash
mvn spring-boot:run
```

Your backend should now be running at `http://localhost:8080`.

## 🔐 Security Overview

### Custom Annotations

- `@JwtValidated`: Validates the JWT token for secured endpoints.
- `@HasRole`: Restricts access based on roles using **AspectJ**.

### SecurityContext

Custom `SecurityContext` is used to store authentication details during the request lifecycle, providing easy access to user info in your controllers.

### Example Usage

```java
@GetMapping("/secure-endpoint")
@JwtValidated
@HasRole(Authority.ADMIN)
public ResponseEntity<String> securedEndpoint() {
    return ResponseEntity.ok("Access granted to secure endpoint!");
}
```

## 🔄 Google and GitHub OAuth Flow

1. The frontend triggers authentication by calling `/api/auth/google` or `/api/auth/github`.
2. The user is redirected to the OAuth provider for authentication.
3. The OAuth provider redirects back to `/api/auth/google/callback` or `/api/auth/github/callback`.
4. The backend retrieves the access token and user info, then generates a JWT for the frontend.

## 🔑 JWT Token

The JWT is used to secure protected routes. Include it in the `Authorization` header:

```
Authorization: Bearer your-jwt-token
```

## ⚠️ Error Handling

Custom exceptions are used for OAuth, JWT, and role validation errors, and are mapped to meaningful HTTP responses.

## 📜 License

This project is licensed under the MIT License.
