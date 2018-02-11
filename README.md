Introduction
===========
This project is a simple Intellij Plugin with set of inspections about using Optionals

* a inspection about simplifying Optional call chains by a new Java 9 Optional Api.
Example:
````
    stream.filter(Optional::isPresent).map(Optional::get) --> stream.flatMap(Optional::stream)
````
See: [Java 9 Optional docs](https://docs.oracle.com/javase/9/docs/api/java/util/Optional.html#flatMap-java.util.function.Function-)
* a inspection about using a collection with an optional type parameter. 
For instance, it is recommended instead of `List<Optional<String>>` just use the `List<String>` with a smaller size.
See: [Java 8 anti-patterns](https://www.youtube.com/watch?v=ohu8dJu8KKw) 
                                
                     
Building the plugin
===================

The project use gradle as build tool. So to build and run plugin use:

    gradle build
    gradle runIdea
    
Testing the plugin
===================
Use JUnit Intellij Idea Run/Debug Configuration with VM options:
    
    -ea -Xbootclasspath/p:../out/classes/production/boot -XX:+HeapDumpOnOutOfMemoryError -Xmx512m -XX:MaxPermSize=320m -Didea.system.path=./build/idea-sandbox/system-test -Didea.config.path=./build/idea-sandbox/config-test -Didea.test.group=ALL_EXCLUDE_DEFINED -Didea.plugins.path=./build/idea-sandbox/plugins-test -Didea.home.path=../intellij-community -Dfile.encoding=utf-8

Before running test:
- download intellij-community github project for using mockJDK in tests
- generate idea-sandbox by gradle task `prepareTestingSandbox`
    
*Running tests by gradle task `test` hasn't worked yet.
