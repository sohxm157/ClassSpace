# 📚 ClassSpace – Frontend Application

ClassSpace is a smart timetable and learning platform that provides **role-based access** for Students and Teachers.  
This repository contains the **frontend implementation** of the ClassSpace login and authentication flow.

---

## 🧠 Overview

The ClassSpace frontend allows users to:
- Log in securely using email and password
- Choose a role (Student / Teacher)
- Get redirected to role-specific dashboards
- Handle authentication errors gracefully

The system follows a **modern frontend–backend architecture** with token-based authentication.

---

## 🏗️ High-Level Architecture

```mermaid
flowchart LR
    U[User Browser] -->|HTTP Requests| F[Frontend Application]
    F -->|REST API| B[Backend Server]
    B -->|Queries| D[(Database)]
    B -->|JWT Token| F

flowchart TD
    A[User opens Login Page] --> B[Enter Email & Password]
    B --> C[Select Role: Student / Teacher]

    C --> D[Frontend Validation]
    D -->|Invalid| E[Show Validation Error]
    D -->|Valid| F[Send Login Request]

    F --> G[Backend Authentication]
    G --> H{Credentials Valid?}

    H -->|No| I[Return Error Message]
    H -->|Yes| J[Generate JWT Token]

    J --> K{User Role}
    K -->|Student| L[Redirect to Student Dashboard]
    K -->|Teacher| M[Redirect to Teacher Dashboard]

    flowchart TD
    A[Login Page Component]
    A --> B[Email Input State]
    A --> C[Password Input State]
    A --> D[Role Selection State]

    B --> E[Form Submit]
    C --> E
    D --> E

    E --> F[API Call (Fetch / Axios)]
    F --> G{Response}

    G -->|Success| H[Store JWT Token]
    G -->|Error| I[Display Error Message]

flowchart TD
    A[POST /auth/login] --> B[Auth Controller]
    B --> C[User Service]
    C --> D[Database]

    D -->|User Found| E[Password Verification]
    D -->|User Not Found| F[Auth Error]

    E -->|Valid| G[Generate JWT Token]
    E -->|Invalid| H[Wrong Password Error]

    G --> I[Send Token & User Role]

flowchart TD
    A[Protected Route Access] --> B[JWT Middleware]
    B -->|Invalid Token| C[401 Unauthorized]
    B -->|Valid Token| D[Extract Role]

    D --> E{Role Type}
    E -->|Student| F[Student Routes]
    E -->|Teacher| G[Teacher Routes]

flowchart TD
    A[Login Success] --> B[JWT Token Issued]
    B --> C[Store Token (LocalStorage)]
    C --> D[Attach Token to API Headers]
    D --> E[Access Protected Resources]

flowchart TD
    A[API Request Failure] --> B{Error Type}
    B -->|401| C[Session Expired]
    B -->|403| D[Access Denied]
    B -->|500| E[Server Error]

    C --> F[Redirect to Login Page]
    D --> G[Show Authorization Error]
    E --> H[Show Retry Message]

