package org.vaultdb.compiler.emp;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.vaultdb.runner.TestResult;
import org.vaultdb.runner.TestRunner;

import static org.junit.Assert.*;

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
        TestRunner.run(serverClass, alicePort, null);
      } catch (IOException e) {
        fail();
        e.printStackTrace();
      }
    });
    Thread bobServerThread = new Thread(() -> {
      try {
        TestRunner.run(serverClass, bobPort, null);
      } catch (IOException e) {
        fail();
        e.printStackTrace();
      }
    });

    Thread aliceClientThread = new Thread(() -> {
      try {
        TestResult result = TestRunner.run(clientClass, aliceInput, null);
        List<String> outputList = result.outputList;
        assertEquals(outputList.get(0), "Is Alice Richer: No!");
      } catch (Exception e) {
        fail();
        e.printStackTrace();
      }
    });

    Thread bobClientThread = new Thread(() -> {
      try {
        TestResult result = TestRunner.run(clientClass, bobInput, null);
        List<String> outputList = result.outputList;
        assertEquals(outputList.get(0), "Is Alice Richer: No!");
      } catch (Exception e) {
        fail();
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
