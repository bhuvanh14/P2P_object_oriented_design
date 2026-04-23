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
- **Single Responsibility (concept used: responsibility partitioning by layer):**
  - Controllers handle transport concerns only (routing, request params, view names).
  - Services handle use-case rules (validation, policy checks, entity updates).
  - Repositories handle persistence only.
- **Open/Closed (concept used: extension via polymorphism):**
  - Booking checks are not hardcoded in one `if` chain in controllers.
  - New booking behavior is added by introducing a new `BookingRule` implementation without modifying controller flow.
- **Liskov Substitution (concept used: substitutable service contracts):**
  - Any implementation of `BookingService`, `AuthService`, `SlotService`, etc., can be injected where the interface is expected.
  - Example: `LoggingBookingService` and `CoreBookingService` both satisfy `BookingService`.
- **Interface Segregation (concept used: role-focused contracts):**
  - Instead of one large "God service", interfaces are split by use case (`AuthService`, `TutorProfileService`, `DashboardService`, `SlotService`, `BookingService`).
  - Each consumer depends only on methods it actually needs.
- **Dependency Inversion (concept used: depend on abstractions):**
  - Controllers depend on interfaces, while Spring resolves concrete implementations through DI.
  - This reduces coupling to implementation details and improves replaceability/testability.

### GRASP
- **Controller (concept used: system operation handler):**
  - Web controllers (`*Controller`) accept user actions as system events and delegate to services.
  - They avoid owning domain decisions.
- **Information Expert (concept used: assign behavior to the class with required information):**
  - Booking validation is placed in booking rules/services that know slot/learner constraints.
  - Slot-creation validation is placed in slot service where time/date/overlap information is processed.
- **Low Coupling (concept used: minimize direct dependencies):**
  - Controllers are coupled to service interfaces, not repository internals.
  - Cross-cutting concerns are composed (decorator) instead of mixed into core logic.
- **High Cohesion (concept used: keep related responsibilities together):**
  - Auth logic in `service/auth`, booking logic in `service/booking`, slot logic in `service/slot`, etc.
- **Indirection (concept used: mediator layer between UI and persistence):**
  - Service interfaces and implementations sit between controller and repository to stabilize dependencies.

### Required GoF Patterns Implemented

1. **Creational - Factory Method**
   - `service/slot/SlotFactory.java`
   - `service/slot/DefaultSlotFactory.java`
   - used by `service/slot/SlotServiceImpl.java`
   - **Concept used:** encapsulate object construction so slot setup rules are centralized and reusable.

2. **Behavioral - Strategy**
   - `service/booking/rule/BookingRule.java`
   - `service/booking/rule/AlreadyBookedRule.java`
   - `service/booking/rule/FutureSlotRule.java`
   - `service/booking/rule/SelfBookingRule.java`
   - orchestrated by `service/booking/CoreBookingService.java`
   - **Concept used:** interchangeable validation algorithms selected via polymorphic rule objects.

3. **Structural - Decorator**
   - `service/booking/BookingService.java` (component)
   - `service/booking/CoreBookingService.java` (core component)
   - `service/booking/LoggingBookingService.java` (decorator)
   - **Concept used:** add behavior (logging) around booking operations without changing core booking code.

### 5.1) Where Exactly They Are Used

#### MVC (Model-View-Controller)

**How MVC is used in this project:**
- Request comes to a controller method (`@GetMapping`/`@PostMapping`).
- Controller delegates business work to a service.
- Service fetches/updates model entities via repositories.
- Controller places results into `Model` and returns a Thymeleaf view name.
- Thymeleaf templates render the response.

- **Model (domain + persistence entities):**
  - `src/main/java/com/p2p/tutoring/model/User.java`
  - `src/main/java/com/p2p/tutoring/model/Slot.java`
  - `src/main/java/com/p2p/tutoring/model/StudentRating.java`
- **View (Thymeleaf templates):**
  - `src/main/resources/templates/home.html`
  - `src/main/resources/templates/register1.html`
  - `src/main/resources/templates/login.html`
  - `src/main/resources/templates/dashboard.html`
  - `src/main/resources/templates/become_tutor.html`
  - `src/main/resources/templates/add_slot.html`
  - `src/main/resources/templates/view_slots.html`
  - `src/main/resources/templates/book_confirm.html`
  - `src/main/resources/templates/book_error.html`
- **Controller (request handlers):**
  - `src/main/java/com/p2p/tutoring/controller/HomeController.java`
  - `src/main/java/com/p2p/tutoring/controller/AuthController.java`
  - `src/main/java/com/p2p/tutoring/controller/DashboardController.java`
  - `src/main/java/com/p2p/tutoring/controller/TutorController.java`
  - `src/main/java/com/p2p/tutoring/controller/SlotController.java`
  - `src/main/java/com/p2p/tutoring/controller/BookingController.java`

#### GRASP (with concrete locations)

- **Controller pattern:** MVC controllers receive system events and delegate.
  - `src/main/java/com/p2p/tutoring/controller/*.java`
  - Concept in practice: no repository-heavy business decisions in controller methods.
- **Information Expert:** booking and slot rules handled by services/rules owning that knowledge.
  - `src/main/java/com/p2p/tutoring/service/booking/CoreBookingService.java`
  - `src/main/java/com/p2p/tutoring/service/booking/rule/*.java`
  - `src/main/java/com/p2p/tutoring/service/slot/SlotServiceImpl.java`
  - Concept in practice: rule objects validate slot state, time window, and ownership constraints.
