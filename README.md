# GymAppQlik
 Gym4U is a simple Spring Boot web application that allows users to manage gym trainers and book appointments with them. It features role-based authentication with Spring Security, CRUD operations for trainers, and appointment booking functionality for users
 ![Main Cosplay](https://github.com/malhibilal/GymAppQlik/blob/375796d46beb2251f64a820bc48d35f3214797ce/imageforReadmeFile/main%20cosplay.jpg)


# Features
- User and Admin roles with different privileges can sign up and login 
- Admin can add, update, and delete trainers, along with uploading their picture
- Users can view trainers, book appointments, and see their own appointments
![Sign in](https://github.com/malhibilal/GymAppQlik/blob/1e68d47c714eca2a7b259d35768bcee293c68087/imageforReadmeFile/signin.jpg)
 
# Technologies used
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL
- Hibernate
- Maven
# Getting Started
In order to run the project we need the following
- Java Development Kit (JDK)
- Apache Maven
- IDE: IntelliJ/NetBeans/Eclipse
- MySQL
# Configuration
- Project is initialized with the help of spring initializr. All the relevant dependencies which are necessary to run the project are added in the pom.xml file.
# Database
Following configuration will connect the project with the MYSQL database.
- spring.datasource.url=jdbc:mysql://localhost:3306/Gym4U
- spring.datasource.username=root
- spring.datasource.password=root
- spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
- spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=true
# Usage
- **Admin**
  - Access the admin panel by visiting /admin and logging in with admin credentials
  - Add trainers, upload their pictures, update, or delete existing trainers
  - update its existing profile and in settings can change its password
   ![Adding a trainer](https://github.com/malhibilal/GymAppQlik/blob/1e68d47c714eca2a7b259d35768bcee293c68087/imageforReadmeFile/add%20trainers.jpg)
- **User**
  - Access the user interface by visiting /user and logging in as a user
  - View trainers and book appointments with them
  - View your booked appointments.
  - Calculate BMI and get the message from the gym regarding diet and exercise
  - update profile and can change password
# Images
- Images of trainers should be stored in a specific directory (e.g., src/main/resources/static/images/trainers).
- Update the database to store the image file path.


