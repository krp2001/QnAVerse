# 🌟 QnAverse

A modern Q&A platform built with Spring Boot that enables users to ask questions, provide answers, and engage in meaningful discussions within a secure and user-friendly environment.

## 🚀 Features

- **User Authentication & Authorization** - Secure login system with Spring Security
- **Question Management** - Create, view, and manage questions
- **Answer System** - Comprehensive answering capabilities
- **Email Integration** - Automated email notifications
- **Responsive Design** - Mobile-friendly JSP-based frontend
- **Role-based Access Control** - Different user permissions and roles
- **Data Validation** - Server-side input validation

## 🛠️ Technology Stack

### Backend
- **Java 21** - Latest LTS version
- **Spring Boot 3.4.4** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Web MVC** - Web layer
- **Hibernate ORM** - Object-relational mapping
- **Spring Mail** - Email functionality

### Frontend
- **JSP** - Server-side rendering
- **JSTL** - JSP Standard Tag Library
- **HTML5/CSS3** - Modern web standards
- **Bootstrap** - Responsive design framework

### Database
- **MySQL** - Primary database
- **Spring ORM** - Database abstraction

### Build & Development
- **Maven** - Dependency management
- **Apache Tomcat** - Embedded server
- **Eclipse IDE** - Development environment

## 📋 Prerequisites

Before running this application, make sure you have:

- **Java 21** or higher installed
- **MySQL 8.0+** database server
- **Maven 3.6+** for dependency management
- **Git** for version control

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/krp2001/QnAVerse.git
cd QnAVerse
```

### 2. Database Setup
Create a MySQL database:
```sql
CREATE DATABASE qnaverse_db;
CREATE USER 'qnaverse_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON qnaverse_db.* TO 'qnaverse_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Application Properties
Update `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/qnaverse_db
spring.datasource.username=qnaverse_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Email Configuration (Update with your credentials)
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 4. Build and Run
```bash
# Using Maven Wrapper (Recommended)
./mvnw clean install
./mvnw spring-boot:run

# Or using system Maven
mvn clean install
mvn spring-boot:run
```

### 5. Access the Application
Open your browser and navigate to:
```
http://localhost:9090
```

## 🏗️ Project Structure

```
qnaverse/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/kathapatel/qnaverse/
│   │   │       ├── config/          # Security & app configuration
│   │   │       ├── controller/      # MVC controllers
│   │   │       ├── entity/          # JPA entities
│   │   │       ├── repository/      # Data access layer
│   │   │       ├── service/         # Business logic
│   │   │       └── QnaverseApplication.java
│   │   ├── resources/
│   │   │   └── application.properties
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── views/           # JSP templates
│   └── test/                        # Test files
├── target/                          # Build output
├── pom.xml                         # Maven configuration
└── README.md                       # Project documentation
```

## 🔧 Configuration

### Email Setup
The application uses Gmail SMTP for email notifications. To configure:

1. Enable 2-factor authentication on your Gmail account
2. Generate an app-specific password
3. Update the email credentials in `application.properties`

### Security Configuration
Spring Security is configured for:
- Form-based authentication
- Role-based authorization
- CSRF protection
- Session management

### Database Configuration
The application supports:
- Automatic schema generation with Hibernate
- Connection pooling
- Transaction management

## 🚦 API Endpoints

### Authentication
- `GET /login` - Login page
- `POST /login` - Process login
- `GET /register` - Registration page  
- `POST /register` - Process registration
- `POST /logout` - User logout

### Questions & Answers
- `GET /` - Home page with questions
- `GET /ask` - Ask question form
- `POST /questions` - Submit new question
- `GET /questions/{id}` - View specific question
- `POST /questions/{id}/answers` - Submit answer

## 🧪 Testing

Run the test suite:
```bash
./mvnw test
```

Run specific test classes:
```bash
./mvnw test -Dtest=ControllerTest
```

## 📦 Deployment

### Production Build
```bash
./mvnw clean package -Dmaven.test.skip=true
```

### Docker Deployment (Optional)
Create a `Dockerfile`:
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/qnaverse-0.0.1-SNAPSHOT.war app.war
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app.war"]
```

Build and run:
```bash
docker build -t qnaverse .
docker run -p 9090:9090 qnaverse
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 Development Guidelines

- Follow Java coding conventions
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages
- Ensure code passes all existing tests

## 🐛 Troubleshooting

### Common Issues

**Port 9090 already in use:**
```bash
# Change port in application.properties
server.port=8080
```

**Database connection failed:**
- Verify MySQL is running
- Check database credentials
- Ensure database exists

**Email not sending:**
- Verify Gmail app password
- Check firewall settings
- Ensure 2FA is enabled on Gmail

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Katha Patel**
- Email: kathapatel111@gmail.com
- LinkedIn: [kathapatel29](https://linkedin.com/in/kathapatel29)
- GitHub: [@krp2001](https://github.com/krp2001)

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Spring Security for robust authentication
- Hibernate for ORM capabilities
- Bootstrap for responsive design components

---

⭐ **Star this repository if you found it helpful!**

For questions or support, please open an issue or contact [kathapatel111@gmail.com](mailto:kathapatel111@gmail.com)
