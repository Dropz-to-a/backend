pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh '''
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
