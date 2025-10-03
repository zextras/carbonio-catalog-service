// SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

library(
    identifier: 'jenkins-packages-build-library@1.0.4',
    retriever: modernSCM([
        $class: 'GitSCMSource',
        remote: 'git@github.com:zextras/jenkins-packages-build-library.git',
        credentialsId: 'jenkins-integration-with-github-account'
    ])
)

pipeline {
    agent {
        node {
            label 'zextras-v1'
        }
    }

    environment {
        JAVA_OPTS = '-Dfile.encoding=UTF8'
        LC_ALL = 'C.UTF-8'
        MVN_OPTS = '-B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '25'))
        skipDefaultCheckout()
        timeout(time: 15, unit: 'MINUTES')
    }

    parameters {
        booleanParam defaultValue: false,
            description: 'Whether to upload the packages in playground repositories',
            name: 'PLAYGROUND'
    }

    tools {
        jfrog 'jfrog-cli'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    gitMetadata()
                }
            }
        }

        stage('Build') {
            steps {
                container('jdk-17') {
                    sh """
                        mvn ${MVN_OPTS} -DskipTests clean package
                        tar czf package/carbonio-catalog-quarkus.tar.gz -C target/ quarkus-app
                    """
                }
            }
        }

        stage('Publish containers') {
            steps {
                container('dind') {
                    withDockerRegistry(credentialsId: 'private-registry', url: 'https://registry.dev.zextras.com') {
                        sh 'docker build ' +
                                '--label org.opencontainers.image.title="Carbonio Catalog Service" ' +
                                '--label org.opencontainers.image.description="Carbonio Catalog Service for service discovery" ' +
                                '--label org.opencontainers.image.vendor="Zextras" ' +
                                '-f Dockerfile -t registry.dev.zextras.com/dev/carbonio-catalog-service:latest .'
                        sh 'docker push registry.dev.zextras.com/dev/carbonio-catalog-service:latest'
                    }
                }
            }
        }

        stage('Tests') {
            when {
                expression { params.SKIP_TESTS == false }
            }
            steps {
                container('jdk-17') {
                    sh "mvn ${MVN_OPTS} -Dmaven.test.redirectTestOutputToFile=true verify"
                }
            }
            post {
                always {
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube analysis') {
            when {
                expression { params.SKIP_TESTS == false }
            }
            environment {
                SCANNER_HOME = tool 'SonarScanner'
            }
            steps {
                container('jdk-17') {
                    withSonarQubeEnv(credentialsId: 'sonarqube-user-token', installationName: 'SonarQube instance') {
                        sh "mvn ${MVN_OPTS} sonar:sonar"
                    }
                }
            }
        }

        stage('Deploy') {
            when {
                anyOf {
                    branch 'devel'
                    buildingTag()
                }
            }
            steps {
                container('jdk-17') {
                    withCredentials([file(credentialsId: 'jenkins-maven-settings.xml', variable: 'SETTINGS_PATH')]) {
                        sh "mvn ${MVN_OPTS} -s " +  SETTINGS_PATH + '-DskipTests deploy'
                    }
                }
            }
        }

        stage('Build deb/rpm') {
            steps {
                echo 'Building deb/rpm packages'
                buildStage()
            }
        }

        stage('Upload artifacts')
        {
            steps {
                uploadStage(
                    packages: yapHelper.getPackageNames()
                )
            }
        }
    }
}
