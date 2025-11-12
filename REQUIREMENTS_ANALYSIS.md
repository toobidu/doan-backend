# PHÂN TÍCH YÊU CẦU HỆ THỐNG QUIZIZZ

## 1. YÊU CẦU CHỨC NĂNG (Functional Requirements)

### 1.1 Quản lý Người dùng & Xác thực
- **FR-01**: Đăng ký tài khoản với username, email, password, thông tin cá nhân
- **FR-02**: Xác thực email sau khi đăng ký qua verification token
- **FR-03**: Đăng nhập bằng username/password, nhận JWT access token và refresh token
- **FR-04**: Đăng xuất và thu hồi token
- **FR-05**: Quên mật khẩu - gửi mật khẩu mới qua email
- **FR-06**: Đổi mật khẩu khi đã đăng nhập
- **FR-07**: Refresh access token bằng refresh token
- **FR-08**: Quản lý profile cá nhân (xem, cập nhật thông tin)
- **FR-09**: Upload và quản lý avatar
- **FR-10**: Tìm kiếm người dùng theo username/tên
- **FR-11**: Xem profile công khai của người dùng khác

### 1.2 Quản lý Vai trò & Quyền hạn
- **FR-12**: Tạo, sửa, xóa vai trò (Role)
- **FR-13**: Tạo, sửa, xóa quyền hạn (Permission)
- **FR-14**: Gán/xóa quyền cho vai trò
- **FR-15**: Gán/xóa vai trò cho người dùng
- **FR-16**: Kiểm tra quyền truy cập dựa trên JWT và Redis cache

### 1.3 Quản lý Chủ đề & Câu hỏi
- **FR-17**: Tạo, sửa, xóa chủ đề (Topic)
- **FR-18**: Tìm kiếm chủ đề với phân trang
- **FR-19**: Tạo câu hỏi trắc nghiệm (MULTIPLE_CHOICE, TRUE_FALSE, FILL_IN_THE_BLANK)
- **FR-20**: Tạo hàng loạt câu hỏi (bulk create)
- **FR-21**: Cập nhật, xóa câu hỏi
- **FR-22**: Tạo, cập nhật, xóa đáp án cho câu hỏi
- **FR-23**: Lấy câu hỏi ngẫu nhiên theo topic và loại
- **FR-24**: Đếm số câu hỏi có sẵn theo điều kiện
- **FR-25**: Tìm kiếm câu hỏi với filter và phân trang

### 1.4 Tạo Câu hỏi bằng AI
- **FR-26**: Nhập mô tả tự nhiên để AI tạo câu hỏi
- **FR-27**: AI tự động tạo 4 đáp án (1 đúng, 3 sai) cho mỗi câu
- **FR-28**: Validate câu hỏi do AI tạo trước khi lưu
- **FR-29**: Parse và lưu câu hỏi AI vào database

### 1.5 Quản lý Phòng chơi
- **FR-30**: Tạo phòng với cấu hình (tên, chế độ, topic, số người, thời gian)
- **FR-31**: Tạo room code duy nhất 8 ký tự
- **FR-32**: Join phòng bằng room code hoặc room ID
- **FR-33**: Rời phòng (leave room)
- **FR-34**: Tự động chuyển host khi host rời phòng
- **FR-35**: Kick người chơi khỏi phòng (chỉ host)
- **FR-36**: Cập nhật thông tin phòng (chỉ host)
- **FR-37**: Xóa phòng (chỉ host, không có game history)
- **FR-38**: Lấy danh sách người chơi trong phòng
- **FR-39**: Chuyển quyền host cho người khác
- **FR-40**: Mời người chơi vào phòng
- **FR-41**: Phản hồi lời mời (chấp nhận/từ chối)
- **FR-42**: Tìm kiếm phòng public với phân trang

### 1.6 Quản lý Game Session
- **FR-43**: Bắt đầu game (chỉ host)
- **FR-44**: Tạo game session với danh sách câu hỏi ngẫu nhiên
- **FR-45**: Gửi câu hỏi real-time qua Socket.IO
- **FR-46**: Nhận câu trả lời từ người chơi
- **FR-47**: Tính điểm dựa trên độ chính xác và tốc độ
- **FR-48**: Cập nhật bảng xếp hạng real-time
- **FR-49**: Chuyển câu hỏi tiếp theo
- **FR-50**: Kết thúc game và lưu kết quả
- **FR-51**: Countdown timer đồng bộ cho tất cả người chơi

### 1.7 Thống kê & Bảng xếp hạng
- **FR-52**: Xem lịch sử game của bản thân
- **FR-53**: Xem thống kê chi tiết (tổng game, điểm TB, độ chính xác)
- **FR-54**: Xem bảng xếp hạng theo topic
- **FR-55**: Xem thành tích (achievements) đã đạt được
- **FR-56**: Tính toán và cập nhật rank của người chơi
- **FR-57**: Xem top players toàn hệ thống