- **Low Coupling:** controllers depend on service abstractions instead of repositories directly.
  - `src/main/java/com/p2p/tutoring/controller/AuthController.java`
  - `src/main/java/com/p2p/tutoring/controller/BookingController.java`
  - `src/main/java/com/p2p/tutoring/controller/SlotController.java`
  - Concept in practice: easier to swap service internals without changing endpoints.
- **High Cohesion:** services are grouped by use case.
  - `src/main/java/com/p2p/tutoring/service/auth/*`
  - `src/main/java/com/p2p/tutoring/service/profile/*`
  - `src/main/java/com/p2p/tutoring/service/dashboard/*`
  - `src/main/java/com/p2p/tutoring/service/slot/*`
  - `src/main/java/com/p2p/tutoring/service/booking/*`
  - Concept in practice: each package has one business theme and one reason to change.
- **Indirection:** service interfaces isolate controllers from implementation details.
  - `src/main/java/com/p2p/tutoring/service/auth/AuthService.java`
  - `src/main/java/com/p2p/tutoring/service/slot/SlotService.java`
  - `src/main/java/com/p2p/tutoring/service/booking/BookingService.java`
  - `src/main/java/com/p2p/tutoring/service/profile/TutorProfileService.java`
  - `src/main/java/com/p2p/tutoring/service/dashboard/DashboardService.java`
  - Concept in practice: controllers remain stable even when service internals evolve.

#### SOLID (with concrete locations)

- **S - Single Responsibility Principle:**
  - Controllers: `src/main/java/com/p2p/tutoring/controller/*.java` (HTTP + model binding)
  - Business logic: `src/main/java/com/p2p/tutoring/service/**/*.java`
  - Concept in practice: changing booking policy usually touches service/rule files, not controller routing.
- **O - Open/Closed Principle:**
  - Add booking rules by adding a new class implementing `BookingRule`.
  - Existing extension point: `src/main/java/com/p2p/tutoring/service/booking/rule/BookingRule.java`
  - Concept in practice: feature growth by addition, not modification.
- **L - Liskov Substitution Principle:**
  - Interface-based substitution:
    - `AuthService` -> `AuthServiceImpl`
    - `SlotService` -> `SlotServiceImpl`
    - `BookingService` -> `CoreBookingService` and `LoggingBookingService`
    - `DashboardService` -> `DashboardServiceImpl`
    - `TutorProfileService` -> `TutorProfileServiceImpl`
  - Concept in practice: controller code does not change when implementation changes.
- **I - Interface Segregation Principle:**
  - Small focused interfaces:
    - `src/main/java/com/p2p/tutoring/service/auth/AuthService.java`
    - `src/main/java/com/p2p/tutoring/service/slot/SlotService.java`
    - `src/main/java/com/p2p/tutoring/service/booking/BookingService.java`
    - `src/main/java/com/p2p/tutoring/service/profile/TutorProfileService.java`
    - `src/main/java/com/p2p/tutoring/service/dashboard/DashboardService.java`
  - Concept in practice: controllers depend on narrowly scoped API surface.
- **D - Dependency Inversion Principle:**
  - Controllers consume interfaces, not concrete implementations.
  - Service wiring is provided by Spring DI (`@Service`, constructor injection).
  - Concept in practice: dependency graph is configured by container, not manual `new` in controllers.

#### GoF Patterns (with concrete locations)

- **Creational - Factory Method:**
  - `src/main/java/com/p2p/tutoring/service/slot/SlotFactory.java`
  - `src/main/java/com/p2p/tutoring/service/slot/DefaultSlotFactory.java`
  - Used in `src/main/java/com/p2p/tutoring/service/slot/SlotServiceImpl.java`
  - Concept in practice: slot defaults and initialization are created in one place.
- **Behavioral - Strategy:**
  - Strategy interface: `src/main/java/com/p2p/tutoring/service/booking/rule/BookingRule.java`
  - Concrete strategies:
    - `src/main/java/com/p2p/tutoring/service/booking/rule/AlreadyBookedRule.java`
    - `src/main/java/com/p2p/tutoring/service/booking/rule/FutureSlotRule.java`
    - `src/main/java/com/p2p/tutoring/service/booking/rule/SelfBookingRule.java`
  - Strategy consumer: `src/main/java/com/p2p/tutoring/service/booking/CoreBookingService.java`
  - Concept in practice: each validation concern is isolated and independently testable.
- **Structural - Decorator:**
  - Component interface: `src/main/java/com/p2p/tutoring/service/booking/BookingService.java`
  - Core component: `src/main/java/com/p2p/tutoring/service/booking/CoreBookingService.java`
  - Decorator: `src/main/java/com/p2p/tutoring/service/booking/LoggingBookingService.java`
  - Concept in practice: adds logging as a wrapper instead of polluting core business logic.

#### Additional Design Principles Applied

- **Separation of Concerns:**
  - Web concerns: `controller`
  - Domain/use-case concerns: `service`
  - Persistence concerns: `repository`
  - Presentation concerns: `templates` + `static`
- **DRY (Don't Repeat Yourself):**
  - Shared success/failure response handling via `src/main/java/com/p2p/tutoring/service/common/ServiceResult.java`
  - Shared session user retrieval via `src/main/java/com/p2p/tutoring/service/SessionUserService.java`
- **Composition over Inheritance:**
  - Rule composition in `CoreBookingService` with injected `List<BookingRule>`
  - Decorator composition in `LoggingBookingService` with `BookingService delegate`

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
