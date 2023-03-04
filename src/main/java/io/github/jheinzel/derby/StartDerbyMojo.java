package io.github.jheinzel.derby;

import java.io.PrintWriter;
import java.net.BindException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Copyright 2012 Martin Todorov.
 * Modification Copyright 2023 Johann Heinzelreiter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 * This version of the software is a modified fork of the
 * original project (<https://github.com/carlspring/derby-maven-plugin>)
 * that references a recent version of derby, adds additional configuration
 * options, and offers a command to delete the database directory.
 */

@Mojo(name="start", requiresProject=false)
public class StartDerbyMojo extends AbstractDerbyMojo {

  @Parameter(property="derby.debug", defaultValue="true")
  private boolean debugStatements;

  @Parameter(property="derby.fail.if.already.running", defaultValue="true")
  private boolean failIfAlreadyRunning;

  @Override
  public void doExecute() throws MojoExecutionException, MojoFailureException {
    try {
      try {
        getLog().info("Starting the Derby server ...");
        server.start(new PrintWriter(System.out));
      }
      catch (Exception e) {
        if (e instanceof BindException) {
          if (failIfAlreadyRunning) {
            throw new MojoExecutionException(
                "Failed to start the Derby server, port already open!", e);
          }
          else {
            getLog().info("Derby is already running.");
          }
        }
        else {
          throw new MojoExecutionException(e.getMessage(), e);
        }
      }

      if (server != null) {        
        long maxSleepTime = 60000;
        long sleepTime = 0;
        boolean pong = false;

        while (!pong && sleepTime < maxSleepTime) {
          try {
            server.ping();
            pong = true;
          }
          catch (Exception e) {
            sleepTime += 1000;
            Thread.sleep(1000);
          }
        }

        if (pong) {
          getLog().info("Derby ping-pong: [OK]");
        }
        else {
          getLog().info("Derby ping-pong: [FAILED]");
          throw new MojoFailureException("Failed to start the NetworkServerControl."
              + " The server did not respond with a pong withing 60 seconds.");
        }
      }
      else {
        throw new MojoExecutionException("Failed to start the Derby server!");
      }
    }
    catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  public boolean isFailIfAlreadyRunning() {
    return failIfAlreadyRunning;
  }

  public void setFailIfAlreadyRunning(boolean failIfAlreadyRunning) {
    this.failIfAlreadyRunning = failIfAlreadyRunning;
  }

}
