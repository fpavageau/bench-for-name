/*
 * Copyright 2014 Frank Pavageau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ekino.jmh;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappClassLoader;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(5)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
public class ClassReflectionBenchmark {
    private final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    private final WebappClassLoader simpleTomcatClassLoader = new WebappClassLoader(contextClassLoader);
    private final WebappClassLoader complex50TomcatClassLoader = new WebappClassLoader(contextClassLoader);
    private final WebappClassLoader complex100TomcatClassLoader = new WebappClassLoader(contextClassLoader);

    @Setup
    public void setup()
            throws LifecycleException, MalformedURLException {
        simpleTomcatClassLoader.start();

        for (int i = 0; i < 50; i++) {
            complex50TomcatClassLoader.addRepository(
                    new File("target/dummy" + i + ".jar").toURI().toURL().toExternalForm());
        }
        complex50TomcatClassLoader.start();

        for (int i = 0; i < 100; i++) {
            complex100TomcatClassLoader.addRepository(
                    new File("target/dummy" + i + ".jar").toURI().toURL().toExternalForm());
        }
        complex100TomcatClassLoader.start();
    }

    @GenerateMicroBenchmark
    public Object baseline() {
        return LoadedDirectly.class;
    }

    /**
     * Retrieves an existing class by reflection, using the context {@link ClassLoader}.
     */
    @GenerateMicroBenchmark
    public Object load_existing_context_cl() {
        try {
            return Class.forName("com.ekino.jmh.LoadedByReflection", true, contextClassLoader);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    /**
     * Tries to retrieve an unknown class by reflection, using the context {@link ClassLoader}.
     */
    @GenerateMicroBenchmark
    public Object load_unknown_context_cl() {
        try {
            return Class.forName("com.ekino.jmh.Unknown", true, contextClassLoader);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    /**
     * Retrieves an existing class by reflection, using a Tomcat {@link ClassLoader} but no external
     * dependencies to scan.
     */
    @GenerateMicroBenchmark
    public Object load_existing_simple_tomcat_cl() {
        try {
            return Class.forName("com.ekino.jmh.LoadedByReflection", true, simpleTomcatClassLoader);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    /**
     * Tries to retrieve an unknown class by reflection, using a Tomcat {@link ClassLoader} but no external
     * dependencies to scan.
     */
    @GenerateMicroBenchmark
    public Object load_unknown_simple_tomcat_cl() {
        try {
            return Class.forName("com.ekino.jmh.Unknown", true, simpleTomcatClassLoader);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    /**
     * Retrieves an existing class by reflection, using a Tomcat {@link ClassLoader} with 50 external
     * dependencies to scan.
     */
    @GenerateMicroBenchmark
    public Object load_existing_complex_50_tomcat_cl() {
        try {
            return Class.forName("com.ekino.jmh.LoadedByReflection", true, complex50TomcatClassLoader);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    /**
     * Tries to retrieve an unknown class by reflection, using a Tomcat {@link ClassLoader} with 50 external
     * dependencies to scan.
     */
    @GenerateMicroBenchmark
    public Object load_unknown_complex_50_tomcat_cl() {
        try {
            return Class.forName("com.ekino.jmh.Unknown", true, complex50TomcatClassLoader);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    /**
     * Retrieves an existing class by reflection, using a Tomcat {@link ClassLoader} with 100 external
     * dependencies to scan.
     */
    @GenerateMicroBenchmark
    public Object load_existing_complex_100_tomcat_cl() {
        try {
            return Class.forName("com.ekino.jmh.LoadedByReflection", true, complex100TomcatClassLoader);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    /**
     * Tries to retrieve an unknown class by reflection, using a Tomcat {@link ClassLoader} with 100 external
     * dependencies to scan.
     */
    @GenerateMicroBenchmark
    public Object load_unknown_complex_100_tomcat_cl() {
        try {
            return Class.forName("com.ekino.jmh.Unknown", true, complex100TomcatClassLoader);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }
}
