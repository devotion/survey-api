# Requirements #
Using microservice architecture make api for online survey application like SurveyMonkey.

Initial set of features would be:  
 - add/edit/delete questions, answers
 - read a list of all questions
 - read a question with all answers
 - respond to a survey
 - read the result for a particular question showing the total number of responses and distribution among the 
   possible answers in percent.
 
# Architectual Decisions #
To be able to scale application for high traffic volumes I will use Microservice Architecture pattern.
Such architecture with help of well known and adopted tools should make development, 
deployment and day to day running processes smooth.
  
## #1 Microservices organization ##
From given features I identified three groups of usages:

- AUTHORING-SERVICE: Survey authoring module that is going to be used by survey creators. 
Expected number of module users (request per sec) is in reasonable order of magnitude.
This module will also provide questions and answers at survey beginning.
Features in this module:
    - add/edit/delete questions, answers
    - read a list of all questions
    - read a question with all answers
 
- RESULTCAPTURE-SERVICE: Survey result capture module that is going accept and store actual answers. 
Expected number of requests in this module is much higher than in the previous case.
Features in this module:
    - respond to a survey

- ANALYTICS-SERVICE: Analytics module will provide different statistical aggregations based on actual answers. 
This module is going to be used by first group of users or/and survey owners. 
This group of users is also much smaller than second one.
Features in this module:
    - read the result for a particular question showing the total number of responses and distribution among the 
        possible answers in percent.

From all above I can make assumption that each module will have different scaling needs 
so decided to design them as separate services.
 
In addition to mentioned services two more technical services are going to be used:

- GATEWAY-SERVICE: for dynamic request routing, security, monitoring...
- REGISTRY-SERVICE: for service discovery 
 

## #2 Data persistence ##
Each service will have its own mongodb instance.
Exception from this is first iteration of ANALYTICS-SERVICE which is going to pull data from other two services on request.
In future iterations I can implement it in more robust and scalable way to be able to support big and fast data analytics 
in realtime (Spark, Kafka ...???) ... 

## #3 Data exchange & Client-service and service-service communication ##
JSON data format over http rest endpoints is going to be used for communication between client and server,
 as well as for inter-service communication.
Use synchronous service communication because of better cloud tools support at the moment
(spring streams cloud or spring 5 reactive is still immature)

## #4 Scaling, Service discovery, load balancing and circuit breakers ##
Use Netflix Eureka for service discovery and Ribbon for client side load balancing and Zuul for request routing.
For auto scaling, self healing I'll relay on cloud provider platform such is Kubernetes.

## #7 API versioning ##
To be able to support smooth API api evolution versioning is going to be in place from the start.
Versioning method of choice is use of Accept header in each request `Accept=application/vnd.survey-1.0+json`

# Build & Run #
####Prerequisites: 
- git
- jdk8
- maven 3.2 +
- docker ce 17.0 or higher
- docker-compose version 1.14.0 or higher
- if you plan to open code in your IDE please install Lombok plugin.

####Build:

- `git clone https://ljufa@bitbucket.org/ljufa/survey.git`

- `cd survey`

- `./mvnw clean package`
 
####Run: 
 
 - `docker-compose up -d --build` - to run all services for the first time
 
 or use utility script
 
 - `./rebuild.sh` - to rebuild and restart all services 
 - `./rebuild.sh <service name>` - to rebuild and restart single service
 - `./rebuild.sh <service name> debug` - to rebuild and restart single service in remote debug mode. 
    See [docker-compose.debug.yml](docker-compose.debug.yml) for debug port mappings. 

After some time API should be accessible on localhost port 80.
Please note that due to service discovery it could take up to 3 heartbeat cycles before each service being discovered.
 
####Stop & clean:

`docker-compose down --rmi local`

# API Documentation #
Once you run application you can [go to swagger UI](http://localhost/swagger-ui.html)
for more details

# Logging & Monitoring #
TBD

# Testing #
TBD
