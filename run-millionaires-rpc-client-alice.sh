#!/bin/bash -x
input="127.0.0.1 54321 1 100 50051"
mvn exec:java -Dexec.mainClass=org.vaultdb.compiler.emp.generated.MillionairesClient  -Dexec.args="$input"
