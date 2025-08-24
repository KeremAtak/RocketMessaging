# RocketMessaging

An application for real-time messaging between two or more people

## Backend:

### Installation

- Install Clojure. See more comprehensive guide on https://clojure.org/guides/install_clojure
- Start the PostgreSQL container with `docker compose up -d`
- With Idea, on backend path you can start the server by creating a local REPL, starting via nREPL. Run with deps and pass `-A:dev` to options.
- Run `(go)` in the repl. Calling it again will (re)start the server. Call with `{:migrate-clean true}` to reset existing database.
- Backend starts on path `http://localhost:3000`. You can inspect Swagger-ui on `http://localhost:3000/swagger-ui`
- Migrations are run via Migratus (see `https://github.com/yogthos/migratus`)

### Info

- Migrations are run via Migratus (see `https://github.com/yogthos/migratus`)
- You can validate formatting via `clojure -M:cljfmt check`. Run `clojure -M:cljfmt fix` to fix issues.
- In Swagger UI you can log in once adding Bearer <token> in front of it `(hint: use app.auth/issue-token-hs256)`

## Todo list:

- Indexing must be taken into account
- Frontend (Next.js?)
- Utilize websockets for notifications
- Authentication (Google Sign-In?)
- AWS deployment
