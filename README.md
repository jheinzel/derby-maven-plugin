# derby-maven-plugin

Simple Maven plugin to start/stop a file-based derby server instance. 
Fork of <https://github.com/carlspring/derby-maven-plugin> created by Martin Todorov.

## POM integration

```xml
<project ...>
    <build>
        <plugins>
            <plugin>
               <groupId>io.github.jheinzel</groupId>
               <artifactId>derby-maven-plugin</artifactId>
               <version>1.5</version>
               <configuration>
                   <derbyHome>${project.basedir}/data</derbyHome>
                   <port>1527</port>
                   <database>MyDatabase</database>
                   <username>user</username>
                   <password>test</password>
               </configuration>
            </plugin>            
        </plugins>
    </build>
</project>
```

## Configuration properties

* `derbyHome`: Location of database files generated be the derby server.
* `database`: Name of the database hosted be the derby instance
* `port`: Port the derby server is listening on. Default ist 1527.
* `username`:
* `password`:

## Maven commands

* `mvn derby:run`
   Will start your derby instance on the configured port and block until stopped.

* `mvn derby:stop`
   Will stop a running derby instance.

* `mvn derby:drop-db`
   Will delete the database directory.

## Connection String

The database can be accessed using the following JDBC connection string:

```
jdbc:derby://localhost[:{port}]/{database name};create=true
```