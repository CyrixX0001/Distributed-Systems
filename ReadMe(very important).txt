# 🏨 Hostel Assist: The Ultimate Distributed System

A premium, production-ready Distributed System demonstrating **Sockets, RMI, REST, and P2P** networking. Featuring a polished Dark Mode UI, smooth animations, and a simulated database of 50+ users.

---

### 🚀 Key Features

**1. 🎨 Premium UI/UX**

* **Dark Mode:** Professional VS Code-inspired dark theme.
* **Animations:** Smooth 60fps fade-in on launch, color-morphing buttons, and "Shake-on-Error" login feedback.
* **Full Screen:** Auto-launches in maximized mode.

**2. 🔐 Advanced Networking Modules**

* **Authentication (Sockets):** Secure login validation against a live server database.
* **Private P2P Sharing:** Send files securely to specific users (e.g., "For Bob Only") or broadcast to everyone.
* **Dynamic Mess Menu (RMI):** Auto-detects the real-world day (Mon-Sun) and serves the correct menu (e.g., "Pongal" on Monday).
* **Notice Board (REST API):** A custom HTTP Server allowing Wardens to post announcements visible to all students.

**3. 📊 Smart Management Systems**

* **Room Database:** Search any room (e.g., "101") to see **who lives there** and their active complaints.
* **Warden Tracking:** Tickets now track *exactly* which warden resolved them (e.g., "Resolved by warden1").
* **Simulated Data:** The server auto-generates **50 Indian Students** (Aarav, Sai, Diya, etc.) across 10 rooms.

---

### 🛠️ Tech Stack

* **Language:** Java (JDK 8+)
* **GUI:** Java Swing (Custom Painted Components)
* **Networking:** `java.net.Socket`, `java.rmi`, `com.sun.net.httpserver`
* **Architecture:** Hybrid Client-Server & Peer-to-Peer

---

### ⚡ How to Run

1. **Prerequisite:** Ensure Java is installed (`java -version`).
2. **Optional:** Place a `logo.png` in the folder for a custom app icon.
3. **Run:** Double-click **`launcher.bat`**.

> **Note:** The launcher automatically compiles code, kills old processes, starts the Server (minimized), and opens the Client.

---

### 🔑 Test Credentials (Database of 50 Users)

The system is pre-loaded with **50 Students** (Rooms 101–110) and **3 Wardens**.

| Role | Username | Password | Room | Features |
| --- | --- | --- | --- | --- |
| **Warden (Head)** | `admin` | `admin` | Office | Full Access |
| **Warden 1** | `warden1` | `warden1` | Office | Test "Resolved By" feature |
| **Warden 2** | `warden2` | `warden2` | Office | Verify Warden 1's actions |
| **Student (Room 101)** | `sai` | `sai123` | 101 | Test Room 101 complaints |
| **Student (Room 101)** | `aarav` | `aarav123` | 101 | Roommate of Sai |
| **Student (Room 110)** | `simran` | `simran123` | 110 | Test Room 110 |
| **Legacy User** | `alice` | `123` | 101 | Standard Test |

---

### 🧪 Testing Guide (Showcase These!)

#### 1. 📂 Test Private P2P Transfer

1. Login as **`sai`**.
2. Go to **Resource Share** -> Click **Share File**.
3. Select a file. When asked "Who is this for?", type **`aarav`**.
4. Login as **`admin`** -> Check Resource Share -> **Empty** (Correct! It's private).
5. Login as **`aarav`** -> Check Resource Share -> **File Visible!**

#### 2. 🕵️ Test Warden Tracking

1. Login as **`sai`** -> Lodge a complaint ("Fan Broken").
2. Login as **`warden1`** -> Go to **Ticket Manager** -> Update status to **RESOLVED**.
3. Login as **`warden2`** -> Refresh List.
4. **Result:** You will see **"Status: RESOLVED (By: warden1)"**.

#### 3. 📅 Test Dynamic Menu

1. Login as anyone.
2. Go to **Mess Feedback** -> Click **Load Menu**.
3. It will show the specific food for **Today** (checked via System Date).

#### 4. 🏠 Test Room Database

1. Login as **`admin`**.
2. Go to **Room Database**.
3. Search **`101`**.
4. **Result:** Lists Residents: *Aarav, Vihaan, Aditya, Sai, Vikram* + All their tickets.

---

**Made with ☕ and Java Sockets.**