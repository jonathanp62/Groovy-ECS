package net.jmp.ecs

/*
 * (#)Formatter.groovy  1.0.0   02/17/2026
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

import groovy.json.JsonSlurper

/**
 * The formatter class for the ECS application
 */
class Formatter {
    /** The JSON file */
    private File jsonFile

    /** The pretty print flag */
    private boolean prettyPrint

    /**
     * The constructor
     *
     * @param   jsonFile    File    The JSON file
     * @param   prettyPrint boolean The pretty print flag
     */
    Formatter(File jsonFile, boolean prettyPrint) {
        this.jsonFile = jsonFile
        this.prettyPrint = prettyPrint
    }

    /**
     * Format the JSON file
     *
     * @return  int The exit code
     */
    int format() {
        def jsonSlurper = new JsonSlurper()
        int exitCode = 0

        this.jsonFile.eachLine { line ->
            try {
                def entry = jsonSlurper.parseText(line)

                def timestamp = entry.'@timestamp' ?: 'N/A'
                def level = entry.'log.level'?.toUpperCase() ?: 'N/A'
                def message = entry.message ?: 'N/A'
                def version = entry.'service.version' ?: 'N/A'

                println "[${timestamp}] [${version}] ${level.padRight(5)} ${message}"
            } catch (Exception e) {
                System.err.println "ecs: Exception: ${e.message}"
                System.err.println "ecs: Error parsing line: ${line}"

                exitCode = 1
            }
        }

        return exitCode
    }
}
