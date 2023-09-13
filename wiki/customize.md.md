### 项目定制化方案

1. 程序通过接口抽象，不同项目自己实现，可通过`mvn -Pxxx`打包设置不同的profile，或者不同项目
2. 通过动态代理, 一般使用`Spring AOP`在运行时实现，可用改动比较少的时候
3. 通过`Java agent`
    - 此时可使用`Java agent + javassist`自己实现
    - 也可以使用`aspectjweaver`
4. 通过`aspectj`在编译期拦截

### AspectJ
```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>aspectj-maven-plugin</artifactId>
                <version>1.11</version>
                <configuration>
                    <complianceLevel>1.8</complianceLevel>
                    <source>1.8</source>
                    <target>1.8</target>
                    <showWeaveInfo>true</showWeaveInfo>
                    <verbose>true</verbose>
                    <Xlint>ignore</Xlint>
                    <encoding>UTF-8</encoding>
                    <excludes>
                        <exclude>src/main/**/*.java</exclude>
                    </excludes>
                    <forceAjcCompile>true</forceAjcCompile>
                    <sources />
                    <weaveDirectories>
                        <weaveDirectory>${project.build.directory}/classes</weaveDirectory>
                    </weaveDirectories>
                    <aspectLibraries>
                        <aspectLibrary>
                            <groupId>com.ofllibnnb.customize</groupId>
                            <artifactId>customize-aspectj</artifactId>
                        </aspectLibrary>
                    </aspectLibraries>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```
