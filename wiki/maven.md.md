### mvn support multil JDK version
```
# jdk8 and jdk20 support
# and default jdk8
cp maven_path/bin/mvn.cmd to maven maven_path/bin/mvn20.cmd
# edit mvn20.cmd at line 46 after set ERROR_CODE=0 
set "JAVA_HOME=E:\local\openjdk-20.0.1_windows-x64_bin\jdk-20.0.1"

```

### maven project pom.xml setting repositories not work
```
<repositories>
        <repository>
            <id>hsweb-nexus</id>
            <url>https://nexus.hsweb.me/content/groups/public/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>

# update maven_path/conf/settings.xml 
# mirror node <mirrorOf>*</mirrorOf> to
<mirrorOf>*,!hsweb-nexus</mirrorOf>

* = everything
external:* = everything not on the localhost and not file based.
repo,repo1 = repo or repo1
*,!repo1 = everything except repo1
```


