// SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

library(
    identifier: 'jenkins-lib-common@v4.1.4',
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
                gitMetadata()
            }
        }

        stage('Maven') {
            steps {
                mavenStage(
                    postBuildScript : 'tar czf package/carbonio-catalog-quarkus.tar.gz -C target/ quarkus-app',
                )
            }
        }

        stage('Publish containers') {
            steps {
                dockerStage([
                        dockerfile: 'Dockerfile',
                        imageName : 'carbonio-catalog',
                        platforms: ['linux/amd64', 'linux/arm64'] as Set,
                        ocLabels  : [
                                title          : 'Carbonio Catalog Service'
                        ]
                ])
                dockerStage([
                        dockerfile: 'Dockerfile-sidecar',
                        imageName : 'carbonio-catalog-sidecar',
                        platforms: ['linux/amd64', 'linux/arm64'] as Set,
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
                uploadStage()
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
