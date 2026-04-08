# Codetwin - Tier 2 Roadmap

## 🎯 Goal
Upgrade Codetwin from MVP → Real Social Media Experience

---

## 🚀 Phase 1: Core Social Features (High Priority)

### 1. Follow System
- Users can follow/unfollow other users
- Display followers & following count
- Feed should prioritize posts from followed users

#### Backend
- Create `follows` table:
  - id
  - follower_id
  - following_id
  - created_at

#### APIs
- POST /api/users/{id}/follow
- POST /api/users/{id}/unfollow
- GET /api/users/{id}/followers
- GET /api/users/{id}/following

---

### 2. Notifications System
- Notify users for:
  - Likes
  - Comments
  - Follows

#### Backend
- Create `notifications` table:
  - id
  - user_id (receiver)
  - type (LIKE, COMMENT, FOLLOW)
  - reference_id (post/comment/user)
  - is_read
  - created_at

#### APIs
- GET /api/notifications
- POST /api/notifications/{id}/read

---

### 3. Image Upload Support
- Allow users to upload images with posts

#### Backend
- Integrate cloud storage (Cloudinary / AWS S3)
- Store image URL in posts table

#### Update Posts Table
- image_url (nullable)

---

## 🚀 Phase 2: Content & Interaction

### 4. Edit & Delete Posts
- Users can edit/delete their own posts

#### APIs
- PUT /api/posts/{id}
- DELETE /api/posts/{id}

---

### 5. Search System
- Search users
- Search posts

#### APIs
- GET /api/search/users?q=
- GET /api/search/posts?q=

---

### 6. Pagination (Infinite Scroll)
- Load posts in chunks

#### Backend
- Add pagination params:
  - page
  - size

---

## 🚀 Phase 3: Social Depth

### 7. Comment Replies (Threading)
- Allow replies to comments

#### Update Comments Table
- parent_comment_id (nullable)

---

### 8. Mentions & Hashtags
- @username → tag users
- #topic → categorize posts

---

### 9. Profile Enhancements
- Add:
  - Bio
  - Profile picture upload
  - Followers / Following list
  - User posts grid

---

## 🎨 UI/UX Improvements

### Feed
- Card elevation
- Animated like button
- Double tap to like

### Navigation
- Add bottom navigation:
  - Home
  - Search
  - Notifications
  - Profile

### Post UI
- Add:
  - Like
  - Comment
  - Save (future)
  - Share (future)

### UX Enhancements
- Pull to refresh
- Empty states
- Loading skeletons (already implemented)

---

## 🔐 Backend Improvements
- Input validation
- Rate limiting
- Proper DTO structure
- Service layer refinement

---

## 🧠 Optional Advanced Features
- Bookmark posts
- Share posts
- Real-time notifications (WebSocket)
- Developer-specific features (code snippets)

---

## ✅ Outcome
After Tier 2:
- Fully functional social media app
- Production-ready architecture
- Strong portfolio project