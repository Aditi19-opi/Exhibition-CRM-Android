# Exhibition CRM Android Application

A modern, native Android application built with **Kotlin** and **Jetpack Compose** designed for exhibition management and customer relationship tracking (CRM). The app communicates with a **Node.js/Express** backend via REST APIs backed by a **PostgreSQL** database.

---

## Project Overview

Exhibition CRM empowers event organizers, sales representatives, and exhibition staff to manage trade shows, expos, and corporate events on the go. Users can seamlessly track customer leads, monitor exhibition timelines, analyze visitor statistics through interactive charts, and keep all customer interactions organized by status (New, Follow Up, Confirmed).

---

## Key Features

- **User Authentication:** Secure Login and Registration screens with persistent local session management.
- **Interactive Dashboard:**
  - Real-time metric summary cards (**Total Customers**, **Today's Visitors**, and **Active Exhibitions**).
  - **Exhibition Analytics Donut Chart:** Visual breakdown of customer distribution across different exhibitions.
  - **Recent Customers Feed:** Quick access to recently added leads with status badges and exhibition tags.
- **Exhibition Management:**
  - Browse exhibitions with search and status filtering (All, Upcoming, Ongoing, Completed).
  - Add new exhibitions with name, venue, start/end dates, status, and optional notes/description.
  - View detailed exhibition breakdowns and associated customer lists.
- **Customer Relationship Management (CRM):**
  - Add new customers with contact details (Name, Phone, Email), exhibition tagging, and status selection.
  - Filter customers by exhibition and status filters (`New`, `Follow Up`, `Confirmed`).
  - Edit customer details or remove records with clean confirmation dialogs.
- **Modern Material 3 UI:** Built entirely with Jetpack Compose following modern design principles, rounded cards, custom iconography, and responsive layouts.

---

## Technology Stack

- **Language:** Kotlin (`2.0.21`)
- **UI Framework:** Jetpack Compose, Material 3 (`androidx.compose`)
- **Concurrency & Asynchrony:** Kotlin Coroutines & `withContext(Dispatchers.IO)`
- **Networking:** Native `HttpURLConnection` with JSON parsing (`org.json`)
- **Backend Integration:** Node.js, Express.js REST API (`http://10.0.2.2:3000/api/`)
- **Database:** PostgreSQL

---

## Project Structure

```text
com.example.kotlinpractice/
│
├── MainActivity.kt                # App entry point
├── network/
│   ├── CustomerApi.kt             # REST API client for customer operations
│   ├── ExhibitionApi.kt           # REST API client for exhibition operations
│   └── ExhibitionStats.kt         # Data class for dashboard analytics stats
│
└── ui/
    ├── AuthScreen.kt              # Main navigation router & auth coordinator
    ├── Login.kt                   # User login screen
    ├── Register.kt                # User registration screen
    ├── MainScreen.kt              # Navigation drawer & bottom navigation host
    ├── Dashboard.kt               # Home dashboard, metrics cards & donut chart analytics
    ├── Exhibition.kt              # Exhibition list, filters, details dialog & editing
    ├── AddExhibition.kt           # Add exhibition form screen
    ├── Customers.kt               # Customer directory, search, status filters & editing
    ├── AddCustomer.kt             # Add customer form screen
    └── theme/                     # Compose theme, colors, and typography
```

---

## Backend Configuration & API Connection

The application connects to a local Node.js/Express backend server. 

- **Base URL (Android Emulator):** `http://10.0.2.2:3000/api/`
- **Exhibitions Endpoint:** `http://10.0.2.2:3000/api/exhibitions`
- **Customers Endpoint:** `http://10.0.2.2:3000/api/customers`

### API Endpoints Summary:
- `GET /api/exhibitions` - Fetch all exhibitions
- `POST /api/exhibitions` - Create a new exhibition
- `PUT /api/exhibitions/{id}` - Update an exhibition
- `GET /api/customers` - Fetch all customers
- `POST /api/customers` - Add a new customer
- `PUT /api/customers/{id}` - Update customer details
- `DELETE /api/customers/{id}` - Delete a customer record

---

## Setup and Run Instructions

### Prerequisites
- **Android Studio** (Koala, Jellyfish, or newer)
- **Android SDK** (Compile SDK 35, Min SDK 24)
- **Node.js Backend Server** running locally on port `3000` connected to PostgreSQL.

### Running the Application
1. **Clone the Repository:**
   ```bash
   git clone <repository-url>
   cd androidapp
   ```
2. **Open in Android Studio:**
   - Launch Android Studio, select **Open**, and choose the `androidapp` folder.
3. **Configure Backend:**
   - Ensure your Node.js/Express backend server is running on `http://localhost:3000`.
4. **Sync Project with Gradle Files:**
   - Allow Android Studio to download dependencies and sync Gradle.
5. **Run on Device or Emulator:**
   - Select an Android Virtual Device (API 24 or higher) or a connected physical device and click **Run (`Shift + F10`)**.

---

## Current Limitations & Future Improvements

### Current Limitations
- **Local Development URL:** Configured for Android emulator loopback (`10.0.2.2`). Physical devices require updating the backend base URL to your local machine's IP address.
- **Offline Caching:** Primarily relies on live REST API requests with basic in-memory state fallbacks.

### Future Improvements
- **Local Database (Room):** Implement Room database for robust offline-first data persistence and local caching.
- **Authentication Tokens:** Integrate JWT (JSON Web Tokens) or OAuth2 authentication headers in API requests.
- **Push Notifications:** Add reminders for upcoming exhibitions and pending customer follow-ups.
- **Advanced Export:** Export customer lead reports to CSV or PDF directly from the app.
