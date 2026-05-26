# Pocket Deck
Pocket Deck is a real-time multiplayer card game application for Android, powered by a Spring Boot backend. It features a robust lobby system and a secure, synchronized game engine capable of handling live, turn-based gameplay (currently featuring Blackjack).

# Core Architecture & Features
- Hybrid REST & WebSocket Architecture: Uses REST endpoints for secure lobby management and owner validation, seamlessly handing off to WebSockets for low-latency, real-time game engine synchronization.

- Secure State Management: Implements strict "information hiding" on the server side. The game engine dynamically generates personalized JSON state payloads for each connection, ensuring clients only receive their own cards and preventing frontend cheating.

- Role-Based Game Flow: Enforces Lobby Owner privileges for starting rounds and managing the lobby state, while safely handling edge cases like unauthorized connections or mid-game spectator attempts.

- Dynamic UI Routing: The backend intelligently tracks seat assignments and connection states, passing synchronized flags to the Android frontend to render the correct player views and action buttons.

# Tech Stack
- Frontend: Android

- Backend: Java, Spring Boot, Spring WebSockets

- Data: REST APIs, JSON payload mapping

### NOTE
Many files were removed for privacy reasons. I worked with a partner on backend, so I kept the backend logic, but without access to the vpn-protected database, or the appropriate softwares, it is rather difficult to run this applciation. This is here purely to preserve the code, not to run.
