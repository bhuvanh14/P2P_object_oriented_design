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
- `src/main/java/com/p2p/tutoring/controller` - route handlers
- `src/main/java/com/p2p/tutoring/service` - session user helper
- `src/main/resources/templates` - Thymeleaf pages
- `src/main/resources/static` - CSS

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
