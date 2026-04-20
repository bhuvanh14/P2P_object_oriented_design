# Tutoring Scheduler (Peer-to-Peer) - Java Version

Complete documentation for the tutoring scheduler codebase, including architecture, features, file-by-file structure, routes, data model, configuration, and extension points.

## 1) Project Overview

This is a Spring Boot web app where:
- students can register/login,
- users can become tutors,
- tutors can publish time slots,
- learners can browse and book future unbooked slots,
- users can track published and booked sessions on a dashboard.

The UI is server-rendered using Thymeleaf templates.

## 2) Tech Stack

- Java 17
- Spring Boot 3.3.4
- Spring MVC + Thymeleaf
- Spring Data JPA (Hibernate)
- H2 file-based database
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## 3) Implemented Features

- User registration:
  - required name/email/password,
  - email format validation,
  - duplicate email prevention.
- Session-based login/logout.
- Dashboard with account details and slot summaries.
- Become tutor flow (`subjects`, `contact`).
- Tutor slot creation with validation:
  - end time must be after start time,
  - slot must be in the future,
  - overlap with existing tutor slots is blocked.
- Browse available future slots.
- Booking flow with constraints:
  - slot must exist,
  - slot must be unbooked,
  - slot must be in the future,
  - tutor cannot book own slot.

## 4) Architecture at a Glance

Layered architecture:
- `controller`: HTTP endpoints and view navigation only.
- `service`: business use cases and domain rules.
- `repository`: persistence abstraction with Spring Data JPA.
- `model`: JPA entities.
- `templates/static`: view layer and styling.

Request flow example (booking):
1. `BookingController` receives request.
2. `BookingService` validates through booking rule strategies.
3. `CoreBookingService` updates entity and saves via `SlotRepository`.
4. `LoggingBookingService` decorates booking calls with logging.
5. Controller returns Thymeleaf view.

## 5) SOLID, GRASP, and Design Patterns

### SOLID
- **Single Responsibility:** controllers are thin; services hold business rules.
- **Open/Closed:** new booking constraints can be added by introducing new `BookingRule` implementations.
- **Liskov Substitution:** service implementations are substituted through service interfaces.
- **Interface Segregation:** use-case-specific interfaces (`AuthService`, `SlotService`, `BookingService`, etc.).
- **Dependency Inversion:** controllers depend on abstractions, not concrete repository-heavy implementations.

### GRASP
- **Controller:** MVC controllers receive system events.
- **Low Coupling / High Cohesion:** logic grouped by domain capabilities.
- **Indirection:** service interfaces decouple controllers from implementation details.
- **Information Expert:** booking and slot rules live in dedicated service/rule classes.

### Required GoF Patterns Implemented

1. **Creational - Factory Method**
   - `service/slot/SlotFactory.java`
   - `service/slot/DefaultSlotFactory.java`
   - used by `service/slot/SlotServiceImpl.java`

2. **Behavioral - Strategy**
   - `service/booking/rule/BookingRule.java`
   - `service/booking/rule/AlreadyBookedRule.java`
   - `service/booking/rule/FutureSlotRule.java`
   - `service/booking/rule/SelfBookingRule.java`
   - orchestrated by `service/booking/CoreBookingService.java`

3. **Structural - Decorator**
   - `service/booking/BookingService.java` (component)
   - `service/booking/CoreBookingService.java` (core component)
   - `service/booking/LoggingBookingService.java` (decorator)

## 6) Detailed File Structure (What Each File Does)

### Root

```text
p2p_java/
  README.md
  pom.xml
  mvnw
  mvnw.cmd
  data/
  src/
  target/
```

- `README.md`: project documentation.
- `pom.xml`: dependencies, Java version, Spring Boot plugin.
- `mvnw`, `mvnw.cmd`: Maven wrapper scripts.
- `data/`: H2 file database storage folder.
- `src/`: source code/resources.
- `target/`: generated build output (compiled classes/resources).

