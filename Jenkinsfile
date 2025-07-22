def mvnCmd(String cmd) {
  sh 'mvn --settings settings.xml -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn ' + cmd
}

def buildDebPackages(String flavor) {
    container('yap') {
        unstash 'staging'
        sh 'cp -r . /tmp/staging'
        if (BRANCH_NAME == 'devel') {
            def timestamp = new Date().format('yyyyMMddHHmmss')
            sh "yap build " + flavor + " /tmp/staging -r ${timestamp}"
        } else {
            sh 'yap build ' + flavor + ' /tmp/staging'
        }
        stash includes: 'artifacts/*.deb', name: 'artifacts-' + flavor
    }
}

def getPackages() {
    return ["carbonio-catalog"]
}

def getRpmSpec(String upstream, String version) {
    packages = getPackages()
    packageSpecList = []
    filesSpec = ""
    packages.each { item ->
        packageSpecList.add(generateRpmSpec(item, version, upstream))
    }
    return packageSpecList.join(",")
}

def generateRpmSpec(String packageName, String version, String upstream) {
    return """{
        "pattern": "artifacts/x86_64/(${packageName})-(*).el${version}.x86_64.rpm",
        "target": "${upstream}/zextras/{1}/{1}-{2}.el${version}.x86_64.rpm",
        "props": "rpm.metadata.arch=x86_64;rpm.metadata.vendor=zextras;vcs.revision=${env.GIT_COMMIT}"
    }
    """
}

