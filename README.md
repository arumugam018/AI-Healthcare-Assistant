# CareSync AI Healthcare Assistant

Your personal AI Healthcare Assistant project built with Java, Spring Boot, and Google Gemini.

## Features
- **AI Symptom Checker:** Analyzes symptoms uniquely inputted by users and suggests over-the-counter medications using Google Gemini.
- **Medicine Reminder:** Local database-backed schedule for keeping track of critical medicine doses.
- **Dashboard Data:** High-level dashboard integrating data. 

## Configuration & Prerequisites

Before running the application, make sure you have:
1. **Java 17 or higher** installed.
2. **Google Gemini API Key**: Get a key from Google AI Studio.
3. **Firebase Project**:
   - Create a Firebase Project.
   - Generate a new private key from your Project Settings > Service Accounts.
   - Save the downloaded JSON file as `firebase-service-account.json` in the `healthcare-ai` root folder (this file is excluded from Git commit for security).

## How to Run The Project

### Step 1: Open Terminal in the Project folder
Ensure you are in the application root directory:
```powershell
cd "healthcare-ai"
```

### Step 2: Start the Java Application
Run the batch script and pass your Gemini API key:
```powershell
.\run.bat YOUR_GEMINI_API_KEY
```
*(Alternatively, you can set the `GEMINI_API_KEY` environment variable in your system and simply run `.\run.bat`)*

This will start compiling the Spring Boot application using the included Maven Wrapper and bring the web server online. Leave this window open while you use the app!

### Step 3: Access the Medical Assistant
Once your terminal displays the message `Started HealthcareAiApplication`, open any web browser (Chrome, Edge, Safari) and navigate to:
[http://localhost:8080](http://localhost:8080)

### To Stop The Server
When you are completely finished, go to the terminal running the server and simply close the window, or press `CTRL + C` on your keyboard to safely stop the Spring app.

## Database Note
This application relies on a lightning-fast H2 In-Memory Database to ensure you don't have to manually configure SQL constraints. That means your Medicine Reminders and Symptom History will reset each time the application is completely shut down.

