# Rooftop Academy Challenge

[Rooftop Academy Challenge Spring Boot App](https://github.com/jmburgues/rooftopacademy)

## Requirements

For building and running the application you need:

- [JDK 11](http://www.oracle.com/technetwork/java/javase/downloads/)
- [Maven 4](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.rooftop.academy.Application` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Features

This service will perform a text analysis based on an input string and character.
Endpoints available '/text'

HTTP verbs:
* POST '/text': analyses given text and verifies repetition in an interval of n characters.

Example Request:
```shell
{
"text": "solo se que nada se",
"chars": 2
}
```

Example Response:
```shell
{
   "id": 2345,
   "url": "/text/2345"
}
```
* GET '/text/{id}': returns detailed information about a particular text identified by ID.
```shell
{
   "id": 2345,
   "hash": "6c37815dafc28e44ded5f7bc827d15b2",
   "chars": 2,
   "result": {
       "so": 1,
       "ol": 1,
       "lo": 1,
       "o ": 1,
       " s": 2,
       "se": 2,
       "e ": 2,
       " q": 1,
       "qu": 1,
       "ue": 1,
       " n": 1,
       "na": 1,
       "ad": 1,
       "da": 1,
       "a ": 1
   }
}
```
* GET '/text': returns all saved texts, paginated. Available params: chars, page, rpp.
  * chars: number of characters to be taken in string analysis
  * page: page number (first page: 1)
  * rpp: page size (10 <= rpp >= 100)
  

* DELETE '/text/{id}': Deletes selected registry

## Contact info
securetux {at} gmail {dot} com