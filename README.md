# MyBike

**MyBike** is a simple yet powerful app that helps you manage your bikes, log your rides, and stay on top of maintenance reminders. It was developed for the **Garmin Cluj Mobile App Contest**, where it won first place!

The project is built with **Clean Architecture** and modular design, keeping it clean, scalable, and ready for future upgrades.

---

## 📸 Screenshots

Here are some highlights of the app in action:  

| Feature                         | Screenshots                                                                                                                        |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| **Splash Screen**              |  <p float="left"><img src="https://github.com/user-attachments/assets/7d1a4415-ee47-4cca-9a2d-38b24be5f1f9" alt="splash screen" height="600"/></p>|
| **Managing Bikes**              |  <p float="left"><img src="https://github.com/user-attachments/assets/515d2f24-c771-423f-9d82-8b1410bf0f32" alt="bikes empty" height="600"/><img src="https://github.com/user-attachments/assets/0e3a282c-db26-4f7e-b578-c2aaae7e10ed" alt="bikes populated" height="600"/></p>|
| **Managing Rides**              |  <p float="left"><img src="https://github.com/user-attachments/assets/764ab9cf-36ed-4ab1-8aa2-df38fb99fb1c" alt="rides empty" height="600"/><img src="https://github.com/user-attachments/assets/8e2d7f4c-ccf6-47dd-9ed7-301d61d47ab3" alt="rides populated" height="600"/></p>|
| **Add Bikes & Rides**              |  <p float="left"><img src="https://github.com/user-attachments/assets/2f782fab-eeeb-46b7-b9b6-fc7845bd2d91" alt="add bikes" height="600"/><img src="https://github.com/user-attachments/assets/5945b984-bd39-4fd1-ae3b-3668f61fb11c" alt="add rides" height="600"/></p>|
| **Settings & notifications**              |  <p float="left"><img src="https://github.com/user-attachments/assets/e0fb5aef-9931-46aa-a3e4-d4de83230b08" alt="settings" height="600"/><img src="https://github.com/user-attachments/assets/4272af36-8a91-4585-b21f-8100e6e2659a" alt="notification" height="600"/></p>|
| **Service reminders**              |  <p float="left"><img src="https://github.com/user-attachments/assets/762a6a75-0775-4ef3-9bc9-534c842714e4" alt="service reminder banner" height="600"/><img src="https://github.com/user-attachments/assets/216aefa1-9428-4d11-9241-6c2416622fa2" alt="reset service confirmation" height="600"/></p>|

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
