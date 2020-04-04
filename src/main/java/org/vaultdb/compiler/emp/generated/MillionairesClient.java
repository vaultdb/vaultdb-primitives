package org.vaultdb.compiler.emp.generated;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;

public class MillionairesClient {

  private final ManagedChannel channel;
  private final MillionairesGrpc.MillionairesBlockingStub blockingStub;

  public MillionairesClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build());
  }

  MillionairesClient(ManagedChannel channel) {
    this.channel = channel;
    blockingStub = MillionairesGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public void sendMillionairesQuery(String host, int port, int party, int netWorth) {
    MillionairesRequest request = MillionairesRequest.newBuilder()
        .setHost(host).setNetWorth(netWorth).setParty(party).setPort(port).build();
    MillionairesReplyOrBuilder response;
    try {
      response = blockingStub.sendMillionairesQuery(request);
    } catch (StatusRuntimeException e) {
      return;
    }
    String result = response.getIsAliceRicher() ? "Yes!" : "No!";

    System.out.println("Is Alice Richer: " + result);
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 5) {
      throw new IllegalArgumentException("Invalid args. Should be: host, port, netWorth, party, serverPort");
    }

    String host = args[0];
    int port = Integer.parseInt(args[1]);
    int party = Integer.parseInt(args[2]);
    int netWorth = Integer.parseInt(args[3]);
    int serverPort = Integer.parseInt(args[4]);
    MillionairesClient client = new MillionairesClient("localhost", serverPort);
    try {
      client.sendMillionairesQuery(host, port, party, netWorth);
    } finally {
      client.shutdown();
    }
  }
}
