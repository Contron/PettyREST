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

To get the server to respond to requests, you'll need to extend the ```Request``` class and override the ```handle()``` method yourself.

Header information, POST data (if the method isn't anything but, this will be empty), and arguments (if there are no arguments present, this will also be empty) are all supplied. Output to the browser is via appending to the ```output``` builder.

```java
public void handle(HashMap<String, String> arguments, HashMap<String, String> headers, HashMap<String, String> post, StringBuilder output)
{
	//get an argument
	String count = (arguments.containsKey("counter") ? arguments.get("counter") : "none");
	
	//show some output
	output.append("This is output being displayed as text/plain, with the counter at " + count + ".");
}
```

Once you've done that, you can link it to your server by calling the following:

```java
server.link("/hello/world", new Definition(MyRequestHandler.class, RequestType.GET_REQUEST, ContentType.TEXT_PLAIN_TYPE));
```

The above will link a custom handler to the **/hello/world** address on the server, accessible with a **GET** request with a content type of **text/plain**

Arguments can be supplied to the URL by appending **?count=32** to the end of the URL, like so: **/hello/world?count=32**.

It is important to ensure that there is ***no*** trailing forward slash at the end of the URL when you link it.

Roadmap
------

Planned support:

- Support for binary POST data
