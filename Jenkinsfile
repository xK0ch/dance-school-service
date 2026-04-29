pipeline {
  agent any

  options {
    disableConcurrentBuilds()
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  environment {
    DB_PASSWORD = credentials('DANCE_SCHOOL_DB_PASSWORD')
    JWT_SECRET = credentials('DANCE_SCHOOL_JWT_SECRET')
    ADMIN_USERNAME = credentials('DANCE_SCHOOL_ADMIN_USERNAME')
    ADMIN_PASSWORD = credentials('DANCE_SCHOOL_ADMIN_PASSWORD')
    GALLERY_UPLOAD_DIR = '/srv/dance-school/uploads/images'
    MAIL_HOST = credentials('DANCE_SCHOOL_MAIL_HOST')
    MAIL_PORT = credentials('DANCE_SCHOOL_MAIL_PORT')
    MAIL_USERNAME = credentials('DANCE_SCHOOL_MAIL_USERNAME')
    MAIL_PASSWORD = credentials('DANCE_SCHOOL_MAIL_PASSWORD')
  }

  stages {
    stage('Deploy') {
      steps {
        sh 'docker compose -f docker-compose-dance-school-service.yml up --build -d --remove-orphans'
      }
    }
  }

  post {
    always {
      sh 'docker image prune -f'
    }
  }
}
