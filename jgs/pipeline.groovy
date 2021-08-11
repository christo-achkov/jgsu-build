pipeline {
    agent any

    // tools {
    //     // Install the Maven version configured as "M3" and add it to the path.
    //     maven "M3"
    // }

    stages {
        stage('Checkout') {
            steps {
                // Get some code from a GitHub repository
                git branch: 'main', url: 'https://github.com/zaibatsu-loli/jgsu-spring-petclinic'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw clean package'
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }

                changed {
                    emailext
                        attachLog: true,
                        body: '',
                        compressLog: true,
                        recipientProviders: [upstreamDevelopers(), requestor()],
                        subject: ''
                }
            }
        }
    }
}
