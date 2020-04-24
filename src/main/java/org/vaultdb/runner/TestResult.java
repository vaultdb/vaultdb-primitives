package org.vaultdb.runner;

import java.util.List;

public class TestResult {
  public List<String> outputList;
  public List<String> errorList;

  public TestResult(List<String> outputList, List<String> errorList) {
    this.outputList = outputList;
    this.errorList = errorList;
  }
}
