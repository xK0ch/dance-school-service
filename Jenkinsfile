pipeline {
  agent any

  environment {
    DB_PASSWORD = credentials('TANZSCHULE_DB_PASSWORD')
    JWT_SECRET = credentials('TANZSCHULE_JWT_SECRET')
    ADMIN_USERNAME = credentials('TANZSCHULE_ADMIN_USERNAME')
    ADMIN_PASSWORD = credentials('TANZSCHULE_ADMIN_PASSWORD')
    GALLERY_UPLOAD_DIR = '/srv/tanzschule/uploads/images'
    MAIL_HOST = credentials('TANZSCHULE_MAIL_HOST')
    MAIL_PORT = credentials('TANZSCHULE_MAIL_PORT')
    MAIL_USERNAME = credentials('TANZSCHULE_MAIL_USERNAME')
    MAIL_PASSWORD = credentials('TANZSCHULE_MAIL_PASSWORD')
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
