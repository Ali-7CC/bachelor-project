# Web-Based Application for Visualization of Process Data
Developed for my bachelor project - Jan 2021

## Installation
### Prerequisites
Make sure to have the following prerequisites installed:
- Java 11 SDK
- npm
- maven

### Running the backend
To install the backend, navigate to the `backend` folder from your command line interpreter and  run:
```
mvn clean install
```
After the installation, you can start the backend by running:
```
java -jar target/bachelor-project-backend-1.0-SNAPSHOT.jar
```

Make sure that the backend is running on port 8080. If your 8080 port is busy, you can change configuration in:
- Backend: `backend/src/main/resources/application.properties` (add `server.port = <number>`)
- Frontend: `frontend/vue-frontend/.env.development`

## Running the frontend
To install the frontend packages, navigate to the `frontend/vue-frontend` folder from your command line interpreter and  run:
```
npm install
```
After the installation, start the frontend by running:
```
npm run serve
```

## Usage
You can try out the tool using the example log in `log/call_center_log.xes`.
### Example relation definition combinations:
- Activity frequency: `Attribute = Activity`, `Operation = COUNT`, `Aggregation = Sum`
- Average time between activities: `Attribute = time:timestamp`, `Operation = Difference`, `Aggregation = Average`

