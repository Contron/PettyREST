PettyREST
======

A work-in-progress REST library for Java.

Designed with a no-bullshit theory in mind.

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

To get the server to respond to requests, you'll need to extend the ```Request``` class and override the ```handle()``` method yourself.

Header information and POST data (if the method is anything but, this will be empty), along with arguments are all supplied. Output to the browser is via appending to the ```output``` builder.

```java
public void handle(HashMap<String, String> headers, HashMap<String, String> post, ArrayList<String> args, StringBuilder output);
{
	output.append("This is a working request being sent as text/plain\n");
}
```

Once you've done that, you can link it to your server by calling the following:

```java
server.link("/some/special/url", new Definition(MyRequestHandler.class, RequestType.GET_REQUEST, ContentType.TEXT_PLAIN_TYPE));
```

The above will link a custom handler to the **/some/special/url** address on the server, accessible with a **GET** request with a content type of **text/plain**

Roadmap
------

The current plan in the future I have for PettyREST includes:

- Support for binary POST data