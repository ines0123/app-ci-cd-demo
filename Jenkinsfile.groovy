pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'ines0123/ci-cd-demo-app' 
        HELM_CHART_PATH = './mon-app'  
    }
    stages {
        stage('Cloner le dépôt') {
            steps {
                git branch: 'main', url: 'https://github.com/ines0123/app-ci-cd-demo.git'
            }
        }
        stage('Vérifier le Dockerfile') {
            steps {
                sh 'ls -l'
            }
        }
        stage('Construire l"image Docker') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                }
            }
        }
        stage('Pousser l"image Docker') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin"
                        sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                    }
                }
            }
        }
        stage('Déployer avec Helm') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'kubeconfig-id', variable: 'KUBECONFIG')]) {
                        sh """
                            export KUBECONFIG=$KUBECONFIG
                            helm upgrade --install mon-app ${HELM_CHART_PATH} \
                                --set image.repository=${DOCKER_IMAGE} \
                                --set image.tag=${BUILD_NUMBER}
                        """
                    }
                }
            }
        }
    }
}
