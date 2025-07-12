@echo off
echo Starting Turf Management System...
echo.
echo Backend will be available at: http://localhost:8080
echo Frontend can be opened at: frontend/index.html
echo.
echo Press Ctrl+C to stop the application
echo.

cd backend
mvn spring-boot:run

pause 