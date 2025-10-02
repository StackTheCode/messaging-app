# ⚙️ The backend implements a real-time chat system using Spring Boot + WebSockets (STOMP) + REST APIs + JPA.
Messages can include text or files, support read/unread status, and are persisted in the database.


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

## 1. Message Entity
Represents a chat message stored in the database.

Fields

- id → Primary key (auto-generated).


- sender → User who sends the message.


- recipient → User who receives the message (nullable for public messages).


- content → Text content of the message.


- fileName → Optional file name if a file is attached.


- timestamp → Time the message was sent.


- messageType → Enum (TEXT, IMAGE, FILE, etc.).


- isRead → Whether recipient has read the message.


---

## 2. MessageController
Provides both REST endpoints and WebSocket endpoints for chat.

| Method   | Endpoint                                    | Description                               |
| -------- | ------------------------------------------- | ----------------------------------------- |
| `GET`    | `/api/messages/history/{userId}/{user2Id}`  | Get chat history between two users.       |
| `DELETE` | `/api/messages/history/{user1Id}/{user2Id}` | Clear chat history between two users.     |
| `DELETE` | `/api/messages/{messageId}`                 | Delete a single message (only by sender). |





# WebSocket Endpoints

- /app/chat.send → Send message (STOMP).

- /app/chat.typing → Send typing indicator.



## WebSocket Subscriptions

/user/{userId}/queue/messages → Private user queue for receiving messages.

/user/{userId}/queue/typing → Private queue for typing status.

/topic/chat → Public chat broadcast.

/topic/delete → Notify clients when a message is deleted.

---


## 3. MessageService

Business logic for handling chat.

Key Methods:

- saveMessage(Message msg) → Saves a new message to DB.

- deleteMessage(Long messageId, Long userId) → Deletes a message if current user is sender.

 - clearChatHistory(Long user1Id, Long user2Id) → Deletes all messages between two users.

- userCanAccessMessage(Long messageId, Long userId) → Verifies if user can access a message.





---



## 4. Messaging Flow
   Sending a Message

User sends message via WebSocket → /app/chat.send.

MessageController builds a Message object and persists it.

Saved message is mapped into a MessageDTO (with generated ID).

Message is delivered to:

Recipient’s private queue → /user/{recipientId}/queue/messages.

Sender’s own queue → /user/{senderId}/queue/messages.

.....................................................................................

Receiving Messages

Clients subscribe to /user/{id}/queue/messages to receive private messages.

Clients subscribe to /topic/chat for public messages.

.....................................................................................

Typing Indicator

Client sends typing status via /app/chat.typing.

Recipient receives it on /user/{recipientId}/queue/typing.

.....................................................................................

Deleting a Message

Sender issues DELETE  :  /api/messages/{messageId}.

MessageService verifies ownership.

If deleted → backend broadcasts /topic/delete with {id: messageId} so UIs can remove it in real-time.


---


## 5. User Entity
Represents a chat message stored in the database.

Fields:

- id → Primary key (auto-generated).

- username → Unique username for login and display.

- email → Unique email address.

- password → Hashed password (nullable for OAuth users).

- role → Role of the user (e.g., USER, ADMIN).

- googleId → Optional field for Google OAuth authentication.

- createdAt → Date when the user was registered.

- updatedAt → Date of the last profile update.

---
## 5. User Controller

| Method   | Endpoint                                | Description                                  |
| -------- | --------------------------------------- | -------------------------------------------- |
| `GET`    | `/api/users/{id}`                       | Get user by ID.                              |
| `GET`    | `/api/users/username/{username}`        | Get user by username.                        |
| `GET`    | `/api/users/email/{email}`              | Get user by email.                           |
| `GET`    | `/api/users/search?username={username}` | Search users by username (case-insensitive). |
| `POST`   | `/api/users/register`                   | Register a new user.                         |
| `POST`   | `/api/users/oauth/google`               | Register/Login user via Google OAuth.        |
| `PUT`    | `/api/users/{id}`                       | Update user details.                         |
| `DELETE` | `/api/users/{id}`                       | Delete user account.                         |

---
## 6. User Service

# Contains the business logic for user management.

Key Methods

 - loadUserByUsername(String username) → Loads a user for authentication (used by Spring Security).

 - getUserIdByUsername(String username) → Retrieves user ID by username.

 - registerUser(User user) → Registers a new user with validation.

 - findByEmail(String email) → Finds user by email.

- findByUsername(String username) → Finds user by username.

 - findByGoogleId(String googleId) → Finds user registered via Google.

 - updateUser(Long id, User user) → Updates existing user details.

 - deleteUser(Long id) → Deletes a user account.





---

## 🚀 Getting Started

Clone the repo and build the project:

```bash
git clone https://github.com/your-username/realtime-chat-backend.git
cd realtime-chat-backend
./mvnw clean install
