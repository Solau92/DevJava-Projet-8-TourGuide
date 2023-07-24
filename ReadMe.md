# **ReadMe**

## - **TourGuide Application**

An app for travelers. </br>
This app uses Java to run.


## - **Getting Started**

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.


## - **Prerequisites**

You need to install :
* Java 17 (! Java 20 is not compatible with Gradle 8.1.1)
* Gradle 8.1.1


## - **Installing**

* [Install Java](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
* [Install Gradle](https://gradle.org/install/)


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.6/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.6/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.6/reference/htmlsingle/#web)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)


## - **Running App**

Import the code on your computer.

To run the app, go to the folder that contains the gradle.build file and execute the following command : *gradle build*

You can access the app in a browser at this address : http://localhost:8080/index or use Postman. </br>


## - **Testing**

The app has unit tests and integration tests written. </br>
To run the tests from gradle, go to the folder that contains the gradle.build file and execute the following command : *gradle test*. </br>
To get the reports, open the files :
- "*index.html*" in the folder "*/build/reports/tests/test*"
- and "*index.html*" in the folder "*/build/jacocoHtml*".

You can also run the tests in GitLab with the pipeline CI/CD, and download the test reports.