# HealthCare Plus - Web-Based Health Management System

A comprehensive web application where users can manage their health records digitally – a digital health diary combined with appointment booking and prescription management.

## 🏗️ Project Structure

```
ClinicManagementSystem/
├── src/
│   └── java/
│       └── com/healthcare/
│           ├── controller/          # Servlets (Controllers)
│           ├── dao/               # Data Access Objects
│           ├── model/             # Java Models
│           ├── util/              # Utility Classes
│           └── filter/            # Servlet Filters
├── web/
│   ├── WEB-INF/
│   │   ├── views/            # JSP Views
│   │   │   ├── patient/
│   │   │   ├── doctor/
│   │   │   └── admin/
│   │   └── web.xml         # Web Configuration
│   └── *.jsp                # Public JSP Pages
├── database/
│   └── healthcare_plus_schema.sql  # Database Schema
└── build.xml                # Ant Build File
```

## 🛠️ Technology Stack

- **Backend**: Java 24, Servlets, JDBC
- **Frontend**: JSP, HTML/CSS/JavaScript, Bootstrap 5
- **Database**: MySQL
- **Server**: Apache Tomcat
- **Build Tool**: Apache Ant
- **IDE**: NetBeans

## 🗄️ Database Schema

The system uses 8 core tables:

1. **users** - Authentication and user management
2. **patients** - Patient-specific data
3. **doctors** - Doctor-specific data
4. **vitals** - Health metrics tracking
5. **medications** - Patient medications
6. **appointments** - Appointment scheduling
7. **prescriptions** - Prescription management
8. **prescription_medicines** - Medicines within prescriptions

## 🚀 Getting Started

### Prerequisites

1. **Java Development Kit (JDK) 24**
2. **Apache Tomcat 9+**
3. **MySQL Server 8.0+**
4. **Apache Ant** (for building)
5. **NetBeans IDE** (recommended)

### Database Setup

1. Create MySQL database named `healthcare_plus`
2. Execute the SQL script: `database/healthcare_plus_schema.sql`
3. Update database connection in `src/java/com/healthcare/util/DatabaseUtil.java`

### Application Setup

1. Open the project in NetBeans
2. Configure Tomcat server in NetBeans
3. Update database credentials if needed
4. Run the project using NetBeans or Ant: `ant clean dist`

## 📋 Features Implemented

### ✅ Completed Features

#### Authentication System
- User registration with role-based profiles (Patient, Doctor, Admin)
- Secure login with session management
- Remember me functionality (7-day cookies)
- Session timeout handling (30 minutes)
- Logout with session cleanup

#### Core Infrastructure
- MVC Architecture with proper separation of concerns
- Database connectivity with connection pooling
- Character encoding filter for UTF-8 support
- Security headers implementation
- Error handling with custom error pages

#### Dashboard Systems
- **Patient Dashboard**: Vital signs overview, medications, appointments
- **Doctor Dashboard**: Today's appointments, patient management
- **Admin Dashboard**: System health, user management, statistics

#### Data Models & DAOs
- Complete CRUD operations for all entities
- Type-safe database interactions
- Connection management and resource cleanup

### 🚧 In Progress

#### Patient Module
- Vital signs logging with charts
- Medication management
- Appointment booking system
- Medical reports upload

#### Doctor Module
- Appointment management
- Patient history viewing
- Prescription system
- Availability settings

#### Admin Module
- User management interface
- Doctor management
- System reports
- Configuration management

### 📅 Planned Features

- Session timeout warnings
- PDF generation for prescriptions
- Email notifications
- Advanced analytics and reporting
- Mobile-responsive design improvements
- Health tips and recommendations

## 🔧 Configuration

### Database Connection
Update connection details in `DatabaseUtil.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/healthcare_plus";
private static final String USERNAME = "root";
private static final String PASSWORD = "";
```

### Session Configuration
Session settings in `web.xml`:
- Timeout: 30 minutes
- Cookie security: HttpOnly enabled
- Character encoding: UTF-8

## 🛡️ Security Features

- Password hashing (to be implemented)
- SQL injection prevention through prepared statements
- XSS protection headers
- Session fixation protection
- Role-based access control

## 📊 System Architecture

### MVC Pattern
- **Models**: Java POJOs representing database entities
- **Views**: JSP pages with Bootstrap styling
- **Controllers**: Servlets handling HTTP requests

### Database Layer
- **DAO Pattern**: Centralized data access
- **Connection Management**: Proper resource handling
- **Transaction Support**: Data consistency

### Security Layer
- **Authentication Filter**: Session validation
- **Authorization**: Role-based access control
- **Encoding Filter**: Character set management

## 🧪 Testing

### Build Verification
```bash
# Using NetBeans
Right-click project → Clean and Build

# Using Ant (if available)
ant clean dist
```

### Database Testing
```sql
-- Test database connection
SELECT COUNT(*) FROM users;
```

## 🚀 Deployment

### Local Deployment
1. Build the WAR file: `ant dist`
2. Deploy to Tomcat webapps directory
3. Start Tomcat server
4. Access at: `http://localhost:8080/ClinicManagementSystem/`

### Production Deployment
1. Configure production database
2. Update connection strings
3. Set production environment variables
4. Deploy WAR to application server

## 🤝 Contributing

1. Follow Java coding conventions
2. Use prepared statements for database queries
3. Implement proper error handling
4. Add appropriate logging
5. Test thoroughly before deployment

## 📄 License

This project is for educational purposes. Please ensure compliance with healthcare data regulations (HIPAA, GDPR) in production use.

## 🆘 Support

For issues and questions:
1. Check database connectivity
2. Verify Java version compatibility
3. Review Tomcat logs
4. Validate SQL schema

---

**Note**: This is a comprehensive healthcare management system designed for educational purposes. In production environments, ensure compliance with healthcare data protection regulations and implement additional security measures.
