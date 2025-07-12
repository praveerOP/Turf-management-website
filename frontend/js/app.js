// Turf Management System - Frontend JavaScript

// Global variables
let selectedTurf = null;
let cart = [];
let stompClient = null;
let currentSection = 'home';

// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupWebSocket();
    loadInitialData();
});

// Initialize application
function initializeApp() {
    // Set minimum date for booking (today)
    const startTimeInput = document.getElementById('startTime');
    if (startTimeInput) {
        const today = new Date();
        const tomorrow = new Date(today);
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(9, 0, 0, 0); // Set to 9 AM tomorrow
        startTimeInput.min = tomorrow.toISOString().slice(0, 16);
    }

    // Setup event listeners
    setupEventListeners();
    
    // Initialize sample data
    initializeSampleData();
}

// Setup event listeners
function setupEventListeners() {
    // Booking form submission
    const bookingForm = document.getElementById('booking-form');
    if (bookingForm) {
        bookingForm.addEventListener('submit', handleBookingSubmit);
    }

    // Hours selection change
    const hoursSelect = document.getElementById('hours');
    if (hoursSelect) {
        hoursSelect.addEventListener('change', calculateTotalAmount);
    }

    // Start time change
    const startTimeInput = document.getElementById('startTime');
    if (startTimeInput) {
        startTimeInput.addEventListener('change', calculateTotalAmount);
    }
}

// Setup WebSocket connection
function setupWebSocket() {
    const socket = new SockJS('http://localhost:8080/api/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function(frame) {
        console.log('Connected to WebSocket: ' + frame);
        
        // Subscribe to real-time updates
        stompClient.subscribe('/topic/updates', function(message) {
            const update = JSON.parse(message.body);
            displayRealTimeUpdate(update);
        });
        
        // Subscribe to booking updates
        stompClient.subscribe('/topic/bookings', function(message) {
            const booking = JSON.parse(message.body);
            updateBookingDisplay(booking);
        });
        
        // Subscribe to equipment updates
        stompClient.subscribe('/topic/equipment', function(message) {
            const equipment = JSON.parse(message.body);
            updateEquipmentDisplay(equipment);
        });
    }, function(error) {
        console.log('WebSocket connection error: ' + error);
        // Retry connection after 5 seconds
        setTimeout(setupWebSocket, 5000);
    });
}

// Load initial data
function loadInitialData() {
    loadTurfs();
    loadEquipment();
    loadBookings();
    loadOrders();
}

// Initialize sample data
async function initializeSampleData() {
    try {
        // Initialize turfs
        await fetch(`${API_BASE_URL}/turfs/init`, { method: 'POST' });
        
        // Initialize equipment
        await fetch(`${API_BASE_URL}/equipment/init`, { method: 'POST' });
        
        // Reload data
        loadInitialData();
        
        showSuccessMessage('Sample data initialized successfully!');
    } catch (error) {
        console.error('Error initializing sample data:', error);
    }
}

// Navigation functions
function showSection(sectionName) {
    // Hide all sections
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => section.style.display = 'none');
    
    // Show selected section
    const selectedSection = document.getElementById(sectionName);
    if (selectedSection) {
        selectedSection.style.display = 'block';
        currentSection = sectionName;
        
        // Update navigation
        updateNavigation(sectionName);
        
        // Load section-specific data
        switch(sectionName) {
            case 'booking':
                loadTurfs();
                break;
            case 'shop':
                loadEquipment();
                break;
            case 'bookings':
                loadBookings();
                break;
            case 'orders':
                loadOrders();
                break;
        }
    }
}

