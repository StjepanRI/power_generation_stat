# Power Generation Statistics

A Java application that parses weekly power generation data from emails, stores it in a database, and visualizes it with customizable charts.

## Features

- **Email Parsing**: Automatically fetches and parses weekly emails containing power generation data tables
- **Data Extraction**: Extrapolates data from email table format
- **Database Storage**: Persists data in PostgreSQL with Hibernate ORM
- **Flexible Charting**: Multiple chart types (line, bar, area, pie, etc.)
- **Time-Based Filtering**: Select data by daily, weekly, monthly, or yearly intervals
- **Web Dashboard**: Spring Boot REST API with Thymeleaf frontend
- **Scheduled Jobs**: Quartz scheduler for weekly email processing

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.2
- **Database**: PostgreSQL with Hibernate ORM
- **Email**: javax.mail (IMAP)
- **Charting**: XChart
- **Scheduling**: Quartz
- **Build**: Maven

## Project Structure

```
src/main/java/com/powerstats/
├── config/              # Configuration classes
├── controller/          # REST API controllers
├── service/             # Business logic
├── repository/          # Database access (JPA)
├── entity/              # JPA entities
├── dto/                 # Data transfer objects
├── email/               # Email parsing logic
├── chart/               # Chart generation
├── scheduler/           # Quartz scheduler jobs
└── util/                # Utility classes

src/main/resources/
├── application.yml      # Spring Boot configuration
├── templates/           # Thymeleaf HTML templates
└── static/              # CSS, JavaScript
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 12+

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd power_generation_stat
   ```

2. **Configure database** (application.yml)
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/powerstats
       username: postgres
       password: your_password
   ```

3. **Configure email settings** (.env or application.yml)
   ```yaml
   email:
     imap-host: imap.gmail.com
     imap-port: 993
     username: your-email@gmail.com
     password: your-app-password
     folder: INBOX
   ```

4. **Build the project**
   ```bash
   mvn clean package
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Or:
   ```bash
   java -jar target/power-generation-stat-1.0.0.jar
   ```

6. **Access the dashboard**
   Open browser: `http://localhost:8080`

## Usage

### Manual Email Import
- Navigate to Dashboard → Import
- Trigger manual email fetch

### Scheduled Processing
- Quartz scheduler runs weekly (configurable)
- Automatically fetches and processes new emails

### Viewing Charts
- Select chart type (line, bar, area, scatter, pie)
- Choose time base (daily, weekly, monthly, yearly)
- Charts update dynamically

## Configuration

See `application.yml` for detailed configuration options:
- Email polling interval
- Database connection settings
- Quartz scheduler settings
- Chart rendering options

## Development

```bash
# Run tests
mvn test

# Format code
mvn spotless:apply

# Check code quality
mvn sonar:sonar
```

## API Endpoints

- `GET /api/data` - Fetch all power generation records
- `GET /api/data?startDate=...&endDate=...` - Filtered data
- `GET /api/chart?type=line&timeBase=daily` - Generate chart
- `POST /api/email/import` - Trigger manual email import
- `GET /api/stats/summary` - Summary statistics

## Next Steps

- [ ] Implement email parser
- [ ] Create JPA entities and repositories
- [ ] Build REST API endpoints
- [ ] Design web dashboard
- [ ] Implement chart generation
- [ ] Setup Quartz scheduler
- [ ] Add data validation and error handling
- [ ] Add unit and integration tests

## License

MIT