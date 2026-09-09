# SplitWallet REST API Testing Examples & Documentation

Complete collection of cURL requests for testing all **SplitWallet REST API** endpoints.

---

## 🔑 1. Authentication APIs

### 1.1 Register User A (Organizer: Mani)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Mani",
    "email": "mani@splitwallet.com",
    "mobile": "+919876543210",
    "password": "password123"
  }'
```
**Expected Response (HTTP 201 Created)**:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "fullName": "Mani",
    "email": "mani@splitwallet.com",
    "mobile": "+919876543210"
  }
}
```

### 1.2 Register User B (Participant: Rahul)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Rahul Sharma",
    "email": "rahul@example.com",
    "mobile": "+919812345678",
    "password": "password123"
  }'
```

### 1.3 Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "mani@splitwallet.com",
    "password": "password123"
  }'
```

---

## 👤 2. User Profile APIs

### 2.1 Get Current User Profile
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <USER_A_JWT_TOKEN>"
```

### 2.2 Update Profile
```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <USER_A_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Mani Kumar",
    "mobile": "+919876543210"
  }'
```

---

## 💳 3. Wallet APIs

### 3.1 Get Wallet Balance
```bash
curl -X GET http://localhost:8080/api/wallet \
  -H "Authorization: Bearer <USER_B_JWT_TOKEN>"
```
**Response**:
```json
{
  "success": true,
  "message": "Wallet details retrieved successfully",
  "data": {
    "id": 2,
    "userId": 2,
    "balance": 0.00,
    "updatedAt": "2026-09-07T23:30:00"
  }
}
```

### 3.2 Top Up Wallet Balance
```bash
curl -X POST http://localhost:8080/api/wallet/topup \
  -H "Authorization: Bearer <USER_B_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00,
    "paymentMethod": "UPI / Credit Card"
  }'
```
**Response**:
```json
{
  "success": true,
  "message": "Wallet topped up successfully",
  "data": {
    "id": 2,
    "userId": 2,
    "balance": 1000.00,
    "updatedAt": "2026-09-07T23:31:00"
  }
}
```

---

## 🍕 4. Expense Split APIs

### 4.1 Create Expense Split (Mani splits ₹900 with Rahul & Arun)
- Total: ₹900
- Mani (User 1): ₹300
- Rahul (User 2): ₹300
- Arun (User 3): ₹300
- **Validation Rule**: $\sum (300+300+300) = 900$.

```bash
curl -X POST http://localhost:8080/api/splits \
  -H "Authorization: Bearer <USER_A_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Dinner at Nobu",
    "totalAmount": 900.00,
    "description": "Group dinner party",
    "participants": [
      { "userId": 1, "amount": 300.00 },
      { "userId": 2, "amount": 300.00 },
      { "userId": 3, "amount": 300.00 }
    ]
  }'
```

### 4.2 Get My Created Splits
```bash
curl -X GET http://localhost:8080/api/splits/my \
  -H "Authorization: Bearer <USER_A_JWT_TOKEN>"
```

### 4.3 Get Split Details by ID
```bash
curl -X GET http://localhost:8080/api/splits/1 \
  -H "Authorization: Bearer <USER_A_JWT_TOKEN>"
```

---

## 📩 5. Split Requests APIs

### 5.1 View Incoming Requests (Rahul checks requests)
```bash
curl -X GET http://localhost:8080/api/split-requests \
  -H "Authorization: Bearer <USER_B_JWT_TOKEN>"
```

### 5.2 Approve & Debit Request (Rahul approves ₹300 for Dinner)
```bash
curl -X PUT http://localhost:8080/api/split-requests/2/approve \
  -H "Authorization: Bearer <USER_B_JWT_TOKEN>"
```
**Execution Behavior**:
1. Checks Rahul's wallet balance (₹1000).
2. Debits ₹300 from Rahul's wallet (New balance: ₹700).
3. Creates `SPLIT_DEBIT` transaction for Rahul.
4. Credits ₹300 to Mani's wallet (New balance: ₹300).
5. Creates `SPLIT_CREDIT` transaction for Mani.
6. Updates participant status to `DEBITED`.
7. Sends approval notification to Mani and debit notification to Rahul.

### 5.3 Reject Request
```bash
curl -X PUT http://localhost:8080/api/split-requests/3/reject \
  -H "Authorization: Bearer <USER_C_JWT_TOKEN>"
```

---

## 📊 6. Transactions & Notifications APIs

### 6.1 Get Transaction History
```bash
curl -X GET http://localhost:8080/api/transactions \
  -H "Authorization: Bearer <USER_B_JWT_TOKEN>"
```

### 6.2 Get Notifications
```bash
curl -X GET http://localhost:8080/api/notifications \
  -H "Authorization: Bearer <USER_A_JWT_TOKEN>"
```

### 6.3 Mark Notification as Read
```bash
curl -X PUT http://localhost:8080/api/notifications/1/read \
  -H "Authorization: Bearer <USER_A_JWT_TOKEN>"
```
