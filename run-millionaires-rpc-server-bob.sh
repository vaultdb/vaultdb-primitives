#!/bin/bash -x
input="50052"
mvn exec:java -Dexec.mainClass=org.vaultdb.compiler.emp.generated.MillionairesServer -Dexec.args="$input"