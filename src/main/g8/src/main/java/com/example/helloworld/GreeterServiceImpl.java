package com.example.helloworld;

//#import

import org.apache.pekko.NotUsed;
import org.apache.pekko.japi.Pair;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.stream.javadsl.BroadcastHub;
import org.apache.pekko.stream.javadsl.Keep;
import org.apache.pekko.stream.javadsl.MergeHub;
import org.apache.pekko.stream.javadsl.Sink;
import org.apache.pekko.stream.javadsl.Source;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

//#import

//#service-request-reply
//#service-stream
class GreeterServiceImpl implements GreeterService {

  final ActorSystem<?> system;
  //#service-request-reply
  final Sink<HelloRequest, NotUsed> inboundHub;
  final Source<HelloReply, NotUsed> outboundHub;
  //#service-request-reply

  public GreeterServiceImpl(ActorSystem<?> system) {
    this.system = system;
    //#service-request-reply
    Pair<Sink<HelloRequest, NotUsed>, Source<HelloReply, NotUsed>> hubInAndOut =
      MergeHub.of(HelloRequest.class)
        .map(request ->
            HelloReply.newBuilder()
                .setMessage("Hello, " + request.getName())
                .build())
        .toMat(BroadcastHub.of(HelloReply.class), Keep.both())
        .run(system);

    inboundHub = hubInAndOut.first();
    outboundHub = hubInAndOut.second();
    //#service-request-reply
  }

  @Override
  public CompletionStage<HelloReply> sayHello(HelloRequest request) {
    return CompletableFuture.completedFuture(
        HelloReply.newBuilder()
            .setMessage("Hello, " + request.getName())
            .build()
    );
  }

  //#service-request-reply
  @Override
  public Source<HelloReply, NotUsed> sayHelloToAll(Source<HelloRequest, NotUsed> in) {
    in.runWith(inboundHub, system);
    return outboundHub;
  }
  //#service-request-reply
}
//#service-stream
//#service-request-reply