### Java Source (`src/main/java/com/p2p/tutoring`)

```text
src/main/java/com/p2p/tutoring/
  P2pSchedulerApplication.java
  controller/
    AuthController.java
    BookingController.java
    DashboardController.java
    HomeController.java
    SlotController.java
    TutorController.java
  model/
    Slot.java
    StudentRating.java
    User.java
  repository/
    SlotRepository.java
    StudentRatingRepository.java
    UserRepository.java
  service/
    SessionUserService.java
    auth/
      AuthService.java
      AuthServiceImpl.java
    booking/
      BookingService.java
      CoreBookingService.java
      LoggingBookingService.java
      rule/
        BookingRule.java
        AlreadyBookedRule.java
        FutureSlotRule.java
        SelfBookingRule.java
    common/
      ServiceResult.java
    dashboard/
      DashboardService.java
      DashboardServiceImpl.java
      DashboardView.java
    profile/
      TutorProfileService.java
      TutorProfileServiceImpl.java
    slot/
      SlotFactory.java
      DefaultSlotFactory.java
      SlotService.java
      SlotServiceImpl.java
```

#### App bootstrap
- `P2pSchedulerApplication.java`: Spring Boot entry point (`main`).

#### Controllers
- `HomeController.java`: serves home page route (`/`).
- `AuthController.java`: registration/login/logout endpoints and auth page navigation.
- `DashboardController.java`: dashboard route and model population via `DashboardService`.
- `TutorController.java`: become tutor GET/POST flow.
- `SlotController.java`: add slot form submission and view available slots.
- `BookingController.java`: booking confirmation and booking submit flow.

#### Models (Entities)
- `User.java`: user account entity; includes role, contact, subjects, timestamps.
- `Slot.java`: tutoring slot entity; links tutor/learner, schedule fields, booking flag.
- `StudentRating.java`: future extension entity for ratings/feedback.

#### Repositories
- `UserRepository.java`: user lookup and duplicate email checks.
- `SlotRepository.java`: slot query methods for dashboard/view/overlap use cases.
- `StudentRatingRepository.java`: basic CRUD repository for ratings.

#### Services
- `SessionUserService.java`: session state helper for login/logout/current user retrieval.

##### Auth service
- `AuthService.java`: auth use-case contract.
- `AuthServiceImpl.java`: registration/login validation and persistence logic.

##### Profile service
- `TutorProfileService.java`: tutor profile update contract.
- `TutorProfileServiceImpl.java`: validates and stores tutor subjects/contact/role.

##### Dashboard service
- `DashboardService.java`: dashboard assembly contract.
- `DashboardServiceImpl.java`: gathers tutor/learner slot lists for dashboard.
- `DashboardView.java`: immutable dashboard data transfer object (record).

##### Slot service
- `SlotService.java`: slot use-case contract (add/list).
- `SlotServiceImpl.java`: slot parsing, validation, overlap checks, persistence.
- `SlotFactory.java`: slot creation abstraction (Factory Method).
- `DefaultSlotFactory.java`: default slot construction implementation.

##### Booking service
- `BookingService.java`: booking use-case contract.
- `CoreBookingService.java`: core booking validation and transaction logic.
- `LoggingBookingService.java`: decorator adding logging around booking operations.
- `BookingRule.java`: booking rule strategy interface.
- `AlreadyBookedRule.java`: blocks already-booked slots.
- `FutureSlotRule.java`: blocks past/non-future slots.
- `SelfBookingRule.java`: blocks tutor self-booking.

##### Shared service model
- `ServiceResult.java`: reusable success/failure wrapper for service outcomes.

### Resources (`src/main/resources`)

```text
src/main/resources/
  application.properties
  static/
    styles.css
  templates/
    add_slot.html
    become_tutor.html
    book_confirm.html
    book_error.html
    dashboard.html
    home.html
    login.html
    register1.html
    view_slots.html
```

