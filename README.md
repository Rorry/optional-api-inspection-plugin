Introduction
===========

This project is a simple Intellij Inspection Plugin to simplify Optional call chains by a new Java 9 Optional Api.
Example:

    collection.filter(Optional::isPresent).map(Optional::get) --> collection.flatMap(Optional::stream)
                                
                     
Building the plugin
===================

The project use gradle as build tool. So to build and run plugin use:

    gradle build
    gradle runIdea
    
Testing the plugin
===================
Use JUnit Intellij Idea Run/Debug Configuration with VM options:
    
    -ea -Xbootclasspath/p:../out/classes/production/boot -XX:+HeapDumpOnOutOfMemoryError -Xmx512m -XX:MaxPermSize=320m -Didea.system.path=./build/idea-sandbox/system-test -Didea.home.path=../intellij-community -Didea.config.path=./build/idea-sandbox/config -Didea.test.group=ALL_EXCLUDE_DEFINED -Didea.load.plugins.id=com.github.rorry.optional.api.inspection.plugin
