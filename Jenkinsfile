pipeline {
  agent any

  environment {
    DB_PASSWORD = credentials('TANZSCHULE_DB_PASSWORD')
    JWT_SECRET = credentials('TANZSCHULE_JWT_SECRET')
    ADMIN_USERNAME = credentials('TANZSCHULE_ADMIN_USERNAME')
    ADMIN_PASSWORD = credentials('TANZSCHULE_ADMIN_PASSWORD')
    GALLERY_UPLOAD_DIR = '/srv/tanzschule/uploads/images'
  }

  stages {
    stage('Deploy') {
      steps {
        sh 'docker compose -f docker-compose-tanzschule-family-and-friends-service.yml down'
        sh 'docker image prune -af'
        sh 'docker compose -f docker-compose-tanzschule-family-and-friends-service.yml up --build -d'
      }
    }
  }
}
