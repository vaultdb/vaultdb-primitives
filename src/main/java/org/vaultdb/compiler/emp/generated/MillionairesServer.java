package org.vaultdb.compiler.emp.generated;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.vaultdb.compiler.emp.generated.Millionaires.MillionairesClass;


public class MillionairesServer {


  public MillionairesServer() {

  }



  private Server server;

  private void start(int port) throws IOException {
    server = ServerBuilder.forPort(port)
        .addService(new MillionairesImpl())
        .build()
        .start();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          MillionairesServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  public static class MillionairesImpl extends MillionairesGrpc.MillionairesImplBase {

    @Override
    public void sendMillionairesQuery(
        MillionairesRequest req, StreamObserver<MillionairesReply> responseObserver) {

      MillionairesClass m = new MillionairesClass();
      boolean aliceRicher;
      m.configure(req.getParty(), req.getPort(), req.getHost());

      aliceRicher = m.run(req.getNetWorth());
      m.cleanup();
      m.close();

//      String result = aliceRicher ? "Yes!" : "No!";
//      System.out.println("Is Alice richer than Bob? " + result);

      MillionairesReply reply = MillionairesReply
          .newBuilder().setIsAliceRicher(aliceRicher).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }


  // usage: millionaires <party> <host>  <port>
  public static void main(String[] args) throws IOException, InterruptedException {
    int port = Integer.parseInt(args[0]);
		final MillionairesServer server = new MillionairesServer();
		server.start(port);
		server.blockUntilShutdown();

  }
}
