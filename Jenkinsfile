// SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

library(
    identifier: 'jenkins-lib-common@1.3.3',
    retriever: modernSCM([
        $class: 'GitSCMSource',
        remote: 'git@github.com:zextras/jenkins-lib-common.git',
        credentialsId: 'jenkins-integration-with-github-account'
    ])
)

properties(defaultPipelineProperties())

boolean isBuildingTag() {
    return env.TAG_NAME ? true : false
}

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

    stages {
        stage('Setup') {
            steps {
                checkout scm
                script {
                    gitMetadata()
                }
            }
        }

        stage('Build') {
            steps {
                container('jdk-21') {
                    sh """
                        mvn ${MVN_OPTS} -DskipTests clean package
                        tar czf package/carbonio-catalog-quarkus.tar.gz -C target/ quarkus-app
                    """
                }
            }
        }

        stage('Tests') {
            when {
                expression { params.SKIP_TESTS == false }
            }
            steps {
                container('jdk-21') {
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
                container('jdk-21') {
                    withSonarQubeEnv(credentialsId: 'sonarqube-user-token', installationName: 'SonarQube instance') {
                        sh "mvn ${MVN_OPTS} sonar:sonar"
                    }
                }
            }
        }
        stage('Publish containers') {
            steps {
                dockerStage([
                        dockerfile: 'Dockerfile',
                        imageName : 'carbonio-catalog',
                        ocLabels  : [
                                title          : 'Carbonio Catalog Service'
                        ]
                ])
                dockerStage([
                        dockerfile: 'Dockerfile-sidecar',
                        imageName : 'carbonio-catalog-sidecar',
                        ocLabels  : [
                                title : 'Carbonio Catalog Service Sidecar',
                        ]
                ])

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
                container('jdk-21') {
                    withCredentials([file(credentialsId: 'jenkins-maven-settings.xml', variable: 'SETTINGS_PATH')]) {
                        sh "mvn ${MVN_OPTS} -s " +  SETTINGS_PATH + ' -DskipTests deploy'
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
            when {
                expression { return uploadStage.shouldUpload() }
            }
            tools {
                jfrog 'jfrog-cli'
            }
            steps {
                uploadStage(
                    packages: yapHelper.resolvePackageNames()
                )
            }
        }
        stage('Bump version') {
            steps {
                script {
                    dt2_semanticRelease()
                }
            }
        }
    }
}
