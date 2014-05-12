Loading a class by reflection
======

This is a micro-benchmark using [JMH](http://openjdk.java.net/projects/code-tools/jmh/) to compare the time it takes to fail to load a class by reflection, using different configurations of classloaders.

## What does it do?

The baseline just returns a class, not loaded by reflection. There are then 3 different configurations of classloader tested, loading either an existing class or an unknown class:

  1. Using the context classloader
  1. Using a Tomcat classloader without any external dependency configured
  1. Using a Tomcat classloader with 50 external dependencies configured (mostly empty jars, though)
  1. Using a Tomcat classloader with 100 external dependencies configured

## How to compile

The project uses Maven 2+, so just do:

        mvn clean package

## How to run

        java -jar target/microbenchmarks.jar

## Results

On my i7-2600 CPU @ 3.40GHz running Ubuntu Saucy and a 64-bit Oracle JDK (1.7u40), I get the following results (reordered to match the description of the configurations):

    Benchmark                                                              Mode   Samples         Mean   Mean error    Units
    c.e.j.ClassReflectionBenchmark.baseline                                avgt        25        0.002        0.000    us/op
    c.e.j.ClassReflectionBenchmark.load_existing_context_cl                avgt        25        0.583        0.004    us/op
    c.e.j.ClassReflectionBenchmark.load_existing_simple_tomcat_cl          avgt        25        0.583        0.005    us/op
    c.e.j.ClassReflectionBenchmark.load_existing_complex_50_tomcat_cl      avgt        25        0.581        0.010    us/op
    c.e.j.ClassReflectionBenchmark.load_existing_complex_100_tomcat_cl     avgt        25        0.573        0.004    us/op
    c.e.j.ClassReflectionBenchmark.load_unknown_context_cl                 avgt        25       22.854        0.280    us/op
    c.e.j.ClassReflectionBenchmark.load_unknown_simple_tomcat_cl           avgt        25       44.917        0.449    us/op
    c.e.j.ClassReflectionBenchmark.load_unknown_complex_50_tomcat_cl       avgt        25       64.229        1.112    us/op
    c.e.j.ClassReflectionBenchmark.load_unknown_complex_100_tomcat_cl      avgt        25       75.683        1.064    us/op

Loading an existing class (which is cached after the first load) is pretty much the same across configurations, as could be expected. Trying to load a class which is not present, however, is

  1. more expensive in all configurations, since the miss is not cached and an exception has to be thrown
  1. more and more expensive as the number of possible sources increases


------

Licensed under the Apache License, Version 2.0
