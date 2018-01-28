Introduction
===========
This project is a simple Intellij Plugin with set of inspections about using Optionals

* a inspection about simplifying Optional call chains by a new Java 9 Optional Api.
Example:
````
    collection.filter(Optional::isPresent).map(Optional::get) --> collection.flatMap(Optional::stream)
````
* a inspection about using a collection with an optional type parameter. 
For instance, it is recommended instead of `List<Optional<String>>` just use the `List<String>` with a smaller size. 
                                
                     
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