function updateNavigation(activeSection) {
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => link.classList.remove('active'));
    
    const activeLink = document.querySelector(`[onclick="showSection('${activeSection}')"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }
}

// Turf Management Functions
async function loadTurfs() {
    try {
        const response = await fetch(`${API_BASE_URL}/turfs/available`);
        const turfs = await response.json();
        displayTurfs(turfs);
    } catch (error) {
        console.error('Error loading turfs:', error);
        showErrorMessage('Failed to load turfs');
    }
}

function displayTurfs(turfs) {
    const container = document.getElementById('turfs-container');
    if (!container) return;
    
    container.innerHTML = '';
    
    turfs.forEach(turf => {
        const turfCard = createTurfCard(turf);
        container.appendChild(turfCard);
    });
}

function createTurfCard(turf) {
    const col = document.createElement('div');
    col.className = 'col-md-6 col-lg-4 mb-4';
    
    col.innerHTML = `
        <div class="card turf-card" onclick="selectTurf('${turf.id}')" data-turf-id="${turf.id}">
            <div class="position-relative">
                <img src="${turf.imageUrl || 'https://via.placeholder.com/300x200?text=' + turf.name}" 
                     class="card-img-top turf-image" alt="${turf.name}">
                <span class="turf-status ${turf.isAvailable ? 'available' : 'unavailable'}">
                    ${turf.isAvailable ? 'Available' : 'Unavailable'}
                </span>
            </div>
            <div class="card-body">
                <h5 class="card-title">${turf.name}</h5>
                <p class="card-text">${turf.description}</p>
                <div class="d-flex justify-content-between align-items-center">
                    <span class="badge bg-secondary">${turf.type}</span>
                    <span class="badge bg-info">${turf.size}</span>
                </div>
                <div class="mt-3">
                    <strong class="text-success">$${turf.pricePerHour}/hour</strong>
                </div>
            </div>
        </div>
    `;
    
    return col;
}

function selectTurf(turfId) {
    // Remove previous selection
    const previousSelected = document.querySelector('.turf-card.selected');
    if (previousSelected) {
        previousSelected.classList.remove('selected');
    }
    
    // Add selection to clicked card
    const selectedCard = document.querySelector(`[data-turf-id="${turfId}"]`);
    if (selectedCard) {
        selectedCard.classList.add('selected');
    }
    
    // Get turf details
    fetch(`${API_BASE_URL}/turfs/${turfId}`)
        .then(response => response.json())
        .then(turf => {
            selectedTurf = turf;
            updateBookingSummary(turf);
            document.getElementById('selectedTurf').value = turf.name;
            calculateTotalAmount();
        })
        .catch(error => {
            console.error('Error fetching turf details:', error);
        });
}

function updateBookingSummary(turf) {
    const summary = document.getElementById('booking-summary');
    if (summary) {
        summary.innerHTML = `
            <h6>${turf.name}</h6>
            <p class="text-muted">${turf.description}</p>
            <div class="d-flex justify-content-between">
                <span>Type:</span>
                <span class="badge bg-secondary">${turf.type}</span>
            </div>
            <div class="d-flex justify-content-between">
                <span>Size:</span>
                <span class="badge bg-info">${turf.size}</span>
            </div>
            <div class="d-flex justify-content-between">
                <span>Price:</span>
                <span class="text-success fw-bold">$${turf.pricePerHour}/hour</span>
            </div>
        `;
    }
}

function calculateTotalAmount() {
    if (!selectedTurf) return;
    
    const hours = parseInt(document.getElementById('hours').value) || 0;
    const total = selectedTurf.pricePerHour * hours;
    
    document.getElementById('totalAmount').value = `$${total.toFixed(2)}`;
}

async function handleBookingSubmit(event) {
    event.preventDefault();
    
    if (!selectedTurf) {
        showErrorMessage('Please select a turf first');
        return;
    }
    
    const formData = new FormData(event.target);
    const bookingData = {
        turfId: selectedTurf.id,
        customerName: formData.get('customerName') || document.getElementById('customerName').value,
        customerEmail: formData.get('customerEmail') || document.getElementById('customerEmail').value,
        customerPhone: formData.get('customerPhone') || document.getElementById('customerPhone').value,
        startTime: document.getElementById('startTime').value,
        hours: parseInt(document.getElementById('hours').value),
        endTime: calculateEndTime(document.getElementById('startTime').value, document.getElementById('hours').value)
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/turfs/bookings`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bookingData)
        });
        
        if (response.ok) {
            const booking = await response.json();
            showSuccessMessage('Booking created successfully!');
            event.target.reset();
            selectedTurf = null;
            document.getElementById('selectedTurf').value = '';
            document.getElementById('totalAmount').value = '';
            
            // Remove selection from turf cards
            const selectedCard = document.querySelector('.turf-card.selected');
            if (selectedCard) {
                selectedCard.classList.remove('selected');
            }
            
            // Reload turfs to update availability
            loadTurfs();
        } else {
            showErrorMessage('Failed to create booking');
        }
    } catch (error) {
        console.error('Error creating booking:', error);
        showErrorMessage('Failed to create booking');
    }
}

function calculateEndTime(startTime, hours) {
    const start = new Date(startTime);
    const end = new Date(start.getTime() + (hours * 60 * 60 * 1000));
    return end.toISOString();
}

