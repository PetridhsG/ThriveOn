# 📱 ThriveOn – Smart Habit Tracking & Goal-Oriented Social App

## 🧠 Project Summary

**ThriveOn** is a hybrid habit-tracking and social media app aimed at boosting productivity, motivation, and personal development through goal-setting and community engagement. Users can select or create daily goals, share achievements, and earn rewards like badges, streaks, and rerolls.

---

## 🔍 Key Features

- **AI-Powered Goal Suggestions**  
  Based on user-selected interest categories, the AI recommends 3 daily goals. Users can reroll each suggestion up to 3 times using reroll tokens.

- **Custom Goals**  
  Users can create their own goals which do not offer rewards but provide flexibility.

- **Photo Sharing & Social Feed**  
  Users can post completed goals with or without photos. Posts appear on a social feed where friends can react.

- **Reward System**
    - **Rerolls**: Earned by uploading a photo of a completed goal.
    - **Streaks**: Increase with consecutive daily completions.
    - **Badges & Titles**: Earned upon completing milestones in specific categories.

- **User Interaction**
    - Add/search friends via username or suggestions.
    - View others’ profiles and achievements.
    - React to shared goal completions.

- **Profile Page**
    - Shows streaks, titles, badges, bio, and photos of completed goals.
    - Allows editing personal info and viewing notifications.

---

## 🧭 User Flow

1. **Sign Up / Login**  
   Requires email and password. User selects 3+ interest categories.

2. **Daily Goal Selection**
    - Accessed on Home Screen.
    - AI proposes 3 personalized goals.
    - Reroll available via photo uploads.

3. **Goal Completion**
    - Tap goal → Choose to share with/without photo.
    - If photo is uploaded → opens camera → preview → confirm/retry.

4. **Sharing & Feed**
    - Completed goals published on the feed.
    - Includes time, category, photo (or default), and reactions.

5. **Profile Exploration**
    - Access personal and friends’ achievements.
    - Navigation via date picker, carousel, and menus.

---

## 🏗️ App Architecture

### Frontend

- **Architecture Pattern**: MVI (Model–View–Intent)
- **Platform**: Android SDK 31
- **Language**: Kotlin
- **Libraries**:
    - Jetpack Compose (Material 3, Navigation)
    - Kotlin Flows & Coroutines
- **UI Design**: Figma
- **IDE**: Android Studio

### Backend

- **Approach**: Serverless using Google Firebase
- **Services Used**:
    - **Authentication**: User login/registration
    - **Cloud Firestore**: User data and goals
    - **Firebase Storage**: Images and media
    - **Firebase SDK**: Android integration

---

## 🗂️ App Package Structure

The app is organized into 5 main packages under the `app` folder:

- **core**: Contains core classes responsible for common utilities and work needed throughout the app.
- **di**: Manages dependency injection for all necessary app components.
- **domain**: Hosts data models and interactors that communicate with data sources such as Firebase.
- **network**: Handles network calls specifically to the large language model API (Llama 4).
- **ui**: Includes all UI-related code such as screens, components, themes, and navigation.

---

## ⚙️ Build & Versioning

- **Build System**: Gradle
- **Versioning**: Managed using a version catalog for dependencies and version numbers to maintain consistency across the app.

---

## 🔐 Local Properties

Local environment variables for development are configured as follows (excluding sensitive API keys here):

```properties
API_KEY=your_api_key
BASE_URL=https://openrouter.ai/api/v1/
MODEL=meta-llama/llama-4-maverick:free
TEMPERATURE=0.8
```
Note that google-services.json is excluded as well.

## 📦 APK Release

You can download the release version of **ThriveOn** APK here:  
[ThriveOn Release APK](https://drive.google.com/file/d/1VOe1yDd_5hWPfLf-9Smk_gV8srg3Am51/view?usp=sharing)
