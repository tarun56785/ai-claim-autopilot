# AI Incident-to-Claim Autopilot

A production-grade, event-driven microservices platform that transforms unstructured insurance incident reports (text) into a fully structured claim packet. This system utilizes local LLMs for data extraction, asynchronous messaging for fault tolerance, and GraphQL WebSockets for real-time human-in-the-loop review.

## Architecture & Tech Stack

This project is built using modern, cloud-native enterprise patterns.

**Core Technologies:**
* **Backend:** Java 21, Spring Boot 3.5.x
* **AI Integration:** Spring AI, Ollama (Local LLM: `qwen2.5:3b`)
* **Message Broker:** Apache Kafka (KRaft mode)
* **Database:** PostgreSQL (with Spring Data JPA)
* **API Gateway:** Spring Cloud Gateway
* **Security:** Keycloak (OAuth2 / OpenID Connect)
* **API Design:** REST & GraphQL (with Subscriptions/WebSockets)
* **Document/Email:** iText (In-memory PDF generation), Spring Mail, MailHog
* **Infrastructure:** Docker & Docker Compose

### Microservices Breakdown
1. **API Gateway (`port 8080`):** Acts as the single entry point. Validates Keycloak JWTs and routes requests to downstream services.
2. **Intake Service (`port 8081`):** REST API that receives raw incident text, saves the initial record to PostgreSQL, and produces an `incident.submitted` event to Kafka.
3. **AI Triage Service (`port 8082`):** Consumes raw incidents from Kafka. Uses Spring AI and Ollama to extract strict JSON data (Claim Type, Severity, Confidence Score). Implements retry logic and a Dead Letter Queue (DLQ) for AI failures. Produces a `claim.ai.triaged` event.
4. **Human Review Service (`port 8083`):** GraphQL API. Consumes AI-triaged claims, saves them to PostgreSQL, and broadcasts the new data to frontend clients in real-time via WebSockets. Provides a GraphQL Mutation for human adjusters to approve the claim.
5. **Fulfillment Service (`port 8084`):** Consumes the `claim.approved` event. Generates an official PDF claim dossier entirely in-memory using iText and emails it to the customer via MailHog.

---

## Prerequisites

Before running the application, ensure you have the following installed:
* **Docker & Docker Compose**
* **Java 17 or 21** & **Maven**
* **Ollama** (Running locally)
* **Postman** (For testing APIs and WebSockets)

---

## 🚀 How to Run the Application

### Step 1: Start the Infrastructure
Navigate to the root directory and spin up the backing services using Docker Compose:
```bash
docker-compose up -d