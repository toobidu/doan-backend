# QUIZIZZ BACKEND API DOCUMENTATION

## 🚀 TỔNG QUAN HỆ THỐNG

### **Endpoints:**
- **REST API:** `http://localhost:8080`
- **Socket.IO:** `ws://localhost:9092`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

## 🔐 AUTHENTICATION

### **Login**
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "jwt_token",
    "refreshToken": "refresh_token",
    "user": {
      "id": 1,
      "username": "user123"
    }
  }
}
```

### **Register**
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

---

## 🏠 ROOM MANAGEMENT API

### **Tạo Phòng**
```http
POST /api/v1/rooms
Authorization: Bearer {token}
Content-Type: application/json

{
  "roomName": "My Quiz Room",
  "roomMode": "BATTLE_ROYAL",
  "topicId": 1,
  "isPrivate": false,
  "maxPlayers": 10,
  "questionCount": 10,
  "countdownTime": 30
}
```

### **Join Phòng bằng Code**
```http
POST /api/v1/rooms/join
Authorization: Bearer {token}
Content-Type: application/json

{
  "roomCode": "ABC123"
}
```

### **Join Phòng bằng ID**
```http
POST /api/v1/rooms/{roomId}/join
Authorization: Bearer {token}
```

### **Lấy Danh Sách Players**
```http
GET /api/v1/rooms/{roomId}/players
Authorization: Bearer {token}
```

### **Lấy Danh Sách Phòng**
```http
GET /api/v1/rooms?page=0&size=20&search=quiz
Authorization: Bearer {token}
```

---

## 🎮 SOCKET.IO EVENTS

### **Connection & Authentication**
```javascript
const socket = io('ws://localhost:9092', {
  auth: {
    token: 'jwt_token'
  }
});
```

---

## 🏠 ROOM SOCKET EVENTS

### **📥 Client → Server Events**

#### **Tạo Phòng**
```javascript
socket.emit('create-room', {
  roomName: 'My Quiz Room',
  roomMode: 'BATTLE_ROYAL',
  topicId: 1,
  isPrivate: false,
  maxPlayers: 10,
  questionCount: 10,
  countdownTime: 30
});
```

#### **Join Phòng**
```javascript
socket.emit('join-room', {
  roomCode: 'ABC123'
});
```

#### **Rời Phòng**
```javascript
socket.emit('leave-room', {
  roomId: 123
});
```

#### **Lấy Danh Sách Players**
```javascript
socket.emit('get-players', {
  roomId: 123
});
```

#### **Subscribe Room List**
```javascript
socket.emit('subscribe-room-list');
```

### **📤 Server → Client Events**

#### **Room Events**
```javascript
// Tạo phòng thành công
socket.on('room-created-success', (data) => {
  console.log('Room created:', data.room);
  console.log('Players:', data.players);
});

// Join phòng thành công
socket.on('room-joined-success', (data) => {
  console.log('Joined room:', data.room);
  console.log('Players:', data.players);
});

// Player mới join
socket.on('player-joined', (data) => {
  console.log('New player:', data.player);
  console.log('Total players:', data.totalPlayers);
});

// Player rời phòng
socket.on('player-left', (data) => {
  console.log('Player left:', data.player);
  console.log('Remaining players:', data.totalPlayers);
});

// Cập nhật danh sách players
socket.on('room-players-updated', (data) => {
  console.log('Updated players:', data.players);
});

// Thay đổi host
socket.on('host-changed', (data) => {
  console.log('New host:', data.newHostId);
  console.log('Previous host:', data.previousHostId);
});
```

#### **Room List Events**
```javascript
// Phòng mới được tạo
socket.on('room-created', (data) => {
  console.log('New room available:', data.room);
});

// Phòng được cập nhật
socket.on('room-updated', (data) => {
  console.log('Room updated:', data.room);
});

// Phòng bị xóa
socket.on('room-deleted', (data) => {
  console.log('Room deleted:', data.roomId);
});
```

---

## 🎮 GAME SOCKET EVENTS

### **📥 Client → Server Events**

#### **Bắt Đầu Game (Host Only)**
```javascript
socket.emit('start-game', {
  roomId: 123
});
```

#### **Gửi Đáp Án**
```javascript
socket.emit('submit-answer', {
  roomId: 123,
  questionId: 456,
  selectedOptionIndex: 0,
  selectedAnswer: "Option A",
  answerText: "Paris",
  timeTaken: 15.5
});
```

#### **Hiển Thị Kết Quả Câu Hỏi (Host Only)**
```javascript
socket.emit('show-question-result', {
  roomId: 123
});
```

#### **Chuyển Câu Hỏi Tiếp Theo (Host Only)**
```javascript
socket.emit('next-question', {
  roomId: 123
});
```

#### **Kết Thúc Game (Host Only)**
```javascript
socket.emit('end-game', {
  roomId: 123
});
```

### **📤 Server → Client Events**

#### **Game Flow Events**
```javascript
// Game bắt đầu
socket.on('game-started', (data) => {
  console.log('Game started!');
  console.log('First question:', data.question);
  // data.question = {
  //   questionId: 1,
  //   questionText: "What is the capital of France?",
  //   answers: [
  //     { id: 1, answerText: "Paris" },
  //     { id: 2, answerText: "London" },
  //     { id: 3, answerText: "Berlin" },
  //     { id: 4, answerText: "Madrid" }
  //   ],
  //   timeLimit: 30,
  //   questionNumber: 1,
  //   totalQuestions: 10
  // }
});

// Câu hỏi tiếp theo
socket.on('next-question', (data) => {
  console.log('Next question:', data.question);
});

// Đếm ngược thời gian
socket.on('countdown-tick', (data) => {
  console.log('Time remaining:', data.remainingTime);
});