#### Configuration
- `application.properties`: app name, datasource, JPA config, H2 console, error message settings.

#### Static assets
- `static/styles.css`: global styles for all templates.

#### Thymeleaf templates
- `home.html`: landing page with navigation links.
- `register1.html`: registration form and error display.
- `login.html`: login form and status/error messages.
- `dashboard.html`: user summary and published/booked slot tables.
- `become_tutor.html`: tutor profile form.
- `add_slot.html`: slot creation form and feedback messages.
- `view_slots.html`: available slot listing and booking actions.
- `book_confirm.html`: booking confirmation page.
- `book_error.html`: error page for invalid booking attempts.

### Build Output (`target`)

Generated by Maven build lifecycle:
- compiled classes,
- copied resources/templates/static files,
- Maven compiler metadata.

Do not edit files in `target` manually.

## 7) Data Model (Domain)

### User
- Primary key: `id`
- Core fields: `name`, `email`, `password`, `role`
- Tutor profile fields: `subjects`, `contact`
- Auditing: `createdAt` (`@PrePersist`)

### Slot
- Primary key: `id`
- Relations:
  - `tutor` (`ManyToOne`, required)
  - `learner` (`ManyToOne`, optional)
- Schedule fields: `slotDate`, `startTime`, `endTime`
- Business fields: `subject`, `booked`
- Auditing: `createdAt` (`@PrePersist`)

### StudentRating
- Primary key: `id`
- Relations: `tutor`, `student`
- Fields: `rating`, `feedback`, `createdAt`
- Status: persisted entity present, no UI flow wired yet.

## 8) Endpoint Reference

- `GET /`: home page.
- `GET /register`: registration page.
- `POST /register`: create new user.
- `GET /login`: login page.
- `POST /login`: authenticate and set session.
- `GET /logout`: invalidate session.
- `GET /dashboard`: user dashboard (requires login).
- `GET /become_tutor`: tutor profile form (requires login).
- `POST /become_tutor`: save tutor profile data.
- `GET /add_slot`: slot creation form (requires login).
- `POST /add_slot`: validate and create slot.
- `GET /view_slots`: list unbooked future slots.
- `GET /book/{slotId}`: booking confirmation page.
- `POST /book/{slotId}`: finalize booking.

## 9) Configuration Reference (`application.properties`)

- `spring.application.name=tutoring-scheduler`
- `spring.datasource.url=jdbc:h2:file:./data/tutoringdb;AUTO_SERVER=TRUE`
- `spring.datasource.driverClassName=org.h2.Driver`
- `spring.datasource.username=sa`
- `spring.datasource.password=`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.show-sql=false`
- `spring.jpa.open-in-view=true`
- `spring.h2.console.enabled=true`
- `spring.h2.console.path=/h2-console`
- `server.error.include-message=always`

## 10) Run, Build, and Verify

1. Ensure Java 17 is installed.
2. Start app (Windows PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

3. Compile only:

```powershell
.\mvnw.cmd -DskipTests compile
```

4. Optional test run:

```powershell
.\mvnw.cmd test
```

5. Open in browser:
- App: `http://localhost:8080/`
- H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/tutoringdb;AUTO_SERVER=TRUE`
  - Username: `sa`
  - Password: empty

## 11) Security and Production Notes

- Passwords are currently plain text (demo/academic only).
- For production:
  - use password hashing (BCrypt),
  - add Spring Security with role-based authorization,
  - implement CSRF/session hardening,
  - add input constraints and centralized exception handling,
  - configure external production database.

## 12) Current Limitations and Next Extensions

- `StudentRating` entity is present, but no rating UI/service/controller flow yet.
- No pagination/filtering on slot listings.
- No automated test suite currently included.
- No cancellation/reschedule workflow yet.

Suggested extensions:
- add rating workflows and average tutor rating display,
- add search/filter by subject/date,
- add booking cancellation and audit history,
- add unit and integration tests for service layer.
