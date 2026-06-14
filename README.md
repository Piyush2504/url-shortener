# URL Shortener Platform

A scalable URL Shortener application built using Spring Boot, PostgreSQL, Redis, Docker, and AWS EC2. The application enables users to generate shortened URLs and seamlessly redirect to the original destination while leveraging Redis caching for improved performance and reduced database lookups.

---

## Features

* Generate unique short URLs for long URLs.
* Redirect users to the original URL using the generated short code.
* Redis-based caching for faster URL resolution.
* PostgreSQL persistence layer for reliable storage.
* RESTful API architecture using Spring Boot.
* Responsive frontend built using HTML, CSS, and JavaScript.
* Containerized deployment using Docker and Docker Compose.
* Cloud deployment on AWS EC2.
* Environment-based configuration management using `.env` files.

---

## System Architecture

```text
                +----------------+
                |    Browser     |
                +-------+--------+
                        |
                        v
                +----------------+
                | Spring Boot API|
                +-------+--------+
                        |
          +-------------+-------------+
          |                           |
          v                           v
   +--------------+          +---------------+
   |    Redis     |          | PostgreSQL DB |
   |   (Cache)    |          |   (Storage)   |
   +--------------+          +---------------+
```

### URL Creation Flow

1. User submits a long URL.
2. Spring Boot generates a unique short code.
3. Mapping is stored in PostgreSQL.
4. Short URL is returned to the user.

### URL Redirection Flow

1. User accesses the short URL.
2. Application checks Redis cache.
3. If present, redirect immediately.
4. If absent, fetch from PostgreSQL.
5. Store result in Redis for future requests.
6. Redirect user to original URL.

---

## Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Data JPA
* Maven

### Database

* PostgreSQL

### Cache

* Redis

### Frontend

* HTML
* CSS
* JavaScript

### DevOps & Cloud

* Docker
* Docker Compose
* Docker Hub
* AWS EC2

---

## Project Structure

```text
url-shortener
│
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   │   ├── static
│   │   │   │   └── index.html
│   │   │   └── application.properties
│
├── Dockerfile
├── docker-compose.yml
├── .env
├── pom.xml
└── README.md
```

---

## API Endpoints

### Create Short URL

```http
POST /api/shorten
```

Request:

```json
{
  "url": "https://www.google.com"
}
```

Response:

```json
{
  "shortUrl": "50328aa4"
}
```

---

### Redirect to Original URL

```http
GET /{shortCode}
```

Example:

```http
GET /50328aa4
```

Response:

```http
302 Found
Location: https://www.google.com
```

---

## Local Setup

### Clone Repository

```bash
git clone <repository-url>
cd url-shortener
```

### Build Application

```bash
mvn clean package
```

### Start Using Docker Compose

```bash
docker compose up -d
```

### Verify Running Containers

```bash
docker ps
```

Expected Containers:

* Spring Boot Application
* PostgreSQL
* Redis

---

## Environment Variables

Create a `.env` file:

```env
DB_URL=jdbc:postgresql://postgres-db:5432/urlshortener
DB_USERNAME=postgres
DB_PASSWORD=password
DB_NAME=urlshortener

REDIS_HOST=redis
REDIS_PORT=6379

CACHE_TYPE=redis
```

---

## Docker Deployment

Build Image:

```bash
docker build -t url-shortener .
```

Push Image:

```bash
docker push <dockerhub-username>/url-shortener:latest
```

Run Application:

```bash
docker compose up -d
```

---

## AWS EC2 Deployment

### Steps Performed

1. Provisioned AWS EC2 instance.
2. Installed Docker and Docker Compose.
3. Pulled application image from Docker Hub.
4. Configured PostgreSQL and Redis containers.
5. Managed environment variables using `.env`.
6. Exposed application through EC2 Security Groups.
7. Successfully deployed complete application stack on AWS.

---

## Performance Optimization

* Implemented Redis cache-first lookup strategy.
* Reduced database queries for frequently accessed URLs.
* Containerized services for simplified deployment and scalability.
* Externalized configurations through environment variables.

---

## Future Enhancements

* User authentication and authorization.
* Analytics dashboard for click tracking.
* URL expiration support.
* Custom short URL aliases.
* Rate limiting and API throttling.
* QR code generation for shortened URLs.
* Kubernetes deployment support.

---

## Skills Demonstrated

* Java
* Spring Boot
* REST APIs
* PostgreSQL
* Redis
* Docker
* Docker Compose
* AWS EC2
* System Design
* Caching Strategies
* Cloud Deployment
* Backend Development
* Git & GitHub

---

## Author

**Piyush Bhatia**

Backend Developer | Java | Spring Boot | AWS | Redis | PostgreSQL

LinkedIn: https://www.linkedin.com/in/piyush-bhatia25

GitHub: https://github.com/Piyush2504


