# FormUp

FormUp is an Android application designed to help football coaches manage their teams, players, fixtures, attendance, lineups, match statistics and team information from a single mobile application.

The application uses Jetpack Compose for the user interface, Firebase Authentication for user authentication, and a .NET Web API for retrieving and updating team data.
API Repo Link: https://github.com/ConnorST10435598/FormUp_API.git 
# Features

## Authentication

* Coach registration and login
* Firebase email/password authentication
* Password reset and sign out
* Firebase authentication state tracking
* Firebase ID token authentication
* Coach-only application access
* Google sign-in UI

## Team Management

* View, add and delete players
* Player positions and availability
* Filter players by availability
* Squad information

Availability statuses:

* Fit
* Doubtful
* Out

## Fixtures & Calendar

* Monthly calendar
* Upcoming and previous fixtures
* Match dates, times, opponents and venues
* Match details
* Lineup selection
* Attendance management

## Match Management

* Select match squads and starting lineups
* Record player attendance
* Record goals, assists, clean sheets, minutes and ratings
* View match reports and performance information
* Update match results

## Dashboard & Notifications

* Team availability
* Upcoming fixtures
* Previous matches
* Quick actions
* Team updates
* Notifications
* Invite players

## Coach Profile

* View and edit coach information
* Team information
* Preferred language
* Account activity
* Sign out

Supported languages:

* English
* Afrikaans
* isiXhosa

# Technology Stack

### Android

* Kotlin
* Jetpack Compose
* Material 3
* AndroidX
* ViewModel
* Kotlin Coroutines
* Java 17

### Authentication

* Firebase Authentication
* Firebase ID Tokens
* JWT Bearer Authentication

### Backend

* .NET Web API
* REST
* HTTP/JSON

Current API:

`http://prog7314.runasp.net`

# Application Architecture

```text
Compose UI
    │
    ▼
FormUpViewModel
    │
    ▼
FormUpApi
    │
    ▼
FormUp .NET Web API
```

Firebase Authentication works alongside the API:

```text
Coach
  │
  ▼
Firebase Authentication
  │
  ▼
Firebase ID Token (JWT)
  │
  ▼
Authorization: Bearer <token>
  │
  ▼
FormUp API
```

`FormUpViewModel` manages application state and coordinates the UI with the API. `FormUpApi` handles HTTP requests, authentication headers, JSON data and API errors.

# JWT Authentication

FormUp uses Firebase Authentication to authenticate coaches and Firebase ID tokens as JWT bearer tokens for API requests.

After authentication, the application retrieves the Firebase ID token:

```kotlin
val token = Tasks
    .await(user.getIdToken(false))
    .token
```

The token is then sent with protected API requests:

```http
Authorization: Bearer <firebase-id-token>
```

The authentication process is:

```text
Coach Login
    ↓
Firebase Authentication
    ↓
Firebase ID Token (JWT)
    ↓
Android Application
    ↓
Authorization: Bearer JWT
    ↓
FormUp API
```

The application does not generate or hard-code its own JWT.

# API Integration

The `FormUpApi` class communicates with the backend.

### GET

```text
GET /api/coach
GET /api/team
GET /api/season-data
GET /api/match/{matchId}/attendance
```

### POST

```text
POST /api/players
```

### PUT

```text
PUT /api/team
PUT /api/match/{matchId}/squad
PUT /api/match/{matchId}/attendance
PUT /api/match/{matchId}/stats
```

### PATCH

```text
PATCH /api/coach
PATCH /api/match/{matchId}
```

### DELETE

```text
DELETE /api/players/{id}
```

These endpoints are used for coach profiles, teams, players, fixtures, squads, attendance, match results and statistics.

# Error Handling

API errors are handled using the `ApiException` class, which stores the HTTP status code and error message.

Unsuccessful requests are handled without crashing the application. For example:

```text
401 - You are not signed in.
API request failed (500)
```

# Project Structure

```text
FormUp/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/formup/app/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── FormUpApi.kt
│       │   │   └── FormUpDomain.kt
│       │   └── ui/
│       │       ├── FormUpViewModel.kt
│       │       ├── CreateTeamUiState.kt
│       │       ├── auth/
│       │       ├── calendar/
│       │       ├── home/
│       │       ├── invite/
│       │       ├── navigation/
│       │       ├── notifications/
│       │       ├── profile/
│       │       ├── stats/
│       │       ├── team/
│       │       └── theme/
│       └── res/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── gradle/
```

# Main Components

### `MainActivity.kt`

Application entry point. Sets up Compose, the application theme, Firebase authentication state and navigation.

### `FormUpViewModel.kt`

Manages UI state and application logic, including players, fixtures, attendance, squads, statistics, notifications and API operations.

### `FormUpApi.kt`

Handles communication with the .NET API, including HTTP requests, JSON data, bearer authentication and errors.

### `FormUpDomain.kt`

Contains the application's main data models and enums, including players, fixtures, teams, coaches, notifications and match data.

# UI

FormUp uses **Jetpack Compose rather than XML layouts**.

```text
ui/
├── auth/
├── home/
├── calendar/
├── team/
├── stats/
├── profile/
├── notifications/
├── invite/
├── navigation/
└── theme/
```

# Configuration

The project uses:

```text
compileSdk = 34
targetSdk = 34
minSdk = 24
Java = 17
JVM Target = 17
```

Main dependencies include:

* AndroidX
* Jetpack Compose
* Material 3
* Firebase Authentication
* Firebase BOM
* Lifecycle ViewModel
* Kotlin 2.0.20
* Android Gradle Plugin 8.5.2

# Firebase Setup

Firebase is configured using:

```text
app/google-services.json
```

Firebase Authentication provides:

* Registration
* Login
* Password reset
* Authentication state
* Firebase ID tokens
* Sign out

A correctly configured Firebase project is required for authentication.

# Running the Application

### 1. Clone the repository

```bash
git clone <repository-url>
```

Open the project in Android Studio.

### 2. Configure Firebase

Add:

```text
app/google-services.json
```

and enable Firebase Authentication.

### 3. Configure the API

The API URL is defined in `FormUpApi.kt`:

```kotlin
const val BASE_URL = "http://prog7314.runasp.net"
```

Change it if the backend address changes.

### 4. Run

Allow Gradle to sync, then run the application on an Android emulator or physical device with network access.

# Security

FormUp uses Firebase Authentication and JWT bearer authentication for protected API requests.

Authentication tokens should never be hard-coded or committed to Git.

The current API uses HTTP for development:

```text
http://prog7314.runasp.net
```

A production deployment should use HTTPS.

# Current Development Status

The application currently includes:

* Firebase authentication
* Jetpack Compose UI
* JWT bearer authentication
* REST API integration
* Team and player management
* Fixtures and calendar
* Lineups and attendance
* Match statistics and reports
* Notifications
* Coach and team profiles
* API error handling

# Future Improvements

* HTTPS API deployment
* Retrofit/OkHttp
* Offline caching
* Automated testing
* Google authentication
* Improved token handling
* Stronger API validation and error handling
* CI/CD
* Production logging and monitoring

# License

Developed as part of the FormUp educational project.
