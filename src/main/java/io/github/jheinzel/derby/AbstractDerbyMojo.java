package io.github.jheinzel.derby;

import java.net.InetAddress;
import java.nio.file.Paths;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

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

public abstract class AbstractDerbyMojo extends AbstractMojo {

  @Parameter(property="project", readonly=true, defaultValue="${project}")
  private MavenProject project;

  @Parameter(property="basedir", defaultValue="${basedir}")
  private String basedir;

  @Parameter(property="derby.port")
  private int port;

  @Parameter(property="derby.username", defaultValue="derby")
  private String username;

  @Parameter(property="derby.password", defaultValue="derby")
  private String password;

  @Parameter(property="derby.driver")
  private String driver;

  @Parameter(property="derby.url")
  private String connectionURL;

  @Parameter(property="derby.url.shutdown")
  private String connectionURLShutdown;

  @Parameter(property="derby.home", defaultValue="${project.basedir}/data")
  private String derbyHome;

  @Parameter(property="derby.debug", defaultValue="true")
  private boolean debugStatements;

  /* Shared {@link NetworkServerControl} instance for all mojos. */
  protected NetworkServerControl server;

  /*
   * Delegates the mojo execution to {@link #doExecute()} after initializing the
   * {@link NetworkServerControl} for localhost
   * 
   * @throws MojoExecutionException
   * @throws MojoFailureException
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    setupDerby();

    doExecute();
  }

  protected void setupDerby() throws MojoExecutionException {
    System.setProperty("derby.system.home", getDerbyHome());
    System.setProperty("derby.language.logStatementText", String.valueOf(debugStatements));

    try {
      final InetAddress localHost =
          InetAddress.getByAddress("localhost", new byte[] { 127, 0, 0, 1 });
      getLog().info("Initializing Derby server control for " + localHost);

      getLog().info("  username: " + getUsername());
      getLog().info("  password: " + getPassword());
      
      server = new NetworkServerControl(localHost, getPort(), getUsername(), getPassword());
    }
    catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  /**
   * Implement mojo logic here.
   * 
   * @throws MojoExecutionException
   * @throws MojoFailureException
   */
  protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;

  public MavenProject getProject() {
    return project;
  }

  public void setProject(MavenProject project) {
    this.project = project;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConnectionURL() {
    return connectionURL;
  }

  public void setConnectionURL(String connectionURL) {
    this.connectionURL = connectionURL;
  }

  public String getConnectionURLShutdown() {
    return connectionURLShutdown;
  }

  public void setConnectionURLShutdown(String connectionURLShutdown) {
    this.connectionURLShutdown = connectionURLShutdown;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getBasedir() {
    return basedir;
  }

  public void setBasedir(String basedir) {
    this.basedir = Paths.get(basedir).toString();
  }

  public String getDerbyHome() {
    return derbyHome;
  }

  public void setDerbyHome(String derbyHome) {
    this.derbyHome = Paths.get(derbyHome).toString();
  }

  public boolean isDebugStatements() {
    return debugStatements;
  }

  public void setDebugStatements(boolean debugStatements) {
    this.debugStatements = debugStatements;
  }

}
