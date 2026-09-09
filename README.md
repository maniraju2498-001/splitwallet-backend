# SplitWallet — Full-Stack Group Expense Splitting Platform

> A production-grade virtual wallet ledger and group expense splitting application built with React + Spring Boot + MySQL.

---

## 🚀 Features

- **JWT Authentication** — Secure register/login with BCrypt password hashing
- **Virtual Wallet** — Per-user wallet ledger with full transaction history
- **Top-Up System** — Simulated top-up prototype (ready for real payment gateway integration)
- **Group Expense Splits** — Create splits with any registered users
- **Equal & Custom Split** — Auto-calculate equal shares or set custom amounts
- **Approval Workflow** — Participants approve or reject their share
- **Wallet Debit** — Approved splits debit the participant's wallet atomically
- **Insufficient Balance Guard** — Prevents negative wallet balances
- **Duplicate Debit Protection** — A participant cannot be charged twice
- **Creator Share** — Split creator's own share is recorded correctly
- **Real-time Notifications** — Alerts for split requests, approvals, rejections, top-ups
- **Transaction History** — Full ledger of TOP_UP, SPLIT_DEBIT, SPLIT_CREDIT
- **User Search** — Find registered users by name or email
- **Admin Dashboard** — System-wide stats (protected by ADMIN role)
- **Responsive Design** — Dark fintech UI works on desktop, tablet, mobile

---

## 🛠 Technology Stack

### Frontend
| Technology | Purpose |
|---|---|
| React 19 | UI Framework |
| Vite 8 | Build tool & dev server |
| React Router v7 | Client-side routing |
| Axios | HTTP client |
| Lucide React | Icon library |
| CSS3 | Styling (dark fintech theme) |

### Backend
| Technology | Purpose |
|---|---|
| Java 26 | Language |
| Spring Boot 3.3 | Application framework |
| Spring Security | Authentication & authorization |
| Spring Data JPA | ORM |
| Hibernate | JPA implementation |
| JJWT 0.12.5 | JWT token generation/validation |
| BCrypt | Password hashing |
| Bean Validation | Request validation |
| Maven | Build tool |

### Database
| Technology | Purpose |
|---|---|
| MySQL 8+ | Primary database |
| InnoDB | Storage engine with foreign key support |

---

## 🏗 Architecture

```
React Frontend (port 5173)
         │
         │ HTTP REST (Axios + JWT Bearer)
         ▼
Spring Boot Backend (port 8080)
         │
         │ JPA/Hibernate
         ▼
MySQL Database (splitwallet_db)
```

---

## 📋 Prerequisites

- **Java 17+** (tested with Java 26)
- **Maven 3.8+**
- **Node.js 18+** with npm
- **MySQL 8.0+**

---

## 🗄 Database Setup

### 1. Start MySQL and create database
```sql
mysql -u root -p
CREATE DATABASE IF NOT EXISTS splitwallet_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE splitwallet_db;
```

### 2. Run the schema (creates all tables)
```bash
mysql -u root -p splitwallet_db < src/main/resources/schema.sql
```

### 3. Seed test data (optional but recommended)
```bash
mysql -u root -p splitwallet_db < src/main/resources/data.sql
```

This seeds 5 test users with pre-loaded wallets and sample splits.

---

## ⚙️ Environment Variables

### Backend (application.yml defaults — override with env vars)

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/splitwallet_db?createDatabaseIfNotExist=true...` | MySQL connection URL |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | `404E635266...` | JWT signing secret (change in production!) |

### Frontend (.env)
```
VITE_API_BASE_URL=http://localhost:8080
```

---

## ▶️ How to Run

### Step 1: Start MySQL
Make sure MySQL is running on port 3306.

### Step 2: Start Backend
```bash
cd split-wallet-backend

# With default settings (root/root, splitwallet_db)
mvn spring-boot:run

