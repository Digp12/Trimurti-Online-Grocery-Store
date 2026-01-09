🛒 Trimurti Kirana Stores - Online Grocery Shopping Platform
<div align="center">
Spring Boot
Java
MySQL
Thymeleaf
Bootstrap
Razorpay

A full-stack e-commerce web application for online grocery shopping with user & admin panels, order management, payment integration, and sales analytics.

Features • Tech Stack • Installation • Screenshots • Project Structure

</div>
📋 Table of Contents
About The Project
Features
Tech Stack
Installation
Screenshots
Project Structure
API Endpoints
Contributing
Contact
🎯 About The Project
Trimurti Kirana Stores is a comprehensive online grocery shopping platform designed to provide users with a seamless shopping experience. The application features a complete e-commerce workflow from product browsing to order placement and payment processing.

🌟 Key Highlights
🛍️ Complete E-commerce Solution - Browse, cart, checkout, and order tracking
👨‍💼 Dual Panel System - Separate interfaces for users and administrators
💳 Secure Payments - Integrated Razorpay payment gateway + COD option
📊 Business Analytics - Sales reports, top-selling products, revenue tracking
🔐 Robust Security - Spring Security with role-based access control
📱 Responsive Design - Works seamlessly across all devices
✨ Features
👤 User Features
Feature	Description
🔐 Authentication	Register, Login, Forgot/Reset Password
🛒 Shopping Cart	Add, update, remove products with real-time price calculation
📦 Order Management	Place orders, track status, cancel orders
💳 Payment Options	Razorpay integration + Cash on Delivery
🔍 Product Search	Search & filter products by category
👤 Profile Management	Update personal details and profile picture
🛠️ Admin Features
Feature	Description
📊 Dashboard	Overview of orders, products, users, and revenue
📦 Product Management	Add, edit, delete products with image upload
🗂️ Category Management	Manage product categories
👥 User Management	View, activate/deactivate users
📋 Order Management	Update order status, track deliveries
💰 Refund Management	Process refund requests
📈 Sales Reports	Daily, monthly, quarterly, yearly analytics
🛠️ Tech Stack
Backend
text

├── Java 17+
├── Spring Boot 3.x
├── Spring Security (Authentication & Authorization)
├── Spring Data JPA (Hibernate)
├── Maven (Build Tool)
Frontend
text

├── HTML5 / CSS3
├── Bootstrap 5
├── JavaScript
├── Thymeleaf (Template Engine)
Database & Payment
text

├── MySQL 8.0
├── Razorpay Payment Gateway
⚙️ Installation
Prerequisites
☕ Java 17 or higher
📦 Maven 3.6+
🐬 MySQL 8.0+
🌐 Stable Internet Connection
Step-by-Step Setup
1️⃣ Clone the Repository

Bash

git clone https://github.com/yourusername/TrimurtiKiranaStores.git
cd TrimurtiKiranaStores
2️⃣ Configure Database

Create a MySQL database:

SQL

CREATE DATABASE trimurti_kirana;
Update src/main/resources/application.properties:

properties

spring.datasource.url=jdbc:mysql://localhost:3306/trimurti_kirana
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
3️⃣ Configure Razorpay (Optional)

properties

razorpay.key.id=your_razorpay_key_id
razorpay.key.secret=your_razorpay_secret
4️⃣ Build & Run

Bash

mvn clean install
mvn spring-boot:run
5️⃣ Access the Application

text

🌐 User Portal:  http://localhost:8080/
🔧 Admin Panel:  http://localhost:8080/admin/
📸 Screenshots
🏠 Home Page
Main landing page with product categories, featured products, and search functionality.

Home Page

🔐 Login & Registration
<table> <tr> <td><b>Login Page</b></td> <td><b>Register Page</b></td> </tr> <tr> <td><img src="docs/screenshots/login.png" alt="Login"/></td> <td><img src="docs/screenshots/register.png" alt="Register"/></td> </tr> </table>
🛍️ Product Pages
<table> <tr> <td><b>Product Listing</b></td> <td><b>Product Details</b></td> </tr> <tr> <td><img src="docs/screenshots/products.png" alt="Products"/></td> <td><img src="docs/screenshots/product-details.png" alt="Product Details"/></td> </tr> </table>
🛒 Shopping Cart & Checkout
<table> <tr> <td><b>Cart Page</b></td> <td><b>Checkout Page</b></td> </tr> <tr> <td><img src="docs/screenshots/cart.png" alt="Cart"/></td> <td><img src="docs/screenshots/checkout.png" alt="Checkout"/></td> </tr> </table>
📦 My Orders
Track order status and view order history.

My Orders

🔧 Admin Panel
<table> <tr> <td><b>Admin Dashboard</b></td> <td><b>Product Management</b></td> </tr> <tr> <td><img src="docs/screenshots/admin-dashboard.png" alt="Admin Dashboard"/></td> <td><img src="docs/screenshots/admin-products.png" alt="Product Management"/></td> </tr> </table><table> <tr> <td><b>Category Management</b></td> <td><b>Order Management</b></td> </tr> <tr> <td><img src="docs/screenshots/admin-category.png" alt="Category Management"/></td> <td><img src="docs/screenshots/admin-orders.png" alt="Order Management"/></td> </tr> </table>
📊 Sales Reports & Analytics
Generate daily, monthly, quarterly, and yearly sales reports with top-selling products analysis.

Sales Report

💰 Refund Management
Admin can approve or reject refund requests.

