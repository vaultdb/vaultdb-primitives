# VaultDB Primitives
This framework demonstrates how to map relational database semantics onto secure computation.  It is part of an ongoing research effort to optimize SQL queries computed with secure computation.  

This code was developed by Madhav Suresh and Jennie Rogers

## VaultDB 

 Despite the abundance of information we collect on practically every domain of life, much of it is fractured among many private data stores. For example, electronic health records on a given patient are often partitioned among multiple hospitals. Querying fractured datasets is often challenging owing to regulatory requirements and privacy concerns. A _private_data_ federation_  queries the union of the private records of multiple autonomous data stores such that no one learns about the data of its peers. VaultDB, our PDF prototype, translates SQL queries into secure multi-party computation protocols and orchestrates their execution among the data providers.  This repo demonstrates the infrastructure that underpins VaultDB and it performs oblivious query evaluation using [EMP Toolkit](https://github.com/emp-toolkit).
 
 For more background on private data federations, please see our paper on this, [SMCQL](http://users.eecs.northwestern.edu/~jennie/pubs/smcql.pdf) and the corresponding [code](https://github.com/smcql/smcql).  


VaultDB performs all of its query planning and optimization in Java because it extends Apache Calcite for generic query parsing over many SQL dialects and uses its query tree system to optimize ad-hoc SQL queries.  In addition, VaultDB queries the federation's private datastores for its inputs to secure computation using JDBC such that it is agnostic to the database implementation used by the data provider.

VaultDB generates SQL-over-MPC code in EMP Toolkit for ad-hoc SQL queries.  The infrastructure in this repo demos how to compile a VaultDB query's code and load it dynamically into a query executor.

## Setup

### Dependencies

VaultDB primitives requires the following dependencies:
* EMP Toolkit: [installation instructions](https://github.com/emp-toolkit/emp-readme)
* Maven 4
* JavaCPP 1.5.2
* Java 8+
 
 ### Configuration
 
 Adjust the data federation configuration using within ```conf/setup```.   
 
 Configure any Java Native Interface (JNI)-specific parameters in  ```src/main/resources/org/bytedeco/javacpp/properties```.
 
 ## Example Code
 
VaultDB primitives demos the following facilities:
 * BuildMillionaires: Compiler for dynamically generated C++ and Java code.  Execute it with run-alice.sh and run-bob.sh
 * SortByteArray: demonstrates building and loading a SQL-over-MPC query.  Forks and execs Alice and Bob for unit test because they cannot run in the same process space.


## Acknowledgments 

This work is supported by NSF Award #1846447: CAREER: Efficient Query Processing for Private Data Federations.  
