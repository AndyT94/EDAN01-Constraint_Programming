!!! Run initcs first

1. Execution of JaCoP programs for command line

javac Main.java
java Main

System variable CLASSPATH is set to jacop.jar.

2. Execution of minizinc models

a) compilation to flatzinc with JaCoP library

mzn2fzn -G jacop model.mzn

b) Execution using JaCoP library

fzn-jacop [options] model.fzn

To get a list of available options use fzn-jacop -h.
 
