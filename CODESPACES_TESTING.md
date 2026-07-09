# GitHub Codespaces Testing Guide

## Quick Start

### 1. Open Codespaces

1. Go to: https://github.com/StjepanRI/power_generation_stat
2. Click **Code** → **Codespaces** → **Create codespace on setup/initial-structure**
3. Wait for the environment to load (2-3 minutes)

### 2. Start PostgreSQL

In the terminal, run:

```bash
# Start PostgreSQL container
docker run --name powerstats-db \
  -e POSTGRES_DB=powerstats \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15-alpine
```

Or use Docker Compose:

```bash
docker-compose up -d postgres
```

### 3. Build the App

```bash
mvn clean package -DskipTests
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

You should see:
```
Tomcat started on port(s): 8080
Started PowerGenerationStatApplication
```

### 5. Access the Dashboard

Codespaces will automatically detect the running app and show a notification:
- Click **"Open in Browser"** or
- Go to the **Ports** tab and click the URL for port 8080

### 6. Test the App

You should see:
- ✅ Dashboard with statistics cards
- ✅ Import textarea for email data
- ✅ Chart section with filters

### 7. Test Data Import

Paste this sample data into the **Import Data from Email** textarea:

```
Date | Generated Power | Peak Power | Average Power
2026-07-01 | 45.5 kWh | 5.2 kW | 3.8 kW
2026-07-02 | 48.3 kWh | 5.5 kW | 4.1 kW
2026-07-03 | 42.1 kWh | 5.0 kW | 3.5 kW
2026-07-04 | 50.2 kWh | 5.8 kW | 4.2 kW
2026-07-05 | 46.8 kWh | 5.3 kW | 3.9 kW
```

Click **"Parse & Import Data"** → You should see:
- ✅ Success alert
- ✅ Statistics updated
- ✅ Chart populated

---

## 🧪 Run Unit Tests

### Run all tests:
```bash
mvn test
```

### Run specific test:
```bash
mvn test -Dtest=EmailParserTest
mvn test -Dtest=DataExtractionServiceTest
mvn test -Dtest=DataControllerTest
```

### View test results:
```bash
cat target/surefire-reports/TEST-*.txt
```

---

## 🔍 Debugging

### Check logs
```bash
# Follow app logs in real-time
tail -f target/power-generation-stat-1.0.0.jar
```

### Database commands
```bash
# Connect to PostgreSQL
docker exec -it powerstats-db psql -U postgres -d powerstats

# List tables
\dt

# Check data
SELECT * FROM power_generation_data;

# Exit
\q
```

### API Testing
```bash
# Get all data
curl http://localhost:8080/api/data

# Get data with date range
curl "http://localhost:8080/api/data?startDate=2026-07-01&endDate=2026-07-05"

# Test import endpoint
curl -X POST http://localhost:8080/api/import/parse \
  -H "Content-Type: application/json" \
  -d '{"content":"Date | Power\n2026-07-01 | 45.5 kWh"}'
```

---

## 🛑 Stop Services

```bash
# Stop the app (Ctrl+C in terminal)

# Stop PostgreSQL
docker stop powerstats-db
docker rm powerstats-db
```

---

## 💡 Tips

- **Keep terminal open** - App needs to keep running to serve requests
- **Forward ports** - Codespaces automatically forwards 8080 and 5432
- **Database persists** - Data stays in the Docker volume until you remove the container
- **Terminal slow?** - This is normal in Codespaces, just be patient
- **Need to rebuild?** - `mvn clean package` to rebuild from scratch

---

## ❌ Troubleshooting

**Port 8080 already in use?**
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

**Database connection failed?**
- Make sure PostgreSQL container is running: `docker ps`
- Check database is ready: `docker logs powerstats-db`

**Maven build fails?**
```bash
# Clear cache and retry
mvn clean
mvn dependency:resolve
mvn package -DskipTests
```

**Can't see app in browser?**
- Go to **Ports** tab in Codespaces
- Make sure port 8080 is visible and not private
- Click the port 8080 URL

---

## 🚀 Next Steps

After testing:
1. Test with your actual email data
2. Try different table formats (pipe, tab, space delimited)
3. Test chart types and time-base filtering
4. Check database with SQL commands

Enjoy testing! 🎉
