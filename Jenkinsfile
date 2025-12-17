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
                sh './gradlew clean build -x test'
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
