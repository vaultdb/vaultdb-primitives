package org.vaultdb.runner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TestRunner {

  public static TestResult run(String mainClass, String args, List<String> input) throws IOException {

    OutputStream outputStream = new ByteArrayOutputStream();
    OutputStream errorStream = new ByteArrayOutputStream();

    if (null == input) {
      input = new ArrayList<>();
    }

    String inputString = String.join("\n", input);
    InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());

    CmdRunner.run(mainClass, args, outputStream, errorStream, inputStream);
    String regex = "\n";
    List<String> res = Arrays.asList(outputStream.toString().split(regex));
    List<String> err = Arrays.asList(errorStream.toString().split(regex));
    return new TestResult(res, err);
  }

}
