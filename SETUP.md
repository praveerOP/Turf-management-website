# Turf Management System - Setup Guide

## Overview

This is a comprehensive Turf Management System with real-time booking capabilities, equipment shop, and automated billing. The system uses Spring Boot for the backend with Redis integration and a modern HTML/JavaScript frontend.

## Features

### ✅ Implemented Features
- **Real-time Turf Booking**: Dynamic booking system with availability tracking
- **Equipment Shop**: Sporting equipment and energy drinks for purchase
- **Automated Billing**: Integrated billing system with tax calculation
- **Redis Integration**: Caching and real-time data management (25MB limit)
- **Responsive UI**: Modern, mobile-friendly interface
- **WebSocket Support**: Real-time updates and notifications
- **Sample Data**: Pre-loaded turfs and equipment for testing

### 🏟️ Turf Management
- Multiple turf types (Football, Cricket, Tennis, Basketball)
- Real-time availability tracking
- Booking validation and conflict detection
- Automated pricing calculation
- Booking status management

### 🛒 Equipment Shop
- Sporting equipment (Football, Cricket Bat, Tennis Racket, Basketball)
- Energy drinks (Red Bull, Monster, Powerade, Gatorade)
- Accessories (Sports Bag, Water Bottle)
- Stock management
- Shopping cart functionality
- Automated billing with tax calculation

## Technology Stack

### Backend
- **Spring Boot 3.2.0**: Main framework
- **Spring Data Redis**: Redis integration
- **Spring WebSocket**: Real-time communication
- **Maven**: Dependency management
- **Java 17**: Programming language

### Frontend
- **HTML5**: Structure
- **CSS3**: Styling with Bootstrap 5
- **JavaScript (ES6+)**: Functionality
- **WebSocket**: Real-time updates
- **Font Awesome**: Icons

### Database
- **Redis Cloud**: Primary data storage
- **Connection**: `redis-10823.c262.us-east-1-3.ec2.redns.redis-cloud.com:10823`

## Prerequisites

1. **Java 17 or higher**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **Web Browser** (Chrome, Firefox, Safari, Edge)

## Installation & Setup

### Step 1: Clone/Download the Project
Ensure all files are in the project directory structure:
```
turf-management-system/
├── backend/
│   ├── src/main/java/com/turfmanagement/
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/
│   ├── css/
│   ├── js/
│   └── index.html
├── README.md
├── SETUP.md
└── start.bat
```

### Step 2: Start the Backend

#### Option A: Using the Batch File (Windows)
```bash
start.bat
```