### 1.8 Real-time Communication (Socket.IO)
- **FR-58**: Kết nối WebSocket với JWT authentication
- **FR-59**: Join/leave room events
- **FR-60**: Broadcast player joined/left
- **FR-61**: Broadcast game started
- **FR-62**: Broadcast next question
- **FR-63**: Broadcast countdown tick
- **FR-64**: Broadcast player answered
- **FR-65**: Broadcast game finished
- **FR-66**: Broadcast room list updates
- **FR-67**: Broadcast host changed

---

## 2. YÊU CẦU PHI CHỨC NĂNG (Non-Functional Requirements)

### 2.1 Hiệu năng (Performance)
- **NFR-01**: Thời gian phản hồi API trung bình < 100ms
- **NFR-02**: Hỗ trợ 1000+ người dùng đồng thời
- **NFR-03**: Độ trễ real-time (Socket.IO) < 50ms
- **NFR-04**: Cache dữ liệu thường xuyên truy cập bằng Redis
- **NFR-05**: Tối ưu query database với indexing

### 2.2 Bảo mật (Security)
- **NFR-06**: Xác thực bằng JWT (access token + refresh token)
- **NFR-07**: Mã hóa mật khẩu bằng BCrypt
- **NFR-08**: Kiểm tra quyền truy cập dựa trên Role-Based Access Control (RBAC)
- **NFR-09**: Blacklist token khi logout
- **NFR-10**: Validate input để tránh SQL Injection, XSS
- **NFR-11**: CORS configuration cho frontend
- **NFR-12**: Rate limiting để chống DDoS
- **NFR-13**: HTTPS cho production

### 2.3 Khả năng mở rộng (Scalability)
- **NFR-14**: Kiến trúc stateless với JWT
- **NFR-15**: Redis cho session management và caching
- **NFR-16**: Horizontal scaling cho Socket.IO server
- **NFR-17**: Database connection pooling
- **NFR-18**: Async processing cho email và background tasks

### 2.4 Độ tin cậy (Reliability)
- **NFR-19**: Transaction management cho các thao tác quan trọng
- **NFR-20**: Soft delete cho dữ liệu quan trọng
- **NFR-21**: Backup database định kỳ
- **NFR-22**: Error handling và logging đầy đủ
- **NFR-23**: Retry mechanism cho email service

### 2.5 Khả năng bảo trì (Maintainability)
- **NFR-24**: Code structure rõ ràng theo layered architecture
- **NFR-25**: API documentation với Swagger/OpenAPI
- **NFR-26**: Logging với SLF4J và Logback
- **NFR-27**: Unit test coverage > 70%
- **NFR-28**: MapStruct cho object mapping
- **NFR-29**: Lombok để giảm boilerplate code

### 2.6 Khả năng sử dụng (Usability)
- **NFR-30**: API response format nhất quán (ApiResponse wrapper)
- **NFR-31**: Message code rõ ràng cho từng lỗi
- **NFR-32**: Validation message dễ hiểu
- **NFR-33**: RESTful API design chuẩn

### 2.7 Tương thích (Compatibility)
- **NFR-34**: Hỗ trợ Java 21+
- **NFR-35**: Tương thích với PostgreSQL 14+
- **NFR-36**: Tương thích với Redis 6+
- **NFR-37**: Cross-browser support cho WebSocket
- **NFR-38**: Mobile-friendly API design

---

## 3. BẢNG ACTOR VÀ USE CASE

### 3.1 Danh sách Actor

| Actor | Mô tả | Quyền hạn chính |
|-------|-------|-----------------|
| **Guest** | Người dùng chưa đăng nhập | Đăng ký, đăng nhập, xem thông tin công khai |
| **Player** | Người chơi đã đăng nhập | Tham gia game, quản lý profile, xem thống kê |
| **Host** | Người tạo và quản lý phòng | Tạo phòng, bắt đầu game, kick player, quản lý câu hỏi |
| **Admin** | Quản trị viên hệ thống | Toàn quyền quản lý user, role, permission, topic, question |
| **AI System** | Hệ thống AI tạo câu hỏi | Tự động tạo câu hỏi từ mô tả tự nhiên |
| **Email System** | Hệ thống gửi email | Gửi email xác thực, reset password |
| **Socket.IO Server** | Server real-time | Broadcast events, quản lý WebSocket connections |

### 3.2 Bảng Use Case theo Actor

#### **GUEST (Người dùng chưa đăng nhập)**

