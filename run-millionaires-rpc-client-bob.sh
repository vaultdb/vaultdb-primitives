#!/bin/bash -x
input="127.0.0.1 54321 2 200 50052"
mvn exec:java -Dexec.mainClass=org.vaultdb.compiler.emp.generated.MillionairesClient  -Dexec.args="$input"
