# Microservices Communication and Batch Processing Test Applications

### Background
Many organizations are transitioning from monolithic to microservices architectures. As applications grow, scaling several smaller services is often easier than scaling a single large application. Effective communication between microservices is crucial for maximizing throughput. This application allows testing of HTTP and Kafka communication methods and the impact of different batch sizes on database performance.

### Overview
These simple Spring Boot applications are designed to test various methods of communicating between microservices (HTTP POST requests, Kafka Event Streaming) and the impact of different batch insert sizes on database throughput. The microservices are containerized using Docker Compose.

### Features
- Communication via HTTP and Kafka.
- Configurable batch sizes for database inserts.
- Performance monitoring with Prometheus and Grafana.
- Data persistence with PostgreSQL.
- Containerized deployment with Docker Compose.

## How to use
By default, simply build the Docker images and tests will begin 30 seconds after startup. Please see below for details on how to configure various settings for the tests.

The output of the tests will be saved to a spreadsheet in the DatabaseController application, which can be found in /app in the container. The configurations for the tests are done in ShopController's application.properties file, which is copied to the container during the build process. 
To change the configuration later, simply edit the application.properties file in the ShopController container and restart all containers.

### ShopController
The ShopController application contains the logic to send OrderEntity objects to the DatabaseController via HTTP or Kafka, and also to change the batch size, step between batch sizes, and to log the time to send each batch. 

Once compiled the Dockerfile will copy application.properties to the container, which contains the following properties:
    
```
batch.step=1000
test.quantity=10000
factors.only=true
```

The batch.step property determines the step between batch sizes, and the test.quantity property determines the total number of records to be sent. The default values will send 10,000 records in batches of 1,000, 2,000, 3,000, etc.

The factors.only property will test only for batch sizes for which the test.quantity is evenly divisible. Eg. if test.quantity=10000 and batch.step=1000, the factors.only property will only test for batch sizes of 1000, 2000, 5000, and 10000.

Note that the time logged by this application is the send time for each batch to be completely sent to the DatabaseController, but does not include the time to insert into the database. 

### DatabaseController
The DatabaseController application contains the logic to receive OrderEntity objects, as well as an endpoint that listens for requests from the ShopController to update batch sizes. 
It also includes functionality to change the wait time for a full batch and logging statements to log the time required to insert the records to the PostgreSQL database at each batch size. 
This logic is contained in the OrderBatchSize class. There is an additional ExcelWriter class that saves the test results for various batches to a spreadsheet, to simplify later analysis.

The times logged by this application start the moment the first record of the batch is received and end when the last record is inserted into the database.

### Grafana
A full explanation of Grafana / Prometheus is beyond the scope of this README, but in short you can access localhost:3000 from your browser (exposed from within the Docker container) and load public dashboards of your choosing to monitor the various applications. The default login is admin/admin. I recommend the following two dashboards:

[Spring Boot Statistics](https://grafana.com/grafana/dashboards/6756-spring-boot-statistics/)

[Spring Boot Kafka Templates](https://grafana.com/grafana/dashboards/20786-spring-boot-kafka-templates/)

The included Prometheus.yml file allows for monitoring of both ShopController and DatabaseController.

## Licensing

MIT License

Copyright (c) 2024 Christian Barbati

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
