pipeline {
    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-21-amazon-corretto"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh '''
                    java -version
                    chmod +x gradlew
                    ./gradlew clean build -x test
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    pkill -f job-manager || true
                    nohup java -jar build/libs/*SNAPSHOT.jar --server.port=8080 > app.log 2>&1 &
                '''
            }
        }
    }
}
