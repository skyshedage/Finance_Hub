# Finance Hub

Status: Tested repair source on repair/security-and-project-docs; not merged or deployed

Finance Hub is a full-stack savings-plan management application. It brings together account registration, savings-plan browsing, plan applications, enquiries, feedback and an administrator dashboard. The repository contains an Angular frontend and a Java/Spring Boot backend.

## What the project implements

The user-facing workflow starts with registration and login. After signing in, a user can browse savings plans, submit an application, view applied plans, raise enquiries and submit feedback. Separate administrator routes provide screens for creating and editing savings plans, reviewing applications, responding to enquiries and viewing feedback.

The dashboard backend exposes statistics for application status and monthly trends, enquiry status, user growth, savings-plan popularity and recent activity. These are application-management features, not bank connectivity, payment processing or investment execution.

## How the technologies are used

### Angular and TypeScript

Components build the registration, login, savings-plan, application, enquiry and dashboard screens. Angular Router maps user and administrator journeys.

### Angular HttpClient

Services send registration, login and business requests to the backend REST endpoints. A token interceptor attaches authentication information.

### Angular route guards

Route metadata distinguishes USER and ADMIN screens. Frontend guards are a navigation aid; backend authorization must still enforce access.

### Java 17 and Spring Boot 3.2.9

REST controllers receive requests and pass business operations to services and repositories.

### Spring Security and JWT

Passwords are hashed with BCrypt. Login issues a JWT. A request filter reads authentication information and method-level annotations restrict selected operations.

### Spring Data JPA and Hibernate

Repository interfaces and entity relationships model users, plans, applications, enquiries and feedback.

### MySQL connector

MySQL stores application data. The repair branch reads connection settings from environment variables rather than bundling database credentials. This repair branch adds Flyway migrations for empty schemas.

### Chart.js and ng2-charts

The Angular administrator dashboard displays application-management statistics.

## Request flow

- A user submits a form in an Angular component.
- A frontend service sends an HTTP request to the relevant API endpoint.
- Spring Security authenticates protected requests.
- A controller calls a service, which applies business logic and uses a JPA repository.
- The backend returns data for the Angular view to display.

The project separates HTTP handling, service logic, database access and entity models. This makes those responsibilities easier to inspect and test independently.

## Repository layout

- `angularapp/`: Angular application, components, services, route guards and dashboard.
- `springapp/`: Spring Boot application, Maven wrapper, controllers, services, repositories, entities and backend tests.
- `.github/workflows/`: existing automation configuration.

## Development setup and current limits

The published repair branch contains conventional Angular and Maven folders, relative /api requests with a development proxy, and environment-only database and JWT settings. Use Java 17, MySQL 8 and Node 22 for the tested local setup. Finance retains Angular 10 and needs the OpenSSL legacy compatibility option. It is not a modern frontend upgrade.

This repair branch adds Flyway versioned migrations for an empty MySQL database, preserves Hibernate schema validation and refuses to silently baseline an existing unversioned database. Fresh MySQL 8 startup, API workflows and restart against the migrated schema passed locally. Existing databases require a backup and an explicit migration plan; this work does not alter a deployed database.

## Repair work, separate from the original implementation

On 20 November 2026 at 20:56 CET, the broader authentication, role, ownership, logging, password-response and environment-configuration repairs were pushed to repair/security-and-project-docs. The published commit is 2b323ee70886031cdf4511726729e2ac7ee19e46. The default master branch is unchanged. These changes are not merged or deployed.

The follow-up repair batch was checked locally: 38 backend tests pass in an isolated H2 database, and three workflow tests plus a schema-restart check pass against disposable MySQL 8. The Angular production build passes. An actual browser journey verifies login, a user application with a PNG proof image, authenticated admin image viewing and approval, and the user seeing the approved status. A test fixture supplies local accounts and a plan, not production users.

Proof images are validated, decoded and re-encoded by the backend, stored with the application and served only to the owner or an administrator. New input checks and corrected phone layouts accompany this work. No real financial transactions, deployed service, complete security audit or bank connectivity are established by these tests.

A manual-only CI replacement is included on this repair branch. It runs isolated backend tests and a frontend build without the legacy scanner credential or external callback. It has not been run on GitHub. No credential rotation or history rewrite has occurred.

This repair branch replaces unsupported homepage investment, high-yield, loan, best-rate and advice claims with the actual savings-plan application workflow. It removes the unverified support address and labels original project credits as source attribution, not independently verified contributions. The hero contrast and credit navigation were checked at 320, 390 and 1280 pixels. These homepage changes are included on this repair branch.

## Local setup

See [development setup](docs/LOCAL_SETUP.md) for commands, environment variables and database migration notes.
