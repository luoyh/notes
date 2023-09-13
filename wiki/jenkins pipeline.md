```
pipeline {
   agent any
   
   stages {
      stage('common') {
          steps {
              dir('common') {
                git branch: '${common-branch}', credentialsId: 'ffcdb8c0-86d5-44fe-85f1-d8d65b60fcb3', url: 'http://ofllibnnb.com/g2/common.git'
                sh "mvn clean install -Dmaven.test.skip=true -U"
              }
          }
      }
      stage('pre-build') {
         steps {
            dir('antifraud') {
                git branch: '${preapp-branch}', credentialsId: 'ffcdb8c0-86d5-44fe-85f1-d8d65b60fcb3', url: 'http://ofllibnnb.com/g2/preapp.git'
                script {
                    PRE_VERSION=sh(script: 'mvn -q -N org.codehaus.mojo:exec-maven-plugin:3.0.0:exec -Dexec.executable=echo -Dexec.args=\'\${project.version}\'',returnStdout:true).trim()
                }
                echo "PRE_VERSION=${PRE_VERSION}"
                sh "mvn versions:set versions:commit -DnewVersion=${PRE_VERSION}-VC -Dmaven.test.skip=true"
                script {
                    if (params.uwa) {
                        sh "mvn clean install -Dmaven.test.skip=true -U -P usewa"
                    } else {
                        sh "mvn clean install -Dmaven.test.skip=true -U -P customize"
                    }
                }
                # sh "mvn clean install -Dmaven.test.skip=true -U -P customize"
                
            }
         }
      }
      stage('build') {
          steps {
              dir('app') {
                git credentialsId: 'ffcdb8c0-86d5-44fe-85f1-d8d65b60fcb3', url: 'http://ofllibnnb.com/g2/appc.git'
                sh "sed -i 's#<ofllibnnb.preapp.version>${PRE_VERSION}</ofllibnnb.preapp.version>#<ofllibnnb.preapp.version>${PRE_VERSION}-VC</ofllibnnb.preapp.version>#g' pom.xml"
                sh "mvn clean package -Dmaven.test.skip=true -U"
                script {
                    POM_ARTIFACTID=sh(script: 'mvn -q -N org.codehaus.mojo:exec-maven-plugin:3.0.0:exec -Dexec.executable=echo -Dexec.args=\'\${project.artifactId}\'',returnStdout:true).trim()
                    POM_VERSION=sh(script: 'mvn -q -N org.codehaus.mojo:exec-maven-plugin:3.0.0:exec -Dexec.executable=echo -Dexec.args=\'\${project.version}\'',returnStdout:true).trim()
                }
                echo "${POM_ARTIFACTID}-${POM_VERSION}.jar"
                /*
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'appc.120', 
                        transfers: [
                            sshTransfer(
                                cleanRemote: false, 
                                excludes: '', 
                                execCommand: """
cd /home/appc/.build
sh build.sh appc/startup $POM_VERSION.$BUILD_NUMBER appc
                                """, 
                                execTimeout: 120000, 
                                flatten: false, 
                                makeEmptyDirs: false, 
                                noDefaultExcludes: false, 
                                patternSeparator: '[, ]+', 
                                remoteDirectory: 'appc/.build', 
                                remoteDirectorySDF: false, 
                                removePrefix: 'target/', 
                                sourceFiles: 'target/*.jar')], 
                                usePromotionTimestamp: false, 
                                useWorkspaceInPromotion: false, 
                                verbose: false)
                ])
                */
              }
          }
      }
   }
}
```