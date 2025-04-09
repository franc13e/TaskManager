# TaskManager Java Swing Application 🗂️

A desktop-based task scheduling and performance testing application built with Java Swing. This project was developed as part of my coursework, applying data structures like Linked Lists and Queues in a practical GUI setting.

## 📌 Current Features

- 👤 **User System**  
  - Basic username/password authentication
  - Default account: francine/1234
  - Simple user switching functionality
  - User data stored in memory (not persistent)
 
- 📝 **Task Management**  
  - Add, edit, and delete tasks
  - Mark tasks as complete/incomplete
  - Basic priority system (High/Medium/Low)
  - Tasks sorted by due date
  

- ⏱️ **Performance Testing**  
  - Simulate performance with predefined options (e.g., Add 100 users, Delete 150 users)
  - Performance logs displayed in real-time

- 🧠 **Data Structures Used**
  - `LinkedList` for managing users
  - `Queue` for managing system logs
  - Basic logging functionality

- 🖥️ **Java Swing GUI**
  - User-friendly interface with tabs for different functions
  - Priority color rendering for visual clarity

## 📁 File Structure

- `TaskManager.java` — Main application class with GUI setup  
- `User.java` — User class with data and methods  
- `Task.java` — Task entity for scheduling (if implemented)  
- `PriorityColorRenderer.java` — Custom renderer for UI table highlighting  
- `users.txt` — Text file storing user data

## 🚀 How to Run

1. Open the project in **NetBeans**.
2. Make sure your Java SDK is set up.
3. Run the `TaskManager.java` file.

> ⚠️ Make sure `users.txt` is in the root directory for proper loading/saving of users.

## 🛠️ Known Issues & Fixes Needed

**User Management**
Add User function not working
- The menu option exists but doesn't currently create new users. Needs connection to registration system.

**Missing logout option**
No way to return to login screen without exiting. Should add:
- "Logout" menu item
- Session timeout functionality
- Proper user switching flow

**Performance Testing**
Performance test fails
- The "Run Performance Test" option crashes when attempting to load test users. Requires:
- Better error handling
- Validation for user data files
- Progress feedback during testing

**Data Issues**
Inconsistent saving
Some users report tasks not persisting between sessions. Need to:
- Verify serialization is working
- Add save confirmation messages
- Implement backup system

## 🔄 Planned Improvements

- ⚙️ Core Functionality
  - Persistent data storage
  - Proper user registration
  - Task persistence between sessions

- 📊 Enhanced Features
  - Performance logging to CSV
  - Modern UI redesign
  - Advanced priority system

- 🔒 Security
  - Password hashing
  - Session management

## 👩‍💻 Author

**Francine**  
Student | Aspiring Full Stack Developer  
[GitHub](https://github.com/franc13e)

---

