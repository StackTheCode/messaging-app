# ⚙️ Real-Time Messaging App — Backend

This is the **backend** for a real-time chat application.  
It’s built with **Spring Boot** and provides secure APIs and WebSocket connections for instant communication. The backend takes care of authentication, message delivery, and storing chat history so the frontend (React + TailwindCSS) can focus on the user experience.  

---

## ✨ What this backend does
- Handles **real-time messaging** through WebSockets with <200ms latency
- Provides **REST APIs** for authentication, user management, and sessions
- Uses **JWT tokens** for secure login and multi-device access
- Stores messages and sessions in a **PostgreSQL database**
- Scales to support thousands of concurrent users
- Works seamlessly with the React frontend (separate repo)

---

## 🛠 Tech Stack
- **Spring Boot** (REST + WebSocket)
- **PostgreSQL** for persistence
- **JWT** for authentication
- **Maven** for dependency management
- **Docker** for containerization
- **Render / AWS** for deployment

---

## 🚀 Getting Started

Clone the repo and build the project:

```bash
git clone https://github.com/your-username/realtime-chat-backend.git
cd realtime-chat-backend
./mvnw clean install
