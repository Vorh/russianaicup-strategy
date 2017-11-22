#!/usr/bin/env bash



cd ./local-runner-ru/
sh local-runner.sh

cd ..

javac -cp src/main/java/ -d target/classes/ src/main/java/Runner.java

cd target/classes

java -cp . Runner