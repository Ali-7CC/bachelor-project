# Web-Based Application for Visualization of Process Data
Developed for my bachelor project - Jan 2021

## Installation
### Prerequisites
Make sure to have the following prerequisites installed:
- Java 11
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

Make sure that the backend is running on port 8080

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