// Equipment Shop Functions
async function loadEquipment() {
    try {
        const response = await fetch(`${API_BASE_URL}/equipment/available`);
        const equipment = await response.json();
        displayEquipment(equipment);
    } catch (error) {
        console.error('Error loading equipment:', error);
        showErrorMessage('Failed to load equipment');
    }
}

function displayEquipment(equipment) {
    const container = document.getElementById('equipment-container');
    if (!container) return;
    
    container.innerHTML = '';
    
    equipment.forEach(item => {
        const equipmentCard = createEquipmentCard(item);
        container.appendChild(equipmentCard);
    });
}

function createEquipmentCard(equipment) {
    const col = document.createElement('div');
    col.className = 'col-md-6 col-lg-4 mb-4';
    
    col.innerHTML = `
        <div class="card equipment-card">
            <img src="${equipment.imageUrl || 'https://via.placeholder.com/300x200?text=' + equipment.name}" 
                 class="card-img-top equipment-image" alt="${equipment.name}">
            <div class="card-body">
                <h5 class="card-title">${equipment.name}</h5>
                <p class="card-text">${equipment.description}</p>
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <span class="equipment-price">$${equipment.price}</span>
                    <span class="equipment-stock">Stock: ${equipment.stockQuantity}</span>
                </div>
                <div class="d-flex justify-content-between align-items-center">
                    <span class="badge bg-primary">${equipment.category}</span>
                    <button class="btn btn-success btn-sm" onclick="addToCart('${equipment.id}', '${equipment.name}', ${equipment.price})">
                        <i class="fas fa-plus me-1"></i>Add to Cart
                    </button>
                </div>
            </div>
        </div>
    `;
    
    return col;
}

function filterEquipment(category) {
    // Update active button
    const buttons = document.querySelectorAll('.btn-outline-primary');
    buttons.forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    if (category === 'all') {
        loadEquipment();
    } else {
        fetch(`${API_BASE_URL}/equipment/category/${category}`)
            .then(response => response.json())
            .then(equipment => displayEquipment(equipment))
            .catch(error => {
                console.error('Error filtering equipment:', error);
                showErrorMessage('Failed to filter equipment');
            });
    }
}

// Shopping Cart Functions
function addToCart(equipmentId, name, price) {
    const existingItem = cart.find(item => item.id === equipmentId);
    
    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({
            id: equipmentId,
            name: name,
            price: price,
            quantity: 1
        });
    }
    
    updateCartDisplay();
    showSuccessMessage(`${name} added to cart!`);
}

function updateCartDisplay() {
    const cartItems = document.getElementById('cart-items');
    const cartTotal = document.getElementById('cart-total');
    
    if (cart.length === 0) {
        cartItems.innerHTML = '<p class="text-muted">Your cart is empty</p>';
        cartTotal.textContent = '$0.00';
        return;
    }
    
    let total = 0;
    cartItems.innerHTML = '';
    
    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        
        const itemElement = document.createElement('div');
        itemElement.className = 'cart-item';
        itemElement.innerHTML = `
            <div>
                <div class="cart-item-name">${item.name}</div>
                <div class="text-muted">$${item.price} each</div>
            </div>
            <div class="cart-item-quantity">
                <button class="quantity-btn" onclick="updateQuantity('${item.id}', -1)">-</button>
                <span>${item.quantity}</span>
                <button class="quantity-btn" onclick="updateQuantity('${item.id}', 1)">+</button>
            </div>
        `;
        cartItems.appendChild(itemElement);
    });
    
    cartTotal.textContent = `$${total.toFixed(2)}`;
}

function updateQuantity(equipmentId, change) {
    const item = cart.find(item => item.id === equipmentId);
    if (item) {
        item.quantity += change;
        if (item.quantity <= 0) {
            cart = cart.filter(item => item.id !== equipmentId);
        }
        updateCartDisplay();
    }
}

function checkout() {
    if (cart.length === 0) {
        showErrorMessage('Your cart is empty');
        return;
    }
    
    // Show checkout modal
    const modal = new bootstrap.Modal(document.getElementById('checkoutModal'));
    modal.show();
    
    // Update order summary
    updateOrderSummary();
}