// Hết thời gian
socket.on('time-up', (data) => {
  console.log('Time is up!');
});

// Đáp án đã được gửi
socket.on('answer-submitted', (data) => {
  console.log('Answer result:', data.result);
  // data.result = {
  //   isCorrect: true,
  //   score: 850,
  //   timeTaken: 15.5,
  //   correctAnswerId: 1
  // }
});

// Player khác đã trả lời
socket.on('player-answered', (data) => {
  console.log('Player answered:', data.userId);
});

// Hiển thị kết quả câu hỏi
socket.on('question-result-shown', (data) => {
  console.log('Show question results now');
});

// Game kết thúc
socket.on('game-finished', (data) => {
  console.log('Game finished!');
  console.log('Final results:', data.result);
  // data.result = {
  //   ranking: [
  //     {
  //       rank: 1,
  //       userId: 123,
  //       userName: "Player1",
  //       totalScore: 2500,
  //       totalTime: 180
  //     }
  //   ],
  //   userScores: [...]
  // }
});
```

#### **Error Events**
```javascript
socket.on('error', (data) => {
  console.error('Socket error:', data.message);
});
```

---

## 📊 DATA STRUCTURES

### **Room Object**
```typescript
interface Room {
  id: number;
  roomName: string;
  roomCode: string;
  roomMode: 'ONE_VS_ONE' | 'BATTLE_ROYAL';
  status: 'WAITING' | 'PLAYING' | 'FINISHED';
  isPrivate: boolean;
  maxPlayers: number;
  currentPlayers: number;
  questionCount: number;
  countdownTime: number;
  ownerId: number;
  ownerUsername: string;
  topicId: number;
  topicName: string;
  createdAt: string;
}
```

### **Player Object**
```typescript
interface RoomPlayer {
  userId: number;
  username: string;
  avatarURL?: string;
  isHost: boolean;
  joinOrder: number;
  status: 'ACTIVE' | 'KICKED';
}
```

### **Question Object**
```typescript
interface Question {
  questionId: number;
  questionText: string;
  answers: Answer[];
  timeLimit: number;
  questionNumber: number;
  totalQuestions: number;
}

interface Answer {
  id: number;
  answerText: string;
}
```

### **Game Result Object**
```typescript
interface GameResult {
  ranking: PlayerRanking[];
  userScores: PlayerScore[];
}

interface PlayerRanking {
  rank: number;
  userId: number;
  userName: string;
  totalScore: number;
  totalTime: number;
}
```

---

## 🔄 LUỒNG HOẠT ĐỘNG CHÍNH

### **1. Tạo & Join Phòng**
```
1. User A tạo phòng → API POST /api/v1/rooms
2. User A tự động join phòng và thành host
3. User B, C nhận event 'room-created' (nếu subscribe room-list)
4. User B join phòng → socket.emit('join-room')
5. User A nhận event 'player-joined'
6. User C join phòng → User A, B nhận event 'player-joined'
```

### **2. Chơi Game**
```
1. Host bấm start → socket.emit('start-game')
2. Tất cả players nhận 'game-started' với câu hỏi đầu tiên
3. Players gửi đáp án → socket.emit('submit-answer')
4. Players nhận kết quả cá nhân → 'answer-submitted'
5. Host điều khiển → socket.emit('show-question-result')
6. Host chuyển câu tiếp theo → socket.emit('next-question')
7. Lặp lại cho đến hết câu hỏi
8. Tất cả nhận 'game-finished' với kết quả cuối cùng
```

### **3. Rời Phòng**
```
1. User rời phòng → socket.emit('leave-room')
2. Các user khác nhận 'player-left'
3. Nếu host rời → user join sớm thứ 2 thành host mới
4. Tất cả nhận 'host-changed'
5. Nếu phòng trống → tự động xóa (hoặc archive nếu có lịch sử)
```

---

## ⚡ REAL-TIME FEATURES

- ✅ **Room List Updates:** Danh sách phòng cập nhật real-time
- ✅ **Player Join/Leave:** Cập nhật danh sách players real-time  
- ✅ **Host Transfer:** Chuyển host tự động khi host rời
- ✅ **Game Synchronization:** Tất cả players nhận câu hỏi cùng lúc
- ✅ **Live Countdown:** Đếm ngược thời gian đồng bộ
- ✅ **Answer Tracking:** Theo dõi ai đã trả lời real-time
- ✅ **Game Results:** Kết quả game hiển thị đồng thời

---

## 🛠️ SETUP FRONTEND

### **1. Install Socket.IO Client**
```bash
npm install socket.io-client
```

### **2. Basic Setup**
```javascript
import io from 'socket.io-client';

const socket = io('ws://localhost:9092', {
  auth: {
    token: localStorage.getItem('accessToken')
  }
});

// Room management
socket.on('connect', () => {
  console.log('Connected to server');
  socket.emit('subscribe-room-list');
});

// Game events
socket.on('game-started', handleGameStart);
socket.on('next-question', handleNextQuestion);
socket.on('countdown-tick', handleCountdown);
socket.on('game-finished', handleGameEnd);
```

---

## 🎯 BACKEND STATUS: 100% HOÀN THIỆN

### ✅ **HOÀN THÀNH:**
- Authentication & Authorization
- Room Management (Create, Join, Leave)
- Real-time Player Updates
- Game Flow (Start, Questions, Answers, Results)
- Timer Synchronization
- Host Management
- Socket.IO Events
- Database Schema
- API Documentation

### 🚀 **SẴN SÀNG CHO FRONTEND:**
Backend đã hoàn thiện 100% và sẵn sàng cho việc phát triển frontend với đầy đủ tính năng như Kahoot/Quizizz.