| Use Case ID | Tên Use Case | Mô tả | Priority |
|-------------|--------------|-------|----------|
| UC-G01 | Đăng ký tài khoản | Guest đăng ký tài khoản mới với email, username, password | High |
| UC-G02 | Xác thực email | Guest click link trong email để xác thực tài khoản | High |
| UC-G03 | Đăng nhập | Guest đăng nhập bằng username/password | High |
| UC-G04 | Quên mật khẩu | Guest yêu cầu reset password qua email | Medium |
| UC-G05 | Xem profile công khai | Guest xem thông tin công khai của người chơi khác | Low |

---

#### **PLAYER (Người chơi)**

| Use Case ID | Tên Use Case | Mô tả | Priority |
|-------------|--------------|-------|----------|
| UC-P01 | Quản lý profile | Player xem và cập nhật thông tin cá nhân | High |
| UC-P02 | Upload avatar | Player tải lên ảnh đại diện | Medium |
| UC-P03 | Đổi mật khẩu | Player thay đổi mật khẩu hiện tại | Medium |
| UC-P04 | Tìm kiếm người chơi | Player tìm kiếm người dùng khác | Low |
| UC-P05 | Tìm phòng public | Player tìm kiếm phòng công khai để tham gia | High |
| UC-P06 | Join phòng bằng code | Player nhập room code để vào phòng | High |
| UC-P07 | Join phòng trực tiếp | Player click vào phòng public để tham gia | High |
| UC-P08 | Rời phòng | Player rời khỏi phòng đang tham gia | High |
| UC-P09 | Chơi game | Player trả lời câu hỏi trong game | High |
| UC-P10 | Xem kết quả game | Player xem điểm số và xếp hạng sau game | High |
| UC-P11 | Xem lịch sử game | Player xem các game đã chơi | Medium |
| UC-P12 | Xem thống kê cá nhân | Player xem tổng điểm, độ chính xác, achievements | Medium |
| UC-P13 | Xem bảng xếp hạng | Player xem top players theo topic | Medium |
| UC-P14 | Phản hồi lời mời | Player chấp nhận/từ chối lời mời vào phòng | Medium |
| UC-P15 | Đăng xuất | Player đăng xuất khỏi hệ thống | High |

---

#### **HOST (Chủ phòng)**

| Use Case ID | Tên Use Case | Mô tả | Priority |
|-------------|--------------|-------|----------|
| UC-H01 | Tạo phòng | Host tạo phòng mới với cấu hình | High |
| UC-H02 | Cập nhật phòng | Host chỉnh sửa thông tin phòng | Medium |
| UC-H03 | Xóa phòng | Host xóa phòng chưa có game history | Medium |
| UC-H04 | Mời người chơi | Host gửi lời mời vào phòng | Medium |
| UC-H05 | Kick người chơi | Host đá người chơi khỏi phòng | High |
| UC-H06 | Chuyển quyền host | Host chuyển quyền quản lý cho người khác | Low |
| UC-H07 | Bắt đầu game | Host khởi động game session | High |
| UC-H08 | Chuyển câu hỏi | Host chuyển sang câu hỏi tiếp theo | High |
| UC-H09 | Kết thúc game | Host kết thúc game sớm | Medium |
| UC-H10 | Tạo câu hỏi | Host tạo câu hỏi mới cho topic | High |
| UC-H11 | Sửa câu hỏi | Host chỉnh sửa câu hỏi hiện có | Medium |
| UC-H12 | Xóa câu hỏi | Host xóa câu hỏi | Medium |
| UC-H13 | Tạo câu hỏi bằng AI | Host nhập mô tả để AI tạo câu hỏi | High |
| UC-H14 | Quản lý đáp án | Host tạo/sửa/xóa đáp án cho câu hỏi | High |

---

#### **ADMIN (Quản trị viên)**

| Use Case ID | Tên Use Case | Mô tả | Priority |
|-------------|--------------|-------|----------|
| UC-A01 | Quản lý người dùng | Admin xem, sửa, xóa (soft delete) user | High |
| UC-A02 | Quản lý vai trò | Admin tạo, sửa, xóa role | High |
| UC-A03 | Quản lý quyền hạn | Admin tạo, sửa, xóa permission | High |
| UC-A04 | Gán quyền cho role | Admin assign/remove permissions to role | High |
| UC-A05 | Gán role cho user | Admin assign/remove roles to user | High |
| UC-A06 | Quản lý topic | Admin tạo, sửa, xóa chủ đề | High |
| UC-A07 | Quản lý câu hỏi | Admin tạo, sửa, xóa câu hỏi hàng loạt | High |
| UC-A08 | Xem thống kê hệ thống | Admin xem tổng quan hoạt động hệ thống | Medium |
| UC-A09 | Quản lý phòng | Admin xem và xóa phòng vi phạm | Medium |
| UC-A10 | Xem logs | Admin xem log hoạt động hệ thống | Low |

---

#### **AI SYSTEM (Hệ thống AI)**

