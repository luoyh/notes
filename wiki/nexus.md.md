
https://help.sonatype.com/repomanager3/rest-and-integration-api/components-api


```shell

snaf() {
    dn="$1"
    file="$2"
    src="$3"
    jar=$4
    ss=`echo "$dn" | sed "s|\./||" | sed "s|/| |g"`
    groupid=""
    artifactid=""
    version=""
    for v in ${ss[@]}
    do
        if [ -n "$artifactid" ]; then
            if [ -z "$groupid" ]; then
               groupid="$artifactid"
            else
                groupid="$groupid.$artifactid"
            fi
        fi
        artifactid="$version"
        version="$v"
    done
    if [ $src -eq 1]; then
        mvn deploy:deploy-file -s /data/.m22/settings.xml \
            -DgroupId=$groupid \
            -DartifactId=$artifactdid \
            -Dversion=$version \
            -Dpackaging=jar \
            -Dfile=${file}.jar \
            -Durl=http://localhost:8081/repository/maven-snapshots/ \
            -DrepositoryId=nexus \
            -Dfiles=${file}-sources.jar \
            -Dclassifier=sources \
            -Dtypes=jar
            -DpomFile=${file}.pom
    fi
}



for ds in `find . -type d`
do
    if [ "$ds" != "." ]; then
        jar=0
        pom=0
        src=0
        file=""
        sna=0
        for fs in `find $ds -maxdepth 1 -type f`
        do
            if [ `echo $fs | grep ".pom$" | wc -l` -gt 0 ]; then
                pom=1
                file=`echo $fs | sed "s|\.pom||"`
            fi

            if [ `echo $fs | grep ".jar$" | wc -l` -gt 0 ]; then
                jar=1
            fi

            if [ `echo $fs | grep "\-SNAPSHOT.pom$" | wc -l` -gt 0 ]; then
                sna=1
            fi
        done
        
        if [ $pom -eq 1 ]; then
            echo "upload $file, jar=$jar, src=$src, sna=$sna"
            if [ $sna -eq 1 ]; then
                snaf $ds $file $src $jar
            else
                if [ $src -eq 1 ]; then
                    curl -X POST -u admin:admin123 \
                        http://localhost:8081/service/rest/v1/components?repository=maven-releases \
                        -F "maven2.generate-pom=false" \
                        -F "maven2.asset1=@${file}.pom" \
                        -F "maven2.asset1.extension=pom" \
                        -F "maven2.asset2=@${file}.jar;type=application/java-archive" \
                        -F "maven2.asset2.extension=jar" \
                        -F "maven2.asset3=@${file}-sources.jar" \
                        -F "maven2.asset3.extension=jar" \
                        -F "maven2.asset3.classifier=sources" 

                fi
            fi


        fi # !end $pom == 1

    fi # !end $ds == .
done

```

### nexus front

```shell
npm config set registry http://ofllibnnb.com/nexus/repository/npm-test-group/

for ds in `find . -type d -name node_modules`
do
    for ns in `find $ds -maxdepth 1 -type d ! -name "\.*"`
    do
        echo "npm publish --registry=http://ofllibnnb.com/nexus/repository/npm-test-hosted/ $ns" >> publish-to-nexus-front.sh
        if [ `basename $ns | grep ^@ | wc -l` -gt 0 ]; then
            for child in `ls $ns`
            do
                echo "npm publish --registry=http://ofllibnnb.com/nexus/repository/npm-test-hosted/ $child" >> publish-to-nexus-front.sh
            done
        fi
    done
done

```