function updateOrderSummary() {
    const summary = document.getElementById('order-summary');
    let total = 0;
    
    let summaryHTML = '<div class="table-responsive"><table class="table table-sm">';
    summaryHTML += '<thead><tr><th>Item</th><th>Qty</th><th>Price</th><th>Total</th></tr></thead><tbody>';
    
    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        summaryHTML += `
            <tr>
                <td>${item.name}</td>
                <td>${item.quantity}</td>
                <td>$${item.price}</td>
                <td>$${itemTotal.toFixed(2)}</td>
            </tr>
        `;
    });
    
    const tax = total * 0.10;
    const grandTotal = total + tax;
    
    summaryHTML += '</tbody></table>';
    summaryHTML += `
        <div class="d-flex justify-content-between">
            <span>Subtotal:</span>
            <span>$${total.toFixed(2)}</span>
        </div>
        <div class="d-flex justify-content-between">
            <span>Tax (10%):</span>
            <span>$${tax.toFixed(2)}</span>
        </div>
        <hr>
        <div class="d-flex justify-content-between fw-bold">
            <span>Total:</span>
            <span>$${grandTotal.toFixed(2)}</span>
        </div>
    </div>`;
    
    summary.innerHTML = summaryHTML;
}

async function placeOrder() {
    const form = document.getElementById('checkout-form');
    const formData = new FormData(form);
    
    const orderData = {
        customerName: formData.get('orderName') || document.getElementById('orderName').value,
        customerEmail: formData.get('orderEmail') || document.getElementById('orderEmail').value,
        customerPhone: formData.get('orderPhone') || document.getElementById('orderPhone').value,
        deliveryAddress: formData.get('orderAddress') || document.getElementById('orderAddress').value,
        items: cart.map(item => ({
            equipmentId: item.id,
            equipmentName: item.name,
            quantity: item.quantity,
            unitPrice: item.price,
            totalPrice: item.price * item.quantity
        }))
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/equipment/orders`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderData)
        });
        
        if (response.ok) {
            const order = await response.json();
            showSuccessMessage('Order placed successfully!');
            
            // Clear cart
            cart = [];
            updateCartDisplay();
            
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('checkoutModal'));
            modal.hide();
            
            // Reset form
            form.reset();
            
            // Reload orders
            loadOrders();
        } else {
            showErrorMessage('Failed to place order');
        }
    } catch (error) {
        console.error('Error placing order:', error);
        showErrorMessage('Failed to place order');
    }
}

// Booking and Order Management Functions
async function loadBookings() {
    try {
        const response = await fetch(`${API_BASE_URL}/turfs/bookings`);
        const bookings = await response.json();
        displayBookings(bookings);
    } catch (error) {
        console.error('Error loading bookings:', error);
        showErrorMessage('Failed to load bookings');
    }
}

function displayBookings(bookings) {
    const container = document.getElementById('bookings-container');
    if (!container) return;
    
    if (bookings.length === 0) {
        container.innerHTML = '<p class="text-muted">No bookings found</p>';
        return;
    }
    
    container.innerHTML = '';
    
    bookings.forEach(booking => {
        const bookingElement = createBookingElement(booking);
        container.appendChild(bookingElement);
    });
}

function createBookingElement(booking) {
    const div = document.createElement('div');
    div.className = 'booking-item fade-in';
    
    const startTime = new Date(booking.startTime).toLocaleString();
    const endTime = new Date(booking.endTime).toLocaleString();
    
    div.innerHTML = `
        <div class="row">
            <div class="col-md-8">
                <h5>${booking.customerName}</h5>
                <p class="text-muted">${booking.customerEmail} | ${booking.customerPhone}</p>
                <div class="row">
                    <div class="col-md-6">
                        <strong>Start:</strong> ${startTime}
                    </div>
                    <div class="col-md-6">
                        <strong>End:</strong> ${endTime}
                    </div>
                </div>
                <div class="row mt-2">
                    <div class="col-md-6">
                        <strong>Hours:</strong> ${booking.hours}
                    </div>
                    <div class="col-md-6">
                        <strong>Total:</strong> $${booking.totalAmount}
                    </div>
                </div>
            </div>
            <div class="col-md-4 text-end">
                <span class="status-badge status-${booking.status.toLowerCase()}">${booking.status}</span>
                <div class="mt-2">
                    <button class="btn btn-sm btn-danger" onclick="cancelBooking('${booking.id}')">
                        <i class="fas fa-times me-1"></i>Cancel
                    </button>
                </div>
            </div>
        </div>
    `;
    
    return div;
}

async function cancelBooking(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking?')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/turfs/bookings/${bookingId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showSuccessMessage('Booking cancelled successfully!');
            loadBookings();
            loadTurfs(); // Reload turfs to update availability
        } else {
            showErrorMessage('Failed to cancel booking');
        }
    } catch (error) {
        console.error('Error cancelling booking:', error);
        showErrorMessage('Failed to cancel booking');
    }
}

async function loadOrders() {
    try {
        const response = await fetch(`${API_BASE_URL}/equipment/orders`);
        const orders = await response.json();
        displayOrders(orders);
    } catch (error) {
        console.error('Error loading orders:', error);
        showErrorMessage('Failed to load orders');
    }
}

function displayOrders(orders) {
    const container = document.getElementById('orders-container');
    if (!container) return;
    
    if (orders.length === 0) {
        container.innerHTML = '<p class="text-muted">No orders found</p>';
        return;
    }
    
    container.innerHTML = '';
    
    orders.forEach(order => {
        const orderElement = createOrderElement(order);
        container.appendChild(orderElement);
    });
}

function createOrderElement(order) {
    const div = document.createElement('div');
    div.className = 'order-item fade-in';
    
    const orderDate = new Date(order.orderDate).toLocaleString();
    
    let itemsHTML = '';
    order.items.forEach(item => {
        itemsHTML += `
            <div class="d-flex justify-content-between">
                <span>${item.equipmentName} x${item.quantity}</span>
                <span>$${item.totalPrice.toFixed(2)}</span>
            </div>
        `;
    });
    
    div.innerHTML = `
        <div class="row">
            <div class="col-md-8">
                <h5>${order.customerName}</h5>
                <p class="text-muted">${order.customerEmail} | ${order.customerPhone}</p>
                <p class="text-muted">${order.deliveryAddress}</p>
                <div class="mt-3">
                    <strong>Items:</strong>
                    <div class="ms-3">
                        ${itemsHTML}
                    </div>
                </div>
                <div class="row mt-3">
                    <div class="col-md-4">
                        <strong>Subtotal:</strong> $${order.subtotal.toFixed(2)}
                    </div>
                    <div class="col-md-4">
                        <strong>Tax:</strong> $${order.tax.toFixed(2)}
                    </div>
                    <div class="col-md-4">
                        <strong>Total:</strong> $${order.totalAmount.toFixed(2)}
                    </div>
                </div>
            </div>
            <div class="col-md-4 text-end">
                <span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span>
                <div class="mt-2">
                    <small class="text-muted">Ordered: ${orderDate}</small>
                </div>
            </div>
        </div>
    `;
    
    return div;
}

// Real-time Updates
function displayRealTimeUpdate(update) {
    const container = document.getElementById('realtime-updates');
    if (!container) return;
    
    const updateElement = document.createElement('div');
    updateElement.className = 'realtime-item new';
    updateElement.innerHTML = `
        <div class="d-flex justify-content-between">
            <span><i class="fas fa-bell me-2"></i>${update.message}</span>
            <small class="text-muted">${new Date().toLocaleTimeString()}</small>
        </div>
    `;
    
    container.insertBefore(updateElement, container.firstChild);
    
    // Remove old updates (keep only last 5)
    const updates = container.querySelectorAll('.realtime-item');
    if (updates.length > 5) {
        updates[updates.length - 1].remove();
    }
    
    // Remove 'new' class after animation
    setTimeout(() => {
        updateElement.classList.remove('new');
    }, 3000);
}

function updateBookingDisplay(booking) {
    if (currentSection === 'bookings') {
        loadBookings();
    }
}

function updateEquipmentDisplay(equipment) {
    if (currentSection === 'shop') {
        loadEquipment();
    }
}

// Utility Functions
function showSuccessMessage(message) {
    const successModal = document.getElementById('successModal');
    const successMessage = document.getElementById('success-message');
    successMessage.textContent = message;
    
    const modal = new bootstrap.Modal(successModal);
    modal.show();
}

function showErrorMessage(message) {
    // Create a simple alert for now - you can enhance this with a proper modal
    alert('Error: ' + message);
}

// Export functions for global access
window.showSection = showSection;
window.selectTurf = selectTurf;
window.filterEquipment = filterEquipment;
window.addToCart = addToCart;
window.updateQuantity = updateQuantity;
window.checkout = checkout;
window.placeOrder = placeOrder;
window.cancelBooking = cancelBooking; 