| Use Case ID | Tên Use Case | Mô tả | Priority |
|-------------|--------------|-------|----------|
| UC-AI01 | Parse yêu cầu tự nhiên | AI phân tích mô tả của giáo viên | High |
| UC-AI02 | Tạo câu hỏi | AI tạo câu hỏi dựa trên topic và yêu cầu | High |
| UC-AI03 | Tạo đáp án | AI tạo 4 đáp án (1 đúng, 3 sai) | High |
| UC-AI04 | Validate câu hỏi | AI kiểm tra chất lượng câu hỏi trước khi trả về | Medium |

---

#### **EMAIL SYSTEM (Hệ thống Email)**

| Use Case ID | Tên Use Case | Mô tả | Priority |
|-------------|--------------|-------|----------|
| UC-E01 | Gửi email xác thực | System gửi email verification sau đăng ký | High |
| UC-E02 | Gửi email reset password | System gửi mật khẩu mới qua email | High |
| UC-E03 | Gửi email thông báo | System gửi email thông báo sự kiện quan trọng | Low |

---

#### **SOCKET.IO SERVER (Real-time Server)**

| Use Case ID | Tên Use Case | Mô tả | Priority |
|-------------|--------------|-------|----------|
| UC-S01 | Xác thực WebSocket | Server validate JWT khi client connect | High |
| UC-S02 | Quản lý room namespace | Server quản lý các room WebSocket | High |
| UC-S03 | Broadcast player joined | Server thông báo player mới vào phòng | High |
| UC-S04 | Broadcast player left | Server thông báo player rời phòng | High |
| UC-S05 | Broadcast game started | Server thông báo game bắt đầu | High |
| UC-S06 | Broadcast next question | Server gửi câu hỏi tiếp theo | High |
| UC-S07 | Broadcast countdown | Server gửi countdown tick mỗi giây | High |
| UC-S08 | Broadcast player answered | Server thông báo player đã trả lời | Medium |
| UC-S09 | Broadcast leaderboard | Server cập nhật bảng xếp hạng real-time | High |
| UC-S10 | Broadcast game finished | Server thông báo game kết thúc | High |
| UC-S11 | Broadcast room list | Server cập nhật danh sách phòng | Medium |
| UC-S12 | Broadcast host changed | Server thông báo host mới | Medium |

---

## 4. USE CASE DIAGRAM (Mô tả văn bản)

```
┌─────────────────────────────────────────────────────────────┐
│                    QUIZIZZ SYSTEM                            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  [Guest]                                                     │
│    ├── Đăng ký                                              │
│    ├── Đăng nhập                                            │
│    ├── Quên mật khẩu                                        │
│    └── Xem profile công khai                               │
│                                                              │
│  [Player] (extends Guest)                                    │
│    ├── Quản lý profile                                      │
│    ├── Tìm & Join phòng                                     │
│    ├── Chơi game                                            │
│    ├── Xem thống kê & achievements                          │
│    └── Đăng xuất                                            │
│                                                              │
│  [Host] (extends Player)                                     │
│    ├── Tạo & quản lý phòng                                  │
│    ├── Tạo câu hỏi (manual & AI)                           │
│    ├── Bắt đầu & điều khiển game                           │
│    └── Kick player                                          │
│                                                              │
│  [Admin] (extends Host)                                      │
│    ├── Quản lý user, role, permission                       │
│    ├── Quản lý topic & question                             │
│    └── Xem thống kê hệ thống                               │
│                                                              │
│  [AI System]                                                 │
│    └── Tạo câu hỏi từ mô tả tự nhiên                       │
│                                                              │
│  [Email System]                                              │
│    └── Gửi email (verification, reset password)            │
│                                                              │
│  [Socket.IO Server]                                          │
│    └── Broadcast real-time events                           │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. TỔNG KẾT

### 5.1 Thống kê Use Case
- **Guest**: 5 use cases
- **Player**: 15 use cases
- **Host**: 14 use cases
- **Admin**: 10 use cases
- **AI System**: 4 use cases
- **Email System**: 3 use cases
- **Socket.IO Server**: 12 use cases

**Tổng cộng**: 63 use cases

### 5.2 Độ ưu tiên
- **High Priority**: 45 use cases (71%)
- **Medium Priority**: 16 use cases (25%)
- **Low Priority**: 2 use cases (4%)

### 5.3 Công nghệ chính
- **Backend**: Spring Boot 3.5.6, Java 21
- **Database**: PostgreSQL 18.0
- **Cache**: Redis 6+
- **Real-time**: Socket.IO (Netty)
- **Security**: JWT, BCrypt, Spring Security
- **AI**: Spring AI (Gemini Flash)
- **Storage**: MinIO
- **Documentation**: Swagger/OpenAPI 3.0
