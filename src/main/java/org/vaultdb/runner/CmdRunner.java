package org.vaultdb.runner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;


public class CmdRunner {

  public static int run(String mainClass, String args, OutputStream outputStream, OutputStream errorStream, InputStream inputStream) throws IOException {
    PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(
        outputStream,
        errorStream,
        inputStream
    );
    Executor exec = new DefaultExecutor();
    exec.setStreamHandler(pumpStreamHandler);

    String cmd = "mvn -q exec:java -Dexec.mainClass=\"" + mainClass + "\" -Dexec.args=\"" + args + "\"";
    CommandLine cl = CommandLine.parse(cmd);
    return exec.execute(cl);
  }

}
