package io.github.jheinzel.derby;

import java.io.File;
import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
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

@Mojo(name="drop-db", requiresProject=false)
public class DropDbMojo extends AbstractMojo {

  @Parameter(property="derby.database")
  private String database;

  @Parameter(property="derby.home")
  private String derbyHome;

  
  public String getDerbyHome() {
    return derbyHome;
  }

  public void setDerbyHome(String derbyHome) {
    this.derbyHome = derbyHome;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      if (getDatabase() == null)
         getLog().error(String.format("Specify database to delete (<database>myDatabaseDir</database> or -Dderby.database=myDatabaseDir)"));
      else {
        File databaseDir = Paths.get(getDerbyHome(), getDatabase()).toFile();
        if (databaseDir.exists()) {
          if (deleteDirectory(databaseDir))
            getLog().info(String.format("Database directory \"%s\" deleted.", databaseDir));
          else {
            getLog().error(String.format("Cannot delete database directory \"%s\".", databaseDir));
            getLog().error("Stop derby server if it is still running.");
          }
        }
        else
          getLog().info(String.format("Database directory \"%s\" does not exist.",
            databaseDir));
      }
    }
    catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private boolean deleteDirectory(File path) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           if (! deleteDirectory(files[i]))
             return false;
         }
         else {
           if (! files[i].delete())
             return false;
         }
      }
    }
    
    return path.delete();
  }

}