# OR with custom DB credentials
DB_USERNAME=myuser DB_PASSWORD=mypass mvn spring-boot:run
```

The backend starts on: **http://localhost:8080**

### Step 3: Start Frontend
```bash
cd split-wallet
npm install
npm run dev
```

The frontend starts on: **http://localhost:5173**

---

## 👥 Test User Credentials

All test users use password: **`password123`**

| User | Email | Starting Balance |
|---|---|---|
| Mani Kumar | mani@splitwallet.com | ₹1,800 |
| Rahul Sharma | rahul@example.com | ₹400 |
| Arun Kumar | arun@example.com | ₹1,000 |
| Priya Patel | priya@example.com | ₹2,500 |
| Sneha Reddy | sneha@example.com | ₹750 |

---

## 🔌 API Endpoints

### Authentication
```
POST /api/auth/register    — Register new user (creates wallet automatically)
POST /api/auth/login       — Login, returns JWT token
```

### User
```
GET  /api/users/me              — Get current user profile
PUT  /api/users/me              — Update profile (name, mobile)
PUT  /api/users/me/change-password — Change password
GET  /api/users                 — List all users
GET  /api/users/search?q=query  — Search users by name or email
```

### Wallet
```
GET  /api/wallet            — Get current wallet balance
POST /api/wallet/topup      — Top up wallet (virtual/demo)
GET  /api/wallet/transactions — Get wallet transaction history
GET  /api/wallet/dashboard  — Get dashboard metrics
```

### Splits
```
POST /api/splits            — Create expense split
GET  /api/splits/my         — List splits created by me
GET  /api/splits/{id}       — Get single split with participants
```

### Split Requests (incoming requests for current user)
```
GET  /api/split-requests            — List my incoming requests
PUT  /api/split-requests/{id}/approve — Approve & debit wallet
PUT  /api/split-requests/{id}/reject  — Reject (no debit)
```

### Transactions
```
GET  /api/transactions          — Get all my transactions
```

### Notifications
```
GET  /api/notifications         — Get all my notifications
PUT  /api/notifications/{id}/read — Mark notification as read
PUT  /api/notifications/read-all  — Mark all as read
```

### Admin (requires ROLE_ADMIN)
```
GET  /api/admin/stats           — System-wide statistics
```

---

## 📦 API Response Format

All responses use consistent format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

Error responses:
```json
{
  "success": false,
  "message": "Insufficient wallet balance"
}
```

---

## 🔐 Security

- JWT tokens expire in 24 hours
- All endpoints except `/api/auth/**` require a valid JWT
- Users can only access their own wallet, profile, and transactions
- Users cannot approve requests belonging to another user
- Admin endpoints protected by `ROLE_ADMIN`
- BCrypt with strength 10 for password hashing
- CORS configured to allow all origins (restrict in production)

---

## 💡 Business Rules

1. **Sum validation**: Sum of participant amounts must equal total expense
2. **No debit on creation**: Wallets are only debited when participant APPROVES
3. **Insufficient balance**: Returns 400 error, balance stays unchanged
4. **Duplicate protection**: DEBITED status cannot be approved again
5. **Creator share**: Creator's own share is marked DEBITED immediately on split creation
6. **Atomic transactions**: Wallet debit + transaction record happen in the same DB transaction
7. **Notifications**: All key events (request, approval, rejection, top-up) generate notifications

---

## 🧪 End-to-End Test Scenario

```
1. Register: mani@splitwallet.com / password123
2. Register: rahul@example.com / password123
3. Register: arun@example.com / password123
4. Top up Mani wallet: +₹3,600
5. Top up Rahul wallet: +₹1,000
6. Top up Arun wallet: +₹1,000

7. Login as Mani → Create Split:
   Title: "Dinner"
   Total: ₹900
   Rahul: ₹300
   Arun: ₹300
   Mani: ₹300 (creator share — auto DEBITED)

8. Login as Rahul → Requests tab → Approve ₹300
   Rahul balance: ₹1,000 → ₹700
   Mani receives SPLIT_CREDIT notification

9. Login as Arun → Requests tab → Approve ₹300
   Arun balance: ₹1,000 → ₹700

10. Login as Mani → My Splits → View:
    Rahul → DEBITED
    Arun → DEBITED
    Mani → DEBITED

11. Check Transactions → See SPLIT_DEBIT entries
12. Attempt duplicate approval → Blocked with error
13. Test insufficient balance → Top up ₹10, try to approve ₹300 → Rejected
```

---

## 📁 Project Structure

```
splitwallet/
├── split-wallet/              # React Frontend
│   ├── src/
│   │   ├── context/
│   │   │   └── AuthContext.jsx        # JWT auth state
│   │   ├── components/
│   │   │   ├── common/               # Navbar, Sidebar, Toast, Modal
│   │   │   ├── dashboard/            # WalletCard
│   │   │   ├── splits/               # SplitCard, UserSelector
│   │   │   └── transactions/         # TransactionCard
│   │   ├── pages/
│   │   │   ├── LandingPage.jsx
│   │   │   ├── LoginPage.jsx
│   │   │   ├── RegisterPage.jsx
│   │   │   ├── DashboardPage.jsx
│   │   │   ├── WalletPage.jsx
│   │   │   ├── CreateSplitPage.jsx
│   │   │   ├── MySplitsPage.jsx
│   │   │   ├── SplitRequestsPage.jsx
│   │   │   ├── TransactionsPage.jsx
│   │   │   ├── NotificationsPage.jsx
│   │   │   └── ProfilePage.jsx
│   │   └── services/
│   │       └── api.js                # Axios API client
│   ├── .env
│   └── package.json
│
└── split-wallet-backend/      # Spring Boot Backend
    ├── src/main/java/com/splitwallet/
    │   ├── controller/        # REST controllers
    │   ├── service/           # Business logic
    │   ├── repository/        # JPA repositories
    │   ├── entity/            # JPA entities
    │   ├── dto/               # Request/Response DTOs
    │   ├── security/          # JWT filter, UserDetailsService
    │   ├── exception/         # Custom exceptions + GlobalExceptionHandler
    │   ├── config/            # SecurityConfig, ApplicationConfig
    │   └── util/              # SecurityUtils
    ├── src/main/resources/
    │   ├── application.yml    # App configuration
    │   ├── schema.sql         # Database DDL
    │   └── data.sql           # Seed data
    └── pom.xml
```

---

## 🖼 Screenshots

*(Screenshots will be available after running the application locally)*

---

## ⚠️ Important Notes

- **Top-up is a virtual/demo ledger** — No real money is moved. This prototype is ready for integration with a legitimate payment provider (Razorpay, Stripe, etc.)
- **JWT secret must be changed** in production via `JWT_SECRET` environment variable
- **CORS** is set to allow all origins in development — restrict to your domain in production
- **data.sql must be run manually** — The app uses `ddl-auto: update` for schema, not the init scripts
