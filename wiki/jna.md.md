
### https://repo1.maven.org/maven2/com/nativelibs4java/jnaerator/0.12/


```bash
java -jar jnaerator-0.12-shaded.jar \
 -runtime JNA \
 -mode Maven \
 -mavenGroupId com.luoyh.jna \
 -mavenArtifactId jna-test \
 -o jna-test-src \
 -package com.luoyh.jna.test \
 -f -library jna-test.dll \
 jna-test.dll jna-test.h
```