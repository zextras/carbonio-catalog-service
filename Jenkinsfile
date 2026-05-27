// SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

library(
    identifier: 'jenkins-lib-common@v2.8.7',
    retriever: modernSCM([
        $class: 'GitSCMSource',
        remote: 'git@github.com:zextras/jenkins-lib-common.git',
        credentialsId: 'jenkins-integration-with-github-account'
    ])
)

properties(defaultPipelineProperties())

pipeline {
    agent {
        node {
            label 'zextras-v1'
        }
    }

    environment {
        JAVA_OPTS = '-Dfile.encoding=UTF8'
        LC_ALL = 'C.UTF-8'
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

        stage('Maven') {
            steps {
                mavenStage(
                    profile         : '',
                    deployArtifacts : false,
                    extraTestArgs   : '-Dmaven.test.redirectTestOutputToFile=true',
                    postBuildScript : 'tar czf package/carbonio-catalog-quarkus.tar.gz -C target/ quarkus-app',
                )
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

        stage('Build deb/rpm') {
            steps {
                echo 'Building deb/rpm packages'
                buildStage([
                    buildFlags: ' -ds ',
                ])
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
