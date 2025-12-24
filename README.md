# Nihongo Flashcard App
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/trannhattan5/nihongo-flashcard-app)

This is a native Android application designed to help users learn Japanese vocabulary through a structured, flashcard-based system. The app organizes content by JLPT levels and lessons, tracks user progress, and provides review functionality, all powered by Firebase.

## Features

-   **User Authentication**: Secure sign-up, login, and password reset functionality using Firebase Authentication.
-   **Structured Learning Path**: Vocabulary is categorized into JLPT levels (e.g., N5) and individual lessons, allowing for progressive learning.
-   **Interactive Flashcards**: An intuitive flashcard interface with a flip animation to reveal the meaning. Users can mark cards as "Remembered" or "Not Remembered".
-   **Progress Tracking**: The app tracks user progress for each lesson, showing counts for remembered, not-remembered, and remaining cards.
-   **Personalized Review**: Users can start a review session filtered to focus specifically on cards they previously marked as "Remembered" or "Not Remembered".
-   **User Profile & Statistics**: A profile screen displays the user's name, email, and overall learning statistics, including total words studied.
-   **Clean UI**: Built with Material Design components for a modern and responsive user experience, featuring bottom navigation for easy access to different sections.

## Technology Stack

-   **Language**: Kotlin
-   **Platform**: Android
-   **Backend**: 
    -   **Firebase Authentication**: For user management.
    -   **Cloud Firestore**: As the real-time NoSQL database for storing levels, lessons, flashcards, and user progress.
-   **Architecture**: Follows the Repository Pattern to abstract data sources.
-   **Android Jetpack**: 
    -   ViewBinding
    -   AppCompat
    -   ConstraintLayout
-   **UI**: Material Design Components (CardView, BottomNavigationView, TextInputLayout, RecyclerView).

## Firebase Setup

This project uses Google Firebase for its backend. To run the app, you need to set up your own Firebase project.

### Database Structure

The Cloud Firestore database should be structured with the following collections:

-   `levels`: Stores documents for each learning level.
    -   Fields: `name` (e.g., "N5"), `description`, `order`.
-   `lessons`: Contains documents for lessons, linked to a level.
    -   Fields: `levelId`, `title`, `order`, `totalCards`.
-   `flashcards`: Contains the individual vocabulary cards for each lesson.
    -   Fields: `lessonId`, `word`, `reading`, `meaning`, `example`.
-   `users`: Stores registered user information.
    -   Fields: `uid`, `email`, `createdAt`.
-   `user_progress`: Tracks each user's interaction with a card.
    -   Fields: `userId`, `lessonId`, `cardId`, `status` ("remembered" or "not_remembered"), `updatedAt`.

## Getting Started

Follow these steps to get the project running on your local machine.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/trannhattan5/nihongo-flashcard-app.git
    ```

2.  **Set up Firebase:**
    -   Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
    -   Add an Android app to your project with the package name: `com.example.nihongoflashcardapp`.
    -   Download the `google-services.json` file provided during the setup process.
    -   Place the downloaded `google-services.json` file into the `app/` directory of the project.

3.  **Enable Firebase Services:**
    -   In the Firebase console, navigate to the **Authentication** section and enable the **Email/Password** sign-in provider.
    -   Navigate to the **Firestore Database** section and create a database. Populate it with data according to the structure described above.

4.  **Run the App:**
    -   Open the project in Android Studio.
    -   Allow Gradle to sync the project files.
    -   Run the application on an Android emulator or a physical device.

## Project Structure

The project is organized into several key packages to maintain a clean architecture:

-   `activities`: Contains the UI logic for each screen (e.g., Login, Level selection, Flashcards).
-   `adapter`: Includes `RecyclerView.Adapter` implementations for displaying lists of levels and lessons.
-   `repository`: Abstracts data sources (Firebase) from the rest of the app. Each repository handles data operations for a specific feature.
-   `models`: Defines the Kotlin data classes that represent the objects in the application (e.g., `Flashcard`, `Lesson`, `UserProgress`).
-   `navigation`: Helpers for handling navigation, such as the bottom navigation bar.
-   `firebase`: Contains the singleton object for accessing Firebase services.