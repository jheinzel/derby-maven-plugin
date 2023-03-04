package io.github.jheinzel.derby;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import java.sql.DriverManager;
import java.sql.SQLException;

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

@Mojo(name = "stop", requiresProject = false)
public class StopDerbyMojo extends AbstractDerbyMojo {

  @Parameter(property = "derby.fail.if.not.running", readonly = true, defaultValue = "false")
  boolean failIfNotRunning;

  @Override
  public void doExecute() throws MojoExecutionException, MojoFailureException {
    try {
      try {
        server.ping();
      }
      catch (Exception e) {
        if (failIfNotRunning) {
          getLog().error("Derby server has already been stopped.");
          throw new MojoExecutionException("Failed to stop the Derby server, no server running!", e);
        }

        getLog().info("Derby server has already been stopped.");
        return;
      }

      try {
        DriverManager.getConnection(getConnectionURL());
        DriverManager.getConnection("jdbc:derby:;shutdown=true");
      }
      catch (SQLException e) {
      }

      server.shutdown();

      while (true) {
        Thread.sleep(1000);
        try {
          server.ping();
        }
        catch (Exception e) {
          getLog().info("Derby has stopped!");
          break;
        }
      }

      System.getProperties().remove("derby.system.home");
    }
    catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  public boolean isFailIfNotRunning() {
    return failIfNotRunning;
  }

  public void setFailIfNotRunning(boolean failIfNotRunning) {
    this.failIfNotRunning = failIfNotRunning;
  }

}
