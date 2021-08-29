# Football stats analyser

You can find the instructions in `doc/inscruction.md`

1- Git clone the repository on your computer
2- Build the application. Move to the root of this repository and run this command `sbt assembly`
3- Run the application `java -cp target/scala-2.13/gameStats-assembly-0.1.0-SNAPSHOT.jar gameStats.Main statToCkeck path`. Example: `java -cp target/scala-2.13/gameStats-assembly-0.1.0-SNAPSHOT.jar gameStats.Main ./doc/xml-test.xml passes_left`