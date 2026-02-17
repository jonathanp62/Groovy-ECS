package net.jmp.ecs

/*
 * (#)Runner.groovy 1.0.0   02/16/2026
 *
 * @author    Jonathan Parker
 * @version   1.0.0
 * @since     1.0.0
 *
 * MIT License
 *
 * Copyright (c) 2026 Jonathan M. Parker
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
 * The runner class for the ECS application
 */
class Runner {
    /** The version of the application */
    private String version

    /** The list of command line arguments */
    private List<String> args

    /** The command line interface */
    private def cli

    /**
     * The constructor
     *
     * @param   version String          The version of the application
     * @param   args    List<String>    The list of command line arguments
     */
    Runner(String version, List<String> args) {
        this.version = version
        this.args = args
    }

    /**
     * The run method for the ECS application
     *
     * @return  int  The exit code
     */
    int run() {
        /* Setting up the command line interface */

        this.buildCli()

        if (this.args.isEmpty()) {
            this.cli.usage()
            return 1
        }

        def options = this.parseOptions()

        if (!options) return 1

        if (this.handleHelp(options) == 1) return 1
        if (this.handleVersion(options) == 1) return 1
        if (this.validateOptions(options) == 1) return 1

        return this.handleFile(options)
    }

    /**
     * The buildCli method for the ECS application
     */
    private void buildCli() {
        this.cli = new CliBuilder(usage: 'ecs [options] <file>')

        this.cli.v(longOpt: 'version', 'Show version information')
        this.cli.h(longOpt: 'help', 'Show help usage')
        this.cli.f(longOpt: 'file', args: 1, argName: 'file', 'Target file to process')
        this.cli.p(longOpt: 'pretty-print', 'Pretty print output (requires --file)')
    }

    /**
     * Parse the command line arguments
     *
     * @return  def The parsed options
     */
    private def parseOptions() {
        def options = this.cli.parse(this.args)

        if (!options) return    // Parse error already reported by CliBuilder

        return options
    }

    /**
     * Handle the version option
     *
     * @param   options def The parsed options
     * @return  int         The exit code
     */
    private int handleVersion(def options) {
        if (options.v) {
            if (this.version == null) {
                System.err.println "ecs: Version unavailable"
                return 1
            } else {
                println "ecs: Version $this.version"
                return 1
            }
        }

        return 0    // Not handled
    }

    /**
     * Handle the help option
     *
     * @param   options def The parsed options
     * @return  int         The exit code
     */
    private int handleHelp(def options) {
        if (options.h) {
            this.cli.usage()
            return 1
        }

        return 0    // Not handled
    }

    /**
     * Validate the parsed options
     *
     * @param   options def The parsed options
     * @return  int         The exit code
     */
    private int validateOptions(def options) {
        /* Enforce dependency: --pretty-print requires --file */

        if (options.p && !options.f) {
            System.err.println "ecs: --pretty-print requires --file"
            this.cli.usage()
            return 1
        }

        return 0    // Not handled
    }

    /**
     * Handle the file option
     *
     * @param   options def The parsed options
     * @return  int         The exit code
     */
    private int handleFile(def options) {
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
                return 1
            }

            println "ecs: File: $file.name"

            Formatter formatter = new Formatter(file, options.p)

            return formatter.format()
        }

        return 1    // Not handled
    }
}
