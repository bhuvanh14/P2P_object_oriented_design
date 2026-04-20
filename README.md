# Tutoring Scheduler (Peer-to-Peer) - Java Version

This is a ready-to-run Java Spring Boot implementation of the peer-to-peer tutoring scheduler.

## Tech Stack
- Java 17
- Spring Boot 3
- Spring MVC + Thymeleaf
- Spring Data JPA
- H2 database (file-based)

## Implemented Features
- User registration with:
  - required `name`, `email`, `password`
  - email format validation
  - duplicate email prevention
- User login/logout with session-based auth
- Dashboard for logged-in users
- Become tutor flow (`subjects`, `contact`)
- Publish tutor availability slots with overlap prevention
- Browse unbooked future slots
- Book slot flow with:
  - self-booking prevention
  - booked-status update and learner assignment

## Route Map
- `/` - home
- `/register` - register (GET/POST)
- `/login` - login (GET/POST)
- `/logout` - logout
- `/dashboard` - dashboard (login required)
- `/become_tutor` - tutor onboarding (login required)
- `/add_slot` - add availability slot (login required)
- `/view_slots` - browse open slots
- `/book/{slotId}` - booking confirm + submit (login required)

## Project Structure
- `src/main/java/com/p2p/tutoring/model` - entities (`User`, `Slot`, `StudentRating`)
- `src/main/java/com/p2p/tutoring/repository` - JPA repositories
- `src/main/java/com/p2p/tutoring/controller` - thin MVC controllers (HTTP only)
- `src/main/java/com/p2p/tutoring/service` - application services and design pattern implementations
  - `service/auth` - authentication use case service
  - `service/profile` - tutor profile use case service
  - `service/dashboard` - dashboard composition service
  - `service/slot` - slot use cases + slot factory
  - `service/booking` - booking use cases + decorator
  - `service/booking/rule` - booking rule strategies
  - `service/common` - shared service result model
- `src/main/resources/templates` - Thymeleaf pages
- `src/main/resources/static` - CSS

## SOLID and GRASP Applied

### SOLID
- **S (Single Responsibility):** Controllers now handle request/response only, while business rules are moved into services.
  - Controllers: `controller/*Controller.java`
  - Business logic: `service/auth/AuthServiceImpl.java`, `service/slot/SlotServiceImpl.java`, `service/booking/CoreBookingService.java`, `service/profile/TutorProfileServiceImpl.java`
- **O (Open/Closed):** Booking validation is extensible via independent rules.
  - Add a new booking constraint by creating another `BookingRule` implementation in `service/booking/rule`.
- **L (Liskov Substitution):** Service implementations are substitutable through interfaces.
  - `AuthService`, `SlotService`, `BookingService`, `DashboardService`, `TutorProfileService`
- **I (Interface Segregation):** Use-case-specific interfaces avoid forcing consumers to depend on unrelated methods.
  - Example: `BookingService` only exposes booking operations.
- **D (Dependency Inversion):** Controllers depend on abstractions (interfaces), not concrete repository-heavy implementations.
  - Example: `AuthController` depends on `AuthService`, `BookingController` depends on `BookingService`.

### GRASP
- **Controller:** MVC controllers receive system events and delegate to use-case services.
- **Low Coupling / High Cohesion:** Logic is grouped by domain capability (auth, booking, slot, dashboard, profile).
- **Indirection:** Service interfaces introduce a stable seam between controllers and data access.
- **Information Expert:** Booking and slot-specific rules are owned by dedicated services and rule objects.

## Design Patterns Used (Where)

### 1) Creational Pattern: Factory Method
- **Pattern:** Factory Method
- **Where:**
  - `service/slot/SlotFactory.java`
  - `service/slot/DefaultSlotFactory.java`
  - used by `service/slot/SlotServiceImpl.java`
- **Why:** Slot object creation is centralized so construction rules are not duplicated in controllers/services.

### 2) Behavioral Pattern: Strategy (with ordered rule chain)
- **Pattern:** Strategy
- **Where:**
  - strategy interface: `service/booking/rule/BookingRule.java`
  - concrete strategies: `AlreadyBookedRule.java`, `FutureSlotRule.java`, `SelfBookingRule.java`
  - orchestrator: `service/booking/CoreBookingService.java`
- **Why:** Booking constraints are modular and extensible; new behavior is added by a new strategy class.

### 3) Structural Pattern: Decorator
- **Pattern:** Decorator
- **Where:**
  - component interface: `service/booking/BookingService.java`
  - core component: `service/booking/CoreBookingService.java`
  - decorator: `service/booking/LoggingBookingService.java`
- **Why:** Adds cross-cutting logging behavior around booking operations without changing core booking logic.

## Run Instructions
1. Ensure Java 17 is installed.
2. From the project root, run with the Maven Wrapper (no global Maven required):

```powershell
.\mvnw.cmd spring-boot:run
```

3. If you have Maven installed globally, you can also run:

```bash
mvn spring-boot:run
```

4. Open:
- App: `http://localhost:8080/`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/tutoringdb;AUTO_SERVER=TRUE`
  - User: `sa`
  - Password: (leave empty)

## Notes for OOAD Submission
- `StudentRating` model exists for future extension (tutor rating learner) as requested, but no UI flow is wired yet.
- Passwords are currently stored in plain text for simplicity in academic/demo usage. For production, use BCrypt hashing and Spring Security.
