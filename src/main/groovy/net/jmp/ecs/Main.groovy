package net.jmp.ecs

/*
 * (#)Main.groovy   1.0.0   02/16/2026
 *
 * @author    Jonathan Parker
 * @version   1.0.0
 * @since     1.0.0
 *
 * MIT License
 *
 * Copyright (c) 2025, 2026 Jonathan M. Parker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import groovy.cli.picocli.CliBuilder

/**
 * The main class for the ECS application
 *
 * @param   args    String[]    The command line arguments
 */
static void main(String[] args) {
    /* Getting the version from the build.gradle file only works for jar deployments */

    int exitValue = run(getClass().package.implementationVersion, args as List<String>)

    System.exit(exitValue)
}

/**
 * The run method for the ECS application
 *
 * @param   version String          The version of the application
 * @param   args    List<String>    The command line arguments
 */
static int run(String version, List<String> args) {
    /* Setting up the command line interface */

    def cli = new CliBuilder(usage: 'ecs [options] <file>')

    if (args.isEmpty()) {
        cli.usage()
        return 1
    } else {
        cli.v(longOpt: 'version', 'Show version information')
        cli.h(longOpt: 'help', 'Show help usage')
        cli.f(longOpt: 'file', args: 1, argName: 'file', 'Target file to process')
        cli.p(longOpt: 'pretty-print', 'Pretty print output (requires --file)')

        def options = cli.parse(args)

        if (!options) return    // Parse error already reported by CliBuilder

        /* Help */

        if (options.h) {
            cli.usage()
            return 1
        }

        /* Enforce dependency: --pretty-print requires --file */

        if (options.p && !options.f) {
            System.err.println "ecs: --pretty-print requires --file"
            cli.usage()
            return 1
        }

        /* File */

        if (options.f) {
            def file = new File(options.f)

            if (!file.exists()) {
                System.err.println "ecs: File ${options.f} does not exist"
                return 1
            }

            if (!file.isFile()) {
                System.err.println "ecs: File ${options.f} is not a file"
                return 1
            }

            if (!file.name.endsWith(".json")) {
                System.err.println "ecs: File ${options.f} is not a JSON file"
                return
            }

            println "ecs: File: $file.name"

            if (options.p) {
                println "ecs: Pretty printing enabled"
            }

            return 0
        }

        /* Version */

        if (options.v) {
            if (version == null) {
                System.err.println "ecs: Version unavailable"
                return 1
            } else {
                println "ecs: Version $version"
                return 0
            }
        }
    }
}