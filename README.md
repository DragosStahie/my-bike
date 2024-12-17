# MyBike

**MyBike** is a simple yet powerful app that helps you manage your bikes, log your rides, and stay on top of maintenance reminders. It was developed for the **Garmin Cluj Mobile App Contest**, where it won first place!

The project is built with **Clean Architecture** and modular design, keeping it clean, scalable, and ready for future upgrades.

---

## Features 🚴  

### 🛠 Manage Your Bikes  
- Add a new bike with its type, color, and service interval.  
- Edit or delete bikes whenever needed.  

### 📊 Log Your Rides  
- Keep track of your rides for each bike.  

### ⏰ Service Reminders  
- Get notified when your bike needs servicing.  

### ⚙️ Custom Settings  
- Adjust measurement units (e.g., kilometers/miles).  
- Tweak service interval preferences.  

---

## Under the Hood 🔧  

The project follows **Clean Architecture** principles and uses a **feature-based modular structure** for better organization and scalability. Here's how it's split:  

- **Data Layer**: Handles all data-related tasks (storage, future integrations).  
- **Domain Layer**: Contains the business logic and core app use cases.  
- **Presentation Layer**: Built using the **classic Android View system** for the UI.  

### Navigation  
The app uses the **Navigation Component library** with nested navigation graphs to move between features seamlessly.  

---

## Tech Stack 🛠  
- **Language**: Kotlin  
- **Concurrency**: Coroutines  
- **Streams**: SharedFlow, StateFlow  
- **UI**: Android Views  
- **Navigation**: Navigation Component Library  
- **Modularization**: Feature-based (e.g., Bikes, Rides)  

---

## Screenshots 📸  


---

## Future Plans 🚀  

There’s plenty of room to take MyBike to the next level:  

- **GPS Ride Tracking**  
  - Record rides with GPS and view your routes on a map.  

- **Sensor Integrations**  
  - Add support for sensors (speed, cadence, power) to track ride stats in real-time.  

- **Detailed Ride Insights**  
  - Visualize ride data with maps and interactive graphs.  

- **Social Features**  
  - Add accounts and allow users to share their bikes and rides with friends.  

---

## Credits 🏆  

- **Developed by**: [Dragos Stahie]  
- **Built for**: Garmin Cluj Mobile App Contest  
- **Award**: First Place Winner  
