
# 🏨 Hostel Assist: Distributed Systems Project

A distributed Java application demonstrating **Socket Programming**, **RMI**, **REST APIs**, and **P2P File Sharing** in a single cohesive system. Built for the Distributed Systems Lab.

### 🚀 Key Features (Modules)

1. **Authentication & Complaints (Socket):** Secure login and real-time ticket lodging for students.
2. **Notice Board (REST API):** A custom HTTP server handling notices with dynamic updates.
3. **Resource Sharing (P2P):** Decentralized peer-to-peer file transfer between students.
4. **Mess Feedback (Java RMI):** Remote method invocation to fetch menus and submit feedback.
5. **Admin Dashboard:** Warden panel to manage tickets and post notices.

---

### 🛠️ Tech Stack

* **Language:** Java (JDK 8+)
* **GUI:** Java Swing
* **Networking:** `java.net.Socket`, `java.rmi`, `com.sun.net.httpserver`
* **Architecture:** Client-Server & Peer-to-Peer Hybrid

---

### ⚡ How to Run (The Easy Way)

You do **not** need to compile code manually. We have a one-click launcher.

1. **Prerequisite:** Ensure Java is installed (`java -version`).
2. **Download** this repository.
3. **Double-click `HostelApplication.bat**`.

> **Note:** The launcher will automatically:
> * Kill any old background servers.
> * Compile the latest code.
> * Start the **Hostel Server** (Minimized).
> * Launch the **Client App**.
> 
> 

---

### 🔑 Test Credentials

| Role | Username | Password | Access |
| --- | --- | --- | --- |
| **Student** | `alice` | `123` | Lodge complaints, Share files, View notices |
| **Student** | `bob` | `123` | (Use to test P2P file sharing with Alice) |
| **Warden** | `admin` | `admin` | Resolve tickets, Post notices (REST) |

---

### 🧪 How to Test P2P (Multiplayer)

To test file sharing between two students:

1. Run `HostelApplication.bat` -> Login as **Alice** -> Click **Share File**.
2. Run `HostelApplication.bat` **again** (keep Alice open) -> Login as **Bob**.
3. On Bob's window, go to **Resource Share** -> Click **Refresh List**.
4. Download Alice's file directly!

---

**Made with ☕ and Java Sockets.**
