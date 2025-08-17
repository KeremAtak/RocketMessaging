# RocketMessaging

An application for real-time messaging between two or more people

## Installation

### Backend:

- Install Clojure. See more comprehensive guide on https://clojure.org/guides/install_clojure
- With Idea, on backend path you can start the server by creating a local REPL, starting via nREPL. Run with deps and pass `-A:dev` to options.
- Run `(go)` in the repl. Calling it again will restart the server.
- Backend starts on path `http://localhost:3000`. You can inspect Swagger-ui on `http://localhost:3000/swagger-ui`

## Todo list:

- Database (postgresql)
- Indexing must be taken into account
- Frontend (Next.js?)
- Utilize websockets for notifications
- Authentication (Google Sign-In?)
- AWS deployment
