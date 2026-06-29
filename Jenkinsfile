pipeline {
    agent any

    environment {
        DB_NAME     = 'saas-app-db'
        DB_USER     = 'postgres'
        DB_PASSWORD = 'postgres'
        DB_PORT     = '5432'
        DB_URL      = 'jdbc:postgresql://localhost:5432/saas-app-db'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Start Postgres') {
            steps {
                sh 'docker-compose down -v || true'
                sh 'docker-compose up -d postgres'
                sh '''
                    for i in {1..30}; do
                      docker exec saas-db pg_isready -U postgres && break
                      sleep 2
                    done
                '''
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean verify'
            }
        }
    }

    post {
        always {
            sh 'docker-compose down -v || true'
        }
    }
}