def buildRpmPackages(String flavor) {
    container('yap') {
        unstash 'staging'
        sh 'cp -r . /tmp/staging'
        if (BRANCH_NAME == 'devel') {
            def timestamp = new Date().format('yyyyMMddHHmmss')
            sh "yap build " + flavor + " . -r ${timestamp}"
        } else {
            sh 'yap build ' + flavor + ' .'
        }
        stash includes: 'artifacts/*.rpm', name: 'artifacts-' + flavor
    }
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
        jenkins_build = 'true'
    }
    parameters {
        booleanParam defaultValue: false, description: 'Whether to upload the packages in playground repositories', name: 'PLAYGROUND'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '25'))
        timeout(time: 15, unit: 'MINUTES')
        skipDefaultCheckout()
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                withCredentials([file(credentialsId: 'jenkins-maven-settings.xml', variable: 'SETTINGS_PATH')]) {
                  sh 'cp ${SETTINGS_PATH} settings.xml'
                }
                script {
                  env.GIT_COMMIT = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
                }
            }
        }
        stage('Build') {
            steps {
                container('jdk-17') {
                    mvnCmd("-DskipTests clean package")
                    stash includes: 'yap.json,package/**,target/quarkus-app/**', name: 'staging'
                }
            }
        }
        stage('Test') {
            environment {
                SCANNER_HOME = tool 'SonarScanner'
            }
            steps {
                withSonarQubeEnv(credentialsId: 'sonarqube-user-token', installationName: 'SonarQube instance') {
                    container('jdk-17') {
                        mvnCmd("verify sonar:sonar")
                    }
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
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
                    mvnCmd("-DskipTests deploy")
                }
            }
        }
        stage('Build deb/rpm') {
            stages {
                stage('yap') {
                    parallel {
                        stage('Ubuntu 22') {
                            agent {
                                node {
                                    label 'yap-ubuntu-22-v1'
                                }
                            }
                            steps {
                                buildDebPackages("ubuntu-jammy")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: 'artifacts/*.deb', fingerprint: true
                                }
                            }
                        }
                        stage('Ubuntu 24') {
                            agent {
                                node {
                                    label 'yap-ubuntu-24-v1'
                                }
                            }
                            steps {
                                buildDebPackages("ubuntu-noble")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: 'artifacts/*.deb', fingerprint: true
                                }
                            }
                        }
                        stage('RHEL 8') {
                            agent {
                                node {
                                    label 'yap-rocky-8-v1'
                                }
                            }
                            steps {
                                buildRpmPackages("rocky-8")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: 'artifacts/*el8*.rpm', fingerprint: true
                                }
                            }
                        }
                        stage('RHEL 9') {
                            agent {
                                node {
                                    label 'yap-rocky-9-v1'
                                }
                            }
                            steps {
                                buildRpmPackages("rocky-9")
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: 'artifacts/*el9*.rpm', fingerprint: true
                                }
                            }
                        }
                    }
                }
            }
        }
        stage('Upload To Devel') {
            when {
                anyOf {
                    branch 'devel'
                }
            }
            steps {
                unstash 'artifacts-ubuntu-jammy'
                unstash 'artifacts-ubuntu-noble'
                unstash 'artifacts-rocky-8'
                unstash 'artifacts-rocky-9'

                script {
                    def server = Artifactory.server 'zextras-artifactory'
                    def buildInfo
                    def uploadSpec
                    buildInfo = Artifactory.newBuildInfo()
                    uploadSpec ="""{
                        "files": [
                        {
                            "pattern": "artifacts/*jammy*.deb",
                            "target": "ubuntu-devel/pool/",
                            "props": "deb.distribution=jammy;deb.component=main;deb.architecture=amd64;vcs.revision=${env.GIT_COMMIT}"
                        },
                        {
                            "pattern": "artifacts/*noble*.deb",
                            "target": "ubuntu-devel/pool/",
                            "props": "deb.distribution=noble;deb.component=main;deb.architecture=amd64;vcs.revision=${env.GIT_COMMIT}"
                        },""" + getRpmSpec("centos8-devel", "8") + """,""" + getRpmSpec("rhel9-devel", "9") + """
                        ]
                    }"""
                    server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
                }
            }
        }
        stage('Upload To Playground') {
            when {
                anyOf {
                    expression {
                        params.PLAYGROUND == true
                    }
                }
            }
            steps {
                unstash 'artifacts-ubuntu-jammy'
                unstash 'artifacts-ubuntu-noble'
                unstash 'artifacts-rocky-8'
                unstash 'artifacts-rocky-9'

                script {
                    def server = Artifactory.server 'zextras-artifactory'
                    def buildInfo
                    def uploadSpec
                    buildInfo = Artifactory.newBuildInfo()
                    uploadSpec ="""{
                        "files": [
                        {
                            "pattern": "artifacts/*jammy*.deb",
                            "target": "ubuntu-playground/pool/",
                            "props": "deb.distribution=jammy;deb.component=main;deb.architecture=amd64;vcs.revision=${env.GIT_COMMIT}"
                        },
                        {
                            "pattern": "artifacts/*noble*.deb",
                            "target": "ubuntu-playground/pool/",
                            "props": "deb.distribution=noble;deb.component=main;deb.architecture=amd64;vcs.revision=${env.GIT_COMMIT}"
                        },
                        """ + getRpmSpec("centos8-playground", "8") + """,""" + getRpmSpec("rhel9-playground", "9") + """]
                    }"""
                    server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
                }
            }
        }
        stage('Upload & Promotion Config') {
            when {
                anyOf {
                    branch 'release/*'
                    buildingTag()
                }
            }
            steps {
                unstash 'artifacts-ubuntu-jammy'
                unstash 'artifacts-ubuntu-noble'
                unstash 'artifacts-rocky-8'
                unstash 'artifacts-rocky-9'

                script {
                    def server = Artifactory.server 'zextras-artifactory'
                    def buildInfo
                    def uploadSpec
                    def config

                    //ubuntu
                    buildInfo = Artifactory.newBuildInfo()
                    buildInfo.name += '-ubuntu'
                    uploadSpec = """{
                        "files": [
                        {
                            "pattern": "artifacts/*jammy*.deb",
                            "target": "ubuntu-rc/pool/",
                            "props": "deb.distribution=jammy;deb.component=main;deb.architecture=amd64;vcs.revision=${env.GIT_COMMIT}"
                        },
                        {
                            "pattern": "artifacts/*noble*.deb",
                            "target": "ubuntu-rc/pool/",
                            "props": "deb.distribution=noble;deb.component=main;deb.architecture=amd64;vcs.revision=${env.GIT_COMMIT}"
                        }]
                        }"""
                    server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
                    config = [
                            'buildName': buildInfo.name,
                            'buildNumber': buildInfo.number,
                            'sourceRepo': 'ubuntu-rc',
                            'targetRepo': 'ubuntu-release',
                            'comment': 'Do not change anything! Just press the button',
                            'status': 'Released',
                            'includeDependencies': false,
                            'copy': true,
                            'failFast': true
                    ]
                    Artifactory.addInteractivePromotion server: server, promotionConfig: config, displayName: "Ubuntu Promotion to Release"
                    server.publishBuildInfo buildInfo

                    //centos8
                    buildInfo = Artifactory.newBuildInfo()
                    buildInfo.name += '-centos8'
                    uploadSpec = """{
                        "files": [""" + getRpmSpec("centos8-rc", "8") + """]
                    }"""
                    server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
                    config = [
                            'buildName': buildInfo.name,
                            'buildNumber': buildInfo.number,
                            'sourceRepo': 'centos8-rc',
                            'targetRepo': 'centos8-release',
                            'comment': 'Do not change anything! Just press the button',
                            'status': 'Released',
                            'includeDependencies': false,
                            'copy': true,
                            'failFast': true
                    ]
                    Artifactory.addInteractivePromotion server: server, promotionConfig: config, displayName: 'Centos8 Promotion to Release'
                    server.publishBuildInfo buildInfo

                    //rhel9
                    buildInfo = Artifactory.newBuildInfo()
                    buildInfo.name += '-rhel9'
                    uploadSpec = """{
                        "files": [""" + getRpmSpec("rhel9-rc", "9") + """
                        ]
                    }"""
                    server.upload spec: uploadSpec, buildInfo: buildInfo, failNoOp: false
                    config = [
                            'buildName': buildInfo.name,
                            'buildNumber': buildInfo.number,
                            'sourceRepo': 'rhel9-rc',
                            'targetRepo': 'rhel9-release',
                            'comment': 'Do not change anything! Just press the button',
                            'status': 'Released',
                            'includeDependencies': false,
                            'copy': true,
                            'failFast': true
                    ]
                    Artifactory.addInteractivePromotion server: server, promotionConfig: config, displayName: 'RHEL9 Promotion to Release'
                    server.publishBuildInfo buildInfo
                }
            }
        }
    }
}
