Loreweave

A Collaborative Fantasy Storytelling Platform

Where every player is an author, every choice shapes the world, and every story becomes legend.

Loreweave is a turn-based multiplayer storytelling platform built with Spring Boot and Thymeleaf. Players create unique characters, write story parts, vote on narrative outcomes, earn Lore Points, and receive real-time updates as stories unfold.
✨ Features
📝 Collaborative Storytelling

Create or join shared stories.

Players take turns writing new chapters (“story parts”).

Voting determines which contribution becomes canon.

🧙 Character Creation

Fully custom character profiles with names and biographies.

Characters are required before creating or joining stories.

💰 Lore Points & Transactions

Earn lore points for contributions and votes.

Integrated transaction system using LoreVote.

Backend validation & reward logic included.

🔔 Real-Time Notifications

Powered by Spring WebSockets (STOMP)

Story updates

Message notifications

Vote results

Read/unread badge updates without refreshing

💬 Messaging System

1-on-1 direct messaging

Live updates for conversations

User-friendly thread layout

🛡️ Authentication & Security

Session-based login

Email OTP verification

BCrypt password hashing

Page access restrictions

Logout & session handling

🎨 Modern Fantasy UI

Fully themed dark-fantasy styling

Video backgrounds (e.g., flyingpages.mov)

Hover glowing effects

Polished Bootstrap + custom CSS

🛠️ Tech Stack
Backend

Java 21

Spring Boot 3

Spring Security

Spring Data JPA

Spring WebSocket

Spring Mail

Frontend

Thymeleaf

Bootstrap 5

Custom CSS

GSAP animations

STOMP WebSocket client

Database

MariaDB

Spring Data Repositories

Build

Maven

Spring Boot Maven Plugin

📦 Installation
Prerequisites

Java 21+

Maven or included ./mvnw wrapper

MariaDB server

🧭 How to Use Loreweave

Register for an account.

Verify your email using the OTP code.

Create a character—required before writing.

Start or join a story with other players.

Take turns writing story parts.

Vote for your favorite continuation.

Earn Lore Points while contributing.

Receive live notifications in real time.

📂 Project Structure
src/main/java/com/loreweave/loreweave/
│
├── config/            # Security, WebSocket
├── controller/        # MVC controllers
├── model/             # Entities (Story, User, LoreVote, Character…)
├── repository/        # JPA repositories
├── service/           # Business logic, OTP, transactions, notifications
└── websocket/         # STOMP messaging channels

Milestone Summary
Milestone 1 — Security & Core Features

User authentication (session-based)

Email OTP verification

Character creation

Story + story part structure

Milestone 2 — Voting, Transactions, Notifications

LoreVote system

Lore Points rewards

Notification service

WebSocket integration

Milestone 3 — UI/UX & Real-Time Polish

Full fantasy-themed styling

Navbar redesign + dropdown menus

Video backgrounds

Responsiveness improvements

Messaging UI completion

Contributors
| Name              | Role                                           | Contributions                                                                                                  |
| ----------------- | ---------------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Jamie Coker**   | Security, Transactions, UI Styling, Deployment | OTP verification, security config, transaction architecture, navbar/notifications styling, site-wide UI polish |
| **Chris Ennis**   | Backend, WebSockets, Database                  | Story/StoryPart backend, WebSocket messaging, DB design, entity relationships                                  |
| **Wyatt Bechtle** | Frontend UI, Templates, UX                     | Story pages, notifications menu, general UI improvements, responsive layouts                                   |

