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

## Frontend

- Install Nodejs, Vue
- Run `npm i` and `npm run dev` to start the project
- Frontend starts on `http://localhost:5173`

### Info

- You must register before you can add messages


## Deployment
- Run `docker compose up -d --build web` at root. Ensure that .env has proper values.
- Program should start at http://localhost/
- (backend available at http://localhost:8080/swagger-ui/#/)
- Deploy aws container
- Run via ssh (`ssh -i key.pem ubuntu@public-ip`) to enable settings (and test it)

`sudo apt update -y
sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo $VERSION_CODENAME) stable" \
| sudo tee /etc/apt/sources.list.d/docker.list >/dev/null
sudo apt update -y
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
newgrp docker <<'EOF'
docker --version
docker compose version
docker run --rm hello-world (might need a reset of connection before this)
`

## Todo list:

- Utilize websockets for notifications
- AWS deployment
- Frontend typing could use help
- More backend unit tests, more frontend tests