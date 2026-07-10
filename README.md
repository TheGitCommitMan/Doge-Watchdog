# Doge-Watchdog

Doge-Watchdog is a sophisticated Android application designed to enhance civic transparency by monitoring and visualizing government spending. By integrating with public API endpoints, the application provides citizens with an intuitive dashboard to track fiscal allocations and expenditures in real-time.

Built with a focus on modern Android development practices, Doge-Watchdog leverages high-performance data processing to transform complex financial records into accessible, actionable insights.

## 🚀 Getting Started

Follow these instructions to set up the project and run it on your local environment for development and testing purposes.

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Latest Version Recommended)
- A valid API Key for data services

### Installation & Local Setup

1. **Clone the Repository**
   Open your terminal and clone the project to your local machine.

2. **Open in Android Studio**
   Launch Android Studio, select **Open**, and navigate to the project directory. Allow the IDE to sync Gradle files and resolve any dependencies.

3. **Configure Environment Variables**
   The application requires an API key for its data processing engine.
   - Create a file named `.env` in the root project directory.
   - Add your key to the file: `GEMINI_API_KEY=your_actual_key_here`
   - Refer to `.env.example` for the required format.

4. **Build Configuration**
   To prepare the project for a local debug build, locate the `build.gradle.kts` file in the app module and ensure the signing configuration is set for your local environment (e.g., removing any specific remote signing references if necessary).

5. **Deployment**
   Select your target device or emulator and click **Run** (Shift + F10) to deploy the application.
