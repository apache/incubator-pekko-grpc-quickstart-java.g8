# Apache Pekko gRPC Quickstart with Java 
 
Pekko gRPC is a toolkit for building streaming gRPC servers and clients on top of Pekko Streams. This guide will get you started building gRPC based systems with Java. If you prefer to use Pekko gRPC with Scala, switch to the [Pekko gRPC Quickstart with Scala guide](https://github.com/apache/pekko-grpc-quickstart-scala.g8). 

After trying this example the [Pekko gRPC documentation](https://pekko.apache.org/docs/pekko-grpc/current/) is a good next step to continue learning more about Pekko gRPC.

## Downloading the example 

Follow the instructions in https://github.com/apache/pekko-grpc-quickstart-java.g8 

## Running the example

To run Hello World:

@sbt[On OSX/Linux use `./sbt` to start sbt in the instructions below, on Windows `sbt.bat`.]
@gradle[On OSX/Linux use `./gradlew` to start Gradle in the instructions below, on Windows `./gradlew.bat`.]

1. In a console, change directories to the top level of the unzipped project.
 
    For example, if you used the default project name, pekko-grpc-quickstart-java, and extracted the project to your root directory,
    from the root directory, enter: `cd pekko-grpc-quickstart-java`

1. Compile the project by entering:

    sbt
    :   ```
        ./sbt compile
        ```

    Maven
    :   ```
        mvn compile
        ```

    Gradle
    :   ```
        ./gradlew compileJava
        ```
 
    @sbt[sbt]@maven[Maven]@gradle[Gradle] downloads project dependencies, generates gRPC classes from protobuf, and compiles.

1. Run the server:

    sbt
    :   ```
        ./sbt "runMain com.example.helloworld.GreeterServer"
        ```

    Maven
    :   ```
        mvn compile dependency:properties exec:exec@server
        ```

    Gradle
    :   ```
        ./gradlew runServer
        ```
 
    @sbt[sbt]@maven[Maven]@gradle[Gradle] runs the `com.example.helloworld.GreeterServer` main class that starts the gRPC server.
    @maven[The `exec:exec@server` execution is defined in the Maven `pom.xml` build definition.]
    @gradle[The `runServer` task is defined in `build.gradle`.]

    The output should include something like:

    ```
    gRPC server bound to: /127.0.0.1:8080
    ```

1. Run the client, open another console window and enter:

    sbt
    :   ```
        ./sbt "runMain com.example.helloworld.GreeterClient"
        ```

    Maven
    :   ```
        mvn compile dependency:properties exec:exec@client
        ```

    Gradle
    :   ```
        ./gradlew runClient
        ```
 
    @sbt[sbt]@maven[Maven]@gradle[Gradle] runs the `com.example.helloworld.GreeterClient` main class that starts the gRPC client.
    @maven[The `exec:exec@client` execution is defined in the Maven `pom.xml` build definition.]
    @gradle[The `runClient` task is defined in `build.gradle`.]

    The output should include something like:

    ```
    Performing request: Alice
    Performing request: Bob
    HelloReply(Hello, Bob)
    HelloReply(Hello, Alice)
    ```


Congratulations, you just ran your first Pekko gRPC server and client. Now take a look at what happened under the covers.

You can end the programs with `ctrl-c`.

## What Hello World does

As you saw in the console output, the example outputs several greetings. Let’s take at the code and what happens at runtime.

### Server

First, the `GreeterServer` main class creates an `org.apache.pekko.actor.typed.ActorSystem`, a container in which Actors, Pekko Streams and Pekko HTTP run. Next, it defines a function from `HttpRequest` to `CompletionStage<HttpResponse>` using the `GreeterServiceImpl`. This function
handles gRPC requests in the HTTP/2 with TLS server that is bound to port 8080 in this example.

@@snip [GreeterServer.java]($g8src$/java/com/example/helloworld/GreeterServer.java) { #import #server }

`GreeterServiceImpl` is our implementation of the gRPC service, but first we must define the interface of the service
in the protobuf file `src/main/proto/helloworld.proto`:

@@snip [helloworld.proto]($g8src$/proto/helloworld.proto) { #service-request-reply }

When compiling the project several things are generated from the proto definition. You can find the generated files in 
@sbt[`target/scala-2.13/pekko-grpc/main/`]@maven[`target/generated-sources/`]@gradle[`build/generated/source/proto/main/`]
if you are curious.

For the server the following classes are generated:

* Message classes, such as `HelloRequest` and `HelloReply`
* `GreeterService` interface of the service
* `GreeterServiceHandler` utility to create the `HttpRequest` to `HttpResponse` function from the `GreeterServiceImpl`

The part that we have to implement on the server side is the `GreeterServiceImpl` which implements the generated `GreeterService` interface. It is this implementation that is bound to the `HTTP` server via the `GreeterServiceHandler` and it looks like this:

@@snip [GreeterServiceImpl.java]($g8src$/java/com/example/helloworld/GreeterServiceImpl.java) { #import #service-request-reply }

### Client

In this example we have the client in the same project as the server. That is common for testing purposes but for real usage
you or another team would have a separate project (different service) that is using the client and doesn't implement the
server side of the service. Between such projects you would only share the proto file (by copying it).

From the same proto file that was used on the server side classes are generated for the client:

* Message classes, such as `HelloRequest` and `HelloReply`
* `GreeterService` interface of the service
* `GreeterServiceClient` that implements the client side of the `GreeterService`

On the client side we don't have to implement anything, the `GreeterServiceClient` is ready to be used as is.

We need an `ActorSystem` and then the `GreeterServiceClient` can be created and used like this:

@@snip [GreeterClient.java]($g8src$/java/com/example/helloworld/GreeterClient.java) { #import #client-request-reply }

Note that clients and servers don't have to be implemented with Pekko gRPC. They can be implemented/used with other libraries or languages and interoperate according to the gRPC specification.

### Other types of calls

In this first example we saw a gRPC service call for single request returning a `CompletionStage` reply.
The parameter and return type of the calls may also be streams in 3 different combinations:

* **client streaming call** - `Source` (stream) of requests from the client that returns a
  `CompletionStage` with a single response,
  see `itKeepsTalking` in above example
* **server streaming call** - single request that returns a `Source` (stream) of responses,
  see `itKeepsReplying` in above example
* **client and server streaming call** - `Source` (stream) of requests from the client that returns a
  `Source` (stream) of responses,
  see `streamHellos` in above example

As next step, let's try the @ref[bidirectional streaming calls](streaming.md).

@@@index

* [Streaming gRPC](streaming.md)
* [Testing gRPC](testing.md)

@@@
