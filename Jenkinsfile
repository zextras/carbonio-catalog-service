// SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

library(
    identifier: 'jenkins-lib-common@1.1.2',
    retriever: modernSCM([
        $class: 'GitSCMSource',
        remote: 'git@github.com:zextras/jenkins-lib-common.git',
        credentialsId: 'jenkins-integration-with-github-account'
    ])
)

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
                    properties(defaultPipelineProperties())
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
        stage('Publish containers') {
            when {
                expression {
                    return isBuildingTag() || env.BRANCH_NAME == 'devel'
                }
            }
            steps {
                container('dind') {
                    withDockerRegistry(credentialsId: 'private-registry', url: 'https://registry.dev.zextras.com') {
                        script {
                            Set<String> tagVersions = []
                            if (isBuildingTag()) {
                                tagVersions = [env.TAG_NAME, 'stable']
                            } else {
                                tagVersions = ['devel', 'latest']
                            }
                            dockerHelper.buildImage([
                                    dockerfile: 'Dockerfile',
                                    imageName : 'registry.dev.zextras.com/dev/carbonio-catalog',
                                    imageTags : tagVersions,
                                    ocLabels  : [
                                            title          : 'Carbonio Catalog',
                                            version        : tagVersions[0]
                                    ]
                            ])
                        }
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
    }
}
