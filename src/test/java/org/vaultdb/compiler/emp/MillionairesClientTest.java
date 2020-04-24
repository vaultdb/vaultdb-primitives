package org.vaultdb.compiler.emp;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.vaultdb.runner.TestResult;
import org.vaultdb.runner.TestRunner;

public class MillionairesClientTest {

  @Test
  public void test() throws InterruptedException {
    String alicePort = "50051";
    String bobPort= "50052";
    String aliceInput = "127.0.0.1 54321 1 100 " + alicePort;
    String bobInput = "127.0.0.1 54321 2 200 " + bobPort;
    String serverClass = "org.vaultdb.compiler.emp.generated.MillionairesServer";
    String clientClass = "org.vaultdb.compiler.emp.generated.MillionairesClient";

    Thread aliceServerThread = new Thread(() -> {
      try {
        TestRunner.run(serverClass, alicePort, List.of());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    Thread bobServerThread = new Thread(() -> {
      try {
        TestRunner.run(serverClass, bobPort, List.of());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    Thread aliceClientThread = new Thread(() -> {
      try {
        TestResult result = TestRunner.run(clientClass, aliceInput, List.of());
        List<String> outputList = result.outputList;
        assertEquals(outputList.get(0), "Is Alice Richer: No!");
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Thread bobClientThread = new Thread(() -> {
      try {
        TestResult result = TestRunner.run(clientClass, bobInput, List.of());
        List<String> outputList = result.outputList;
        assertEquals(outputList.get(0), "Is Alice Richer: No!");
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    // start server
    aliceServerThread.start();
    bobServerThread.start();
    // wait for server to boot up
    Thread.sleep(1000);

    // start client
    aliceClientThread.start();
    bobClientThread.start();

    // wait for client
    aliceClientThread.join();
    bobClientThread.join();
    // kill server
    aliceServerThread.interrupt();
    bobServerThread.interrupt();
  }
}
