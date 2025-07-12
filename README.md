# Turf Management System

A comprehensive web application for turf booking and management with real-time features, equipment shop, and automated billing.

## Features

- **Real-time Turf Booking**: Dynamic booking system with availability tracking
- **Equipment Shop**: Sporting equipment and energy drinks for purchase
- **Automated Billing**: Integrated billing system with invoice generation
- **Redis Integration**: Caching and real-time data management
- **Responsive UI**: Modern, mobile-friendly interface

## Technology Stack

### Frontend
- HTML5, CSS3, JavaScript
- Bootstrap for responsive design
- WebSocket for real-time updates

### Backend
- Spring Boot 3.x
- Spring Data Redis
- Spring WebSocket
- Maven for dependency management

### Database
- Redis Cloud for caching and real-time data
- Connection: `redis-10823.c262.us-east-1-3.ec2.redns.redis-cloud.com:10823`

## Project Structure

```
turf-management-system/
├── frontend/                 # HTML/JavaScript frontend
│   ├── index.html           # Main application page
│   ├── css/                 # Stylesheets
│   ├── js/                  # JavaScript files
│   └── assets/              # Images and other assets
├── backend/                 # Spring Boot application
│   ├── src/main/java/       # Java source code
│   ├── src/main/resources/  # Configuration files
│   └── pom.xml             # Maven dependencies
└── README.md               # This file
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Redis Cloud account (already configured)

### Running the Application

1. **Start the Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Open Frontend**:
   - Navigate to `frontend/index.html` in your browser
   - Or serve using a local server

## API Endpoints

- `GET /api/turfs` - Get all available turfs
- `POST /api/bookings` - Create a new booking
- `GET /api/equipment` - Get available equipment
- `POST /api/orders` - Place equipment order
- `GET /api/bills/{id}` - Get billing information

## Redis Configuration

The application uses Redis Cloud with the following configuration:
- Host: redis-10823.c262.us-east-1-3.ec2.redns.redis-cloud.com
- Port: 10823
- Database: default
- Max Data: 25MB (as per requirements)

## License

MIT License 