#### Option B: Manual Start
```bash
cd backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Step 3: Open the Frontend
1. Navigate to the `frontend` folder
2. Open `index.html` in your web browser
3. Or serve it using a local server:
   ```bash
   # Using Python 3
   cd frontend
   python -m http.server 8000
   # Then open http://localhost:8000
   ```

## Usage Guide

### 1. Home Page
- Overview of the system
- Quick access to booking and shop
- Real-time updates panel

### 2. Book Turf
1. Click "Book Turf" in navigation
2. Select an available turf from the grid
3. Fill in booking details:
   - Customer information
   - Date and time
   - Number of hours
4. Review the booking summary
5. Click "Confirm Booking"

### 3. Equipment Shop
1. Click "Equipment Shop" in navigation
2. Browse equipment by category:
   - Sports Equipment
   - Energy Drinks
   - Accessories
3. Add items to cart
4. Review cart and proceed to checkout
5. Fill in delivery information
6. Place order

### 4. View Bookings/Orders
- "My Bookings": View and manage turf bookings
- "My Orders": View equipment orders and status

## API Endpoints

### Turf Management
- `GET /api/turfs` - Get all turfs
- `GET /api/turfs/available` - Get available turfs
- `GET /api/turfs/{id}` - Get specific turf
- `POST /api/turfs` - Create new turf
- `PUT /api/turfs/{id}` - Update turf
- `DELETE /api/turfs/{id}` - Delete turf

### Booking Management
- `GET /api/turfs/bookings` - Get all bookings
- `GET /api/turfs/bookings/{id}` - Get specific booking
- `POST /api/turfs/bookings` - Create booking
- `PUT /api/turfs/bookings/{id}/status` - Update booking status
- `DELETE /api/turfs/bookings/{id}` - Cancel booking

### Equipment Management
- `GET /api/equipment` - Get all equipment
- `GET /api/equipment/available` - Get available equipment
- `GET /api/equipment/{id}` - Get specific equipment
- `POST /api/equipment` - Create new equipment
- `PUT /api/equipment/{id}` - Update equipment
- `DELETE /api/equipment/{id}` - Delete equipment

### Order Management
- `GET /api/equipment/orders` - Get all orders
- `GET /api/equipment/orders/{id}` - Get specific order
- `POST /api/equipment/orders` - Create order
- `PUT /api/equipment/orders/{id}/status` - Update order status
- `DELETE /api/equipment/orders/{id}` - Cancel order

### Data Initialization
- `POST /api/turfs/init` - Initialize sample turf data
- `POST /api/equipment/init` - Initialize sample equipment data

## Redis Configuration

The application uses Redis Cloud with the following configuration:
- **Host**: redis-10823.c262.us-east-1-3.ec2.redns.redis-cloud.com
- **Port**: 10823
- **Database**: 0
- **Max Data**: 25MB (as per requirements)

### Redis Data Structure
- **Turfs**: `turf:{id}` - Individual turf data
- **Bookings**: `booking:{id}` - Individual booking data
- **Equipment**: `equipment:{id}` - Individual equipment data
- **Orders**: `order:{id}` - Individual order data
- **Sets**: `all_turfs`, `all_bookings`, `all_equipment`, `all_orders` - ID collections

## Troubleshooting

### Common Issues

1. **Backend won't start**
   - Check Java version (must be 17+)
   - Check Maven installation
   - Verify Redis connection

2. **Frontend can't connect to backend**
   - Ensure backend is running on port 8080
   - Check CORS configuration
   - Verify API_BASE_URL in app.js

3. **Redis connection issues**
   - Verify Redis Cloud credentials
   - Check network connectivity
   - Ensure Redis service is active

4. **WebSocket connection fails**
   - Check if backend supports WebSocket
   - Verify WebSocket endpoint configuration
   - Check browser console for errors

### Error Messages

- **"Failed to load turfs"**: Backend connection issue
- **"Turf is not available"**: Time conflict or turf already booked
- **"Insufficient stock"**: Equipment out of stock
- **"Invalid booking data"**: Missing required fields

## Development

### Adding New Features

1. **New Turf Type**:
   - Add to `Turf` model
   - Update sample data in `TurfService`
   - Add UI elements in frontend

2. **New Equipment Category**:
   - Add to `Equipment` model
   - Update sample data in `EquipmentService`
   - Add filter button in frontend

3. **New API Endpoint**:
   - Create controller method
   - Add service method
   - Update frontend JavaScript

### Code Structure

```
backend/src/main/java/com/turfmanagement/
├── TurfManagementApplication.java    # Main application
├── config/
│   └── WebSocketConfig.java         # WebSocket configuration
├── controller/
│   ├── TurfController.java          # Turf REST API
│   └── EquipmentController.java     # Equipment REST API
├── model/
│   ├── Turf.java                    # Turf entity
│   ├── Booking.java                 # Booking entity
│   ├── Equipment.java               # Equipment entity
│   ├── Order.java                   # Order entity
│   └── OrderItem.java               # Order item entity
└── service/
    ├── RedisService.java            # Redis operations
    ├── TurfService.java             # Turf business logic
    └── EquipmentService.java        # Equipment business logic
```

## Performance Considerations

- **Redis Memory**: Limited to 25MB, implement cleanup strategies
- **Caching**: Use Redis for frequently accessed data
- **Real-time Updates**: WebSocket for live updates
- **Responsive Design**: Mobile-first approach

## Security Notes

- CORS enabled for development
- Input validation on backend
- Redis authentication configured
- Consider adding authentication for production

## Future Enhancements

- User authentication and authorization
- Payment gateway integration
- Email notifications
- Admin dashboard
- Advanced reporting
- Mobile app development
- Integration with external APIs

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review console logs
3. Verify Redis connection
4. Test API endpoints directly

---

**Note**: This is a development version. For production deployment, additional security measures and optimizations should be implemented. 