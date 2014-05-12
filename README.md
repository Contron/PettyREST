PettyREST
======

A Java library for creating RESTful services and APIs.

Installation
------

To install PettyREST, you can build from the repo, or alternatively download the pre-built JAR.

Initial setup
------

You don't need any complicated setup to start using PettyREST. To set up a simple server, these two lines will suffice:

```java
Server server = new Server(8080);
server.start();
```

The above server won't do anything by itself, so why don't we add functionality?

Adding functionality
------

To add functionality to the server, it's as easy as linking an address with a specific handler. For example:

```java
server.link("/demonstration", new Definition((arguments, headers, post) -> "Hello, world"));
```

The above will link a custom handler to the **/demonstration** address on the server, accessible with a **GET** request with a content type of **text/plain**. Of course, you can change the request type or content type by passing the respective arguments.

Header information, POST data (if the method isn't anything but, this will be empty), and arguments (if there are no arguments present, this will also be empty) are all supplied. Output is whatever is returned from the method.

Roadmap
------

Planned support:

- Support for binary POST data
