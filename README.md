# 🐕 Doge-Watchdog

**A sophisticated Android application for civic transparency and fiscal monitoring.**

Doge-Watchdog is designed to bridge the gap between complex government data and the citizens it impacts. By integrating with high-fidelity public API endpoints, the application provides a real-time dashboard that tracks, visualizes, and audits government spending allocations.

---

## 🚀 Key Features

- **Real-Time Fiscal Tracking**: Continuous integration with government spending APIs to provide up-to-the-minute expenditure data.
- **Transparency Dashboards**: Interactive charts and visualizations built with **Jetpack Compose** that break down spending by department, category, and date.
- **Audit Alerts**: Automated notifications for unusually large fiscal allocations or deviations from historical spending patterns.
- **Public API Integration**: Native support for the **USAspending.gov API** and other regional transparency endpoints.

---

## 🛠️ Technical Stack

- **UI**: 100% Jetpack Compose for a modern, reactive interface.
- **Networking**: Retrofit + OkHttp for robust API communication.
- **Architecture**: MVVM with Clean Architecture principles for maintainable code.
- **Data Persistence**: Room Database for offline caching of fiscal records.

---

## 📦 Local Setup

1.  **Clone the Repository**: `git clone https://github.com/TheGitCommitMan/Doge-Watchdog.git`
2.  **Open in Android Studio**: Ladybug or newer.
3.  **API Keys**: Register for a developer account at your preferred transparency portal and configure the keys in the application settings.
4.  **Build**: Execute `./gradlew assembleDebug` to generate the testing APK.
