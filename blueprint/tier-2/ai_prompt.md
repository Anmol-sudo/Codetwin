# Codetwin - Tier 2 AI Prompt

## 🎯 Objective
You are an expert software engineer working on Codetwin, a social media Android app.

Your task is to implement Tier 2 features by extending the existing codebase.

The project consists of:
- Frontend: Kotlin (Android, Material 3)
- Backend: Spring Boot (Java, PostgreSQL)

You must follow clean architecture, modular design, and production-level practices.

---

## 🧠 General Instructions

- Do NOT break existing functionality
- Follow existing architecture (Repository pattern, Retrofit, JWT auth)
- Write clean, readable, maintainable code
- Use best practices for Android + Spring Boot
- Ensure API consistency between frontend and backend

---

## ⚙️ Features to Implement

### 1. Follow System
Backend:
- Create Follow entity
- Implement follow/unfollow APIs
- Update user profile response to include:
  - followers_count
  - following_count
  - is_following

Frontend:
- Add follow button in profile screen
- Update UI dynamically on follow/unfollow

---

### 2. Notifications System
Backend:
- Create Notification entity
- Trigger notifications on:
  - Like
  - Comment
  - Follow

Frontend:
- Create notifications screen
- Add notification icon in toolbar
- Display unread indicator

---

### 3. Image Upload in Posts
Backend:
- Integrate image upload (Cloudinary or similar)
- Store image URL in database

Frontend:
- Add image picker
- Show image preview before posting
- Display image in feed

---

### 4. Edit & Delete Posts
Backend:
- Add update & delete endpoints
- Ensure only post owner can modify

Frontend:
- Add menu (3 dots) on post
- Implement edit & delete options

---

### 5. Search Feature
Backend:
- Implement search endpoints for users and posts

Frontend:
- Add search screen
- Show results dynamically

---

### 6. Pagination
Backend:
- Implement pagination for posts API

Frontend:
- Add infinite scroll in feed
- Load more posts on scroll

---

### 7. Comment Replies
Backend:
- Add parent_comment_id field

Frontend:
- Support nested comments UI

---

## 🎨 UI/UX Requirements

- Follow Material 3 design principles
- Improve spacing, typography, and hierarchy
- Add animations for:
  - Like button
  - Navigation transitions

---

## 📦 Deliverables

Backend:
- Entities
- Repositories
- Services
- Controllers
- DTOs

Frontend:
- Activities / Fragments
- Adapters
- ViewModels (if applicable)
- API integration

---

## 🚨 Constraints

- Maintain backward compatibility
- Avoid unnecessary complexity
- Keep code modular and scalable

---

## 🧪 Testing

- Ensure APIs work with Postman
- Handle error states properly
- Validate user inputs

---

## 💡 Bonus (if possible)

- Add optimistic UI updates
- Improve loading states
- Add empty state screens

---

## ✅ Final Goal

Transform Codetwin into a fully functional, scalable, and modern social media application.