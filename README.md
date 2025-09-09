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
- In Swagger UI you can log in once adding Bearer <token> in front of it `(hint: use app.auth/issue-token-hs256 OR via Swagger-ui login)`

## Frontend

- Install Nodejs, Vue
- Run `npm i` and `npm run dev` to start the project
- Frontend starts on `http://localhost:5173`

### Info

- You must register before you can add messages


## Build testing
- Run `docker compose up -d --build web` at root. Ensure that .env has proper values.
- Program should start at http://localhost/
- (backend available at http://localhost:8080/swagger-ui/#/)

## Deployment
- Deploy aws container
- Run ssh (`ssh -i key.pem ubuntu@public-ip`) to connect to the ec2
- (Hint: on first time you need to install docker, compose and whatnot)
- Clone github project to /opt/rocket. Remove existing project if it's there (data will be lost)
- Ensure you have relevant information at `.env`
- Run `docker compose up -d --build web`
- App starts on the public-ip.

## Todo list:

- Utilize websockets for notifications
- Improve visual look
- Implement 1-on-1 chats
- Frontend typing could be improved (progressed)
- More standardized kebab-casing<->camelCasing., eg in
`:parameters {:body [:map
                     [:userIds [:vector int?]]
                     [:title string?]]}`
for example.
- More backend unit tests, frontend tests, e2e tests..
- Validate all inputs, so that million character message doesnt break db etc.
- More ui-friendliness, show limit for max characters
- Mobile support
- CD via github actions
- https instead of http in github
- Better deployment method? Push containers to ecr for example
- Logging
