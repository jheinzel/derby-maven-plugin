## Literature
* [Publishing my artifact](<https://central.sonatype.org/publish/requirements/#supply-javadoc-and-sources>)

## Prepare
```sh
gpg --full-generate-key
gpg --keyserver https://keys.openpgp.org --send-keys <key>
gpg --keyserver http://keyserver.ubuntu.com:11371 --send-keys <key>
```

## Deploy
* Java 17: 
  ```sh
  $Env:MAVEN_OPTS="--add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt.font=ALL-UNNAMED"
  ```

* Deploy to stating environment. Also automatically releases the package, if `autoReleaseAfterClose` is set to `true`. 
  ```sh
  mvn clean deploy
  ```
* If `autoReleaseAfterClose` is set to `false`:
    ```sh
    mvn nexus-staging:release
    ```
