# Local setup and verification

This setup belongs to the new maintenance batch. It is not a deployment.

## Backend

Use Java 17, Maven 3.9 and an empty MySQL 8 database named finance_hub. Set `FINANCE_DB_URL`, `FINANCE_DB_USER`, `FINANCE_DB_PASSWORD` and `FINANCE_JWT_SECRET` in your shell or a local secret manager. The signing key must have at least 32 bytes. Do not commit secrets. A `.env` file is not loaded automatically.

Run `cd springapp && mvn spring-boot:run`. The API defaults to port 8080. Flyway creates the schema in an empty database and Hibernate validates it. Migration SQL is in `src/main/resources/db/migration/mysql/`. Existing non-empty unversioned databases intentionally fail rather than being silently baselined. Back up an existing database and review its migration separately. Do not use destructive schema reset settings against existing data.

Run `cd springapp && mvn test` for isolated H2 tests. Tests use local fixtures; they do not need production credentials or database access. Fresh migration, principal user/admin API workflows and restart validation have additionally been checked against disposable MySQL 8 locally.

Registration only creates USER accounts. There is no public admin-registration endpoint or seeded default admin. For local admin demonstration, promote only a disposable test account through your local database after registration. Never reuse test credentials in production.

## Frontend

Use Node 22. Run `cd angularapp && npm ci`, then `NODE_OPTIONS=--openssl-legacy-provider npm start`. The checked-in development proxy forwards `/api` to localhost:8080. Build with `NODE_OPTIONS=--openssl-legacy-provider npm run build -- --prod`.

Finance retains deprecated Angular 10 dependencies and an OpenSSL compatibility option. This is not a frontend modernization or production security sign-off.

## Scope and limits

The local workflow replacement is manual-only and runs tests/build without the previous scanner or external callback. It has not run on GitHub. No merge, deployment, credential rotation or history rewrite is part of these checks. Existing homepage marketing copy is not evidence of live financial or cinema services. Review production configuration, dependencies, secrets and UI wording before any public deployment.