Refund Management

📁 Project Structure
text

TrimurtiKiranaStores/
├── 📂 src/main/java/com/example/
│   ├── 📄 TrimurtiKiranaStoresApplication.java    # Main Application
│   │
│   ├── 📂 config/                                  # Security & Configuration
│   │   ├── SecurityConfig.java
│   │   ├── AuthSucessHandlerImpl.java
│   │   ├── AuthFailureHandlerImpl.java
│   │   ├── CustomUser.java
│   │   ├── UserDetailsServiceImpl.java
│   │   └── RazorpayConfig.java
│   │
│   ├── 📂 controller/                              # Request Handlers
│   │   ├── HomeController.java
│   │   ├── UserController.java
│   │   ├── AdminController.java
│   │   └── PaymentController.java
│   │
│   ├── 📂 model/                                   # Entity Classes
│   │   ├── UserDtls.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Cart.java
│   │   ├── Orders.java
│   │   ├── ProductOrder.java
│   │   ├── OrderAddress.java
│   │   └── RefundRequest.java
│   │
│   ├── 📂 dto/                                     # Data Transfer Objects
│   │   ├── SalesReportDTO.java
│   │   ├── DailySalesDTO.java
│   │   ├── ProductSalesDTO.java
│   │   └── RefundRequestDTO.java
│   │
│   ├── 📂 repository/                              # JPA Repositories
│   │   ├── UserRepository.java
│   │   ├── ProductRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── CartRepository.java
│   │   ├── OrdersRepository.java
│   │   ├── ProductOrderRepository.java
│   │   └── RefundRequestRepository.java
│   │
│   ├── 📂 service/                                 # Business Logic
│   │   ├── UserService.java
│   │   ├── ProductService.java
│   │   ├── CategoryService.java
│   │   ├── CartService.java
│   │   ├── OrderService.java
│   │   ├── RefundService.java
│   │   └── RazorpayService.java
│   │
│   └── 📂 util/                                    # Utilities
│       ├── AppConstant.java
│       ├── CommonUtil.java
│       └── OrderStatus.java
│
├── 📂 src/main/resources/
│   ├── 📄 application.properties                   # App Configuration
│   │
│   ├── 📂 static/
│   │   ├── 📂 css/                                 # Stylesheets
│   │   ├── 📂 js/                                  # JavaScript Files
│   │   └── 📂 img/                                 # Images & Assets
│   │
│   └── 📂 templates/                               # Thymeleaf Templates
│       ├── 📂 admin/                               # Admin Pages
│       └── 📂 user/                                # User Pages
│
└── 📄 pom.xml                                      # Maven Dependencies
🔗 API Endpoints
🌐 Public Endpoints
Method	Endpoint	Description
GET	/	Home Page
GET	/login	Login Page
GET	/register	Registration Page
GET	/products	Product Listing
GET	/product/{id}	Product Details
👤 User Endpoints (Authenticated)
Method	Endpoint	Description
GET	/user/	User Dashboard
GET	/user/cart	View Cart
POST	/user/addCart	Add to Cart
GET	/user/orders	My Orders
POST	/user/checkout	Place Order
🛠️ Admin Endpoints (Admin Role)
Method	Endpoint	Description
GET	/admin/	Admin Dashboard
GET	/admin/products	Manage Products
POST	/admin/saveProduct	Add/Edit Product
GET	/admin/category	Manage Categories
GET	/admin/orders	Manage Orders
GET	/admin/users	Manage Users
GET	/admin/sales-report	View Sales Report
🎨 User Flow Diagrams
Customer Journey
text

┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  Browse  │ -> │   Add    │ -> │   View   │ -> │ Checkout │ -> │  Track   │
│ Products │    │ to Cart  │    │   Cart   │    │ & Pay    │    │  Order   │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
Admin Workflow
text

┌──────────┐    ┌──────────────┐    ┌───────────────┐
│  Login   │ -> │  Dashboard   │ -> │ Manage Items  │
│          │    │  Overview    │    │ & Orders      │
└──────────┘    └──────────────┘    └───────────────┘
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
    ┌──────────┐ ┌──────────┐ ┌──────────┐
    │ Products │ │  Orders  │ │  Reports │
    │ & Categ. │ │ & Refund │ │ & Sales  │
    └──────────┘ └──────────┘ └──────────┘
🤝 Contributing
Contributions are welcome! Feel free to:

🍴 Fork the repository
🌿 Create a feature branch (git checkout -b feature/AmazingFeature)
💾 Commit changes (git commit -m 'Add AmazingFeature')
📤 Push to branch (git push origin feature/AmazingFeature)
🔃 Open a Pull Request
📞 Contact
Your Name

LinkedIn
GitHub
Email

📄 License
This project is developed for educational purposes.
Feel free to use it as a reference for learning and building similar applications.

<div align="center">
⭐ If you found this project helpful, please give it a star!
Made with ❤️ using Spring Boot

</div>
📝 How to Add Screenshots
Create a docs/screenshots/ folder in your repository and add your screenshots with these names:

text

docs/
└── screenshots/
    ├── home.png
    ├── login.png
    ├── register.png
    ├── products.png
    ├── product-details.png
    ├── cart.png
    ├── checkout.png
    ├── my-orders.png
    ├── admin-dashboard.png
    ├── admin-products.png
    ├── admin-category.png
    ├── admin-orders.png
    ├── sales-report.png
    └── refunds.png
