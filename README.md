# dbServer - Marie Projekt

Spring Boot REST-Server mit MS SQL Server (T-SQL Dialect).

## Was ist hier drin?

```
dbServer/
├── pom.xml                          # Maven Config (wie package.json)
├── schema.sql                       # DB-Setup-Skript (in SSMS ausführen)
├── src/main/
│   ├── java/com/marie/dbserver/
│   │   ├── DbServerApplication.java # Entry Point (wie server.js)
│   │   ├── controller/              # Routes (wie Express Router)
│   │   ├── repository/              # DB-Zugriff mit T-SQL
│   │   └── model/                   # Data Classes (Records)
│   └── resources/
│       └── application.properties   # Config (DB-Verbindung, Port)
```

## Voraussetzungen

1. **Java 21** (oder höher) — `java -version` checken
2. **Maven** — `mvn -version` checken (oder IntelliJ/VS Code macht das für dich)
3. **MS SQL Server** lokal installiert oder erreichbar
4. **SSMS** oder **Azure Data Studio** (zum Ausführen von `schema.sql`)

## Setup - Schritt für Schritt

### 1. Datenbank vorbereiten
Öffne `schema.sql` in SSMS und führe es aus. Das legt die `MarieDB` Datenbank an
und befüllt die `Operators`-Tabelle mit 10 R6S Operator als Testdaten.

### 2. Verbindungsdaten anpassen
In `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MarieDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=DEIN_PASSWORT
```

Bei **SQL Server Express** mit Named Instance:
```properties
spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=MarieDB;encrypt=true;trustServerCertificate=true
```

Bei **Windows Authentication** (statt User/Password):
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=MarieDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true
```
(braucht dann zusätzlich die `mssql-jdbc_auth-*.dll` von Microsoft im PATH)

### 3. Server starten
Im `dbServer/` Ordner:
```bash
mvn spring-boot:run
```
Server läuft dann auf `http://localhost:8080`.

## API-Endpoints testen

Mit `curl`, Postman oder Browser:

```bash
# Alle Operators
curl http://localhost:8080/api/operators

# Nur Attacker
curl "http://localhost:8080/api/operators?side=Attacker"

# Top 5 nach Speed
curl http://localhost:8080/api/operators/top

# Einen Operator per ID
curl http://localhost:8080/api/operators/1

# Neuen Operator anlegen
curl -X POST http://localhost:8080/api/operators \
  -H "Content-Type: application/json" \
  -d '{"name":"Iana","side":"Attacker","organization":"REU","speed":2,"armor":2}'

# Operator updaten
curl -X PUT http://localhost:8080/api/operators/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Ash","side":"Attacker","organization":"FBI SWAT","speed":3,"armor":1}'

# Operator löschen
curl -X DELETE http://localhost:8080/api/operators/1
```

## Express → Spring Boot Cheatsheet

| Express | Spring Boot |
|---------|-------------|
| `app.get("/path", fn)` | `@GetMapping("/path")` |
| `app.post("/path", fn)` | `@PostMapping("/path")` |
| `req.params.id` | `@PathVariable int id` |
| `req.query.side` | `@RequestParam String side` |
| `req.body` | `@RequestBody Operator op` |
| `res.json(data)` | `return data;` (wird automatisch zu JSON) |
| `res.status(404).end()` | `return ResponseEntity.notFound().build();` |
| `npm install` | `mvn install` |
| `node server.js` | `mvn spring-boot:run` |

## Nächste Schritte

- **Validation**: `@Valid` + Jakarta Validation Annotations (`@NotNull`, `@Size`)
- **Exception Handling**: `@ControllerAdvice` für zentrale Error Responses
- **Stored Procedures aufrufen**: Mit `JdbcClient` und `{call sp_name(?)}`
- **Transaktionen**: `@Transactional` auf Service-Methoden
- **Auth**: Spring Security (JWT, OAuth2, etc.)

Viel Spaß, brudi! 🎮
