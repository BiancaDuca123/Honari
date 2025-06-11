# 📚 Honari — Scan, discover, and track your books

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-2.3-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-2026.04-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Min%20SDK-26-brightgreen?style=for-the-badge" />
</div>

---

## About

**Honari** is a book tracking app for Android. Its core feature is the **AI book scanner** — point your camera at any book barcode and the app instantly identifies the book via the Google Books API, just like Vivino does for wine. Identified books can be saved to your personal library and tracked by reading status.

---

## Features

### 📷 Book Scanner
- Point the camera at any ISBN barcode — the app detects it in real time using ML Kit
- Book metadata (title, author, cover, description, pages, publisher) is fetched instantly from the Google Books API
- One-tap save to your personal library with a reading status

### 🔍 Discover
- Browse popular books and new releases sourced from the Google Books API
- Full-text search by title, author, or ISBN

### 📚 My Library
- Personal book collection stored per-user in Firestore
- Filter by status: **Reading**, **Finished**, **Want to Read**
- Tap any book to see full details

### 👤 Profile
- Displays your name, email, and a live count of your library stats
- Sign out

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.3 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture (data / domain / presentation) |
| DI | Dagger Hilt |
| Navigation | Jetpack Navigation Compose |
| Book data | Google Books REST API (Retrofit + OkHttp) |
| Auth | Firebase Authentication |
| Library storage | Firebase Firestore |
| Image loading | Coil |
| Camera | CameraX |
| Barcode scanning | ML Kit Barcode Scanning |
| Code style | Spotless + ktlint |

---

## Project Structure

```
com.honari.app/
├── data/
│   ├── remote/               # Retrofit service + Google Books DTOs
│   └── repository/           # AuthRepositoryImpl, BookLookupRepositoryImpl, LibraryRepositoryImpl
├── domain/
│   ├── model/                # Book, User, ReadingStatus
│   ├── repository/           # AuthRepository, BookLookupRepository, LibraryRepository
│   └── usecase/              # AddBookToLibraryUseCase
├── presentation/
│   ├── screens/
│   │   ├── auth/             # Login, Register, Password reset
│   │   ├── discover/         # Discover screen + ViewModel
│   │   ├── library/          # Library screen + ViewModel
│   │   ├── scan/             # Scanner screen + ViewModel + BarcodeAnalyzer
│   │   ├── book/             # Book detail screen + ViewModel
│   │   └── profile/          # Profile screen + ViewModel
│   ├── navigation/           # NavHost, BottomNavigationBar, Screen routes
│   └── theme/                # Color, Type, Shape, Theme
└── di/                       # AppModule (Hilt)
```

---

## Getting Started

### Requirements
- Android Studio Meerkat or newer
- JDK 17+
- Min SDK 26 / Target SDK 36
- A Firebase project with **Authentication** and **Firestore** enabled

### Setup

1. **Clone the repo**
```bash
git clone https://github.com/BiancaDuca123/Honari.git
cd Honari
```

2. **Add Firebase config**
   - Create a project in the [Firebase Console](https://console.firebase.google.com)
   - Enable Email/Password Authentication and Firestore
   - Download `google-services.json` and place it in `app/`

3. **Build & run**
   - Open in Android Studio, sync Gradle, run on an emulator or device

### Code formatting

```bash
# Check for violations
./gradlew spotlessCheck

# Auto-fix
./gradlew spotlessApply
```

---

## Author

**Bianca Duca** — [@BiancaDuca123](https://github.com/BiancaDuca123)
