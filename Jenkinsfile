pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        APP_PORT = '8080'
        // Jika Java 25 terpasang di Jenkins lewat "Global Tool Configuration",
        // aktifkan blok tools di bawah dan sesuaikan namanya (mis. 'jdk25').
    }

    // tools {
    //     jdk 'jdk25'
    // }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Info') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'java -version'
                        sh 'chmod +x mvnw'
                    } else {
                        bat 'java -version'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script { mvn('clean compile') }
            }
        }

        stage('Test & Coverage') {
            steps {
                script { mvn('verify') }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Package') {
            steps {
                // Profil production wajib agar JAR Vaadin bisa dijalankan dengan java -jar
                script { mvn('clean package -Pproduction -DskipTests') }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success { echo 'Pipeline SUCCESS: cashflow siap dijalankan.' }
        failure { echo 'Pipeline FAILED: cek log stage yang merah.' }
        cleanup { cleanWs() }
    }
}

/** Menjalankan Maven Wrapper di Linux maupun Windows agent. */
def mvn(String goals) {
    if (isUnix()) {
        sh "./mvnw -B -ntp ${goals}"
    } else {
        bat ".\\mvnw.cmd -B -ntp ${goals}"
    }
}