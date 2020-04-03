#!/bin/bash -x
input="50051"
mvn exec:java -Dexec.mainClass=org.vaultdb.compiler.emp.generated.MillionairesServer -Dexec.args="$input" -e