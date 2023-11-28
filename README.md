# Search application

## Start
### Prerequisites
* Java 17
* Maven

### How to compile
mvn clean compile

### How to run
mvn spring-boot:run

## Explain
### What is started
A java spring application is running when you started the run command. 
It will start a webserver with a simple html page at ***localhost:8080***
that can be used to call the backend server.
The backend is a simple REST server with one endpoint ***GET /search?term=searchingforthis***.
The result is the combined estimate search result from Google and Bing.

### Curl example
curl --location 'http://localhost:8080/search?term=test'

### Webpage
http://localhost:8080
