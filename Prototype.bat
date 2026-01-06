@echo off
cd src
javac hostel/module1/*.java hostel/module2/*.java hostel/module3/*.java hostel/module4/*.java hostel/module5/*.java
echo Compilation Complete.

:: MODULE 1: Sockets
start "Socket Server" java hostel.module1.ComplaintServer
timeout /t 1
start "Student Client" java hostel.module1.StudentClient

:: MODULE 2: RMI
start "RMI Server" java hostel.module2.RoomServer

:: MODULE 3: REST
start "REST Server" java hostel.module3.NoticeBoardServer
start http://localhost:8000/notices

:: MODULE 4: P2P
start "P2P Node A" java hostel.module4.P2PNode
start "P2P Node B" java hostel.module4.P2PNode

:: MODULE 5: Shared Memory
start "Mess Feedback" java hostel.module5.MessFeedback
start "Mess Feedback" java hostel.module5.MessFeedback

echo All Modules Running!
pause