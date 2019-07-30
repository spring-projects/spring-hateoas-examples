pipeline {
	agent none

	triggers {
		pollSCM 'H/10 * * * *'
	}

	options {
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '14'))
	}

	stages {
		stage("test: baseline (jdk8)") {
			agent {
				docker {
					image 'adoptopenjdk/openjdk8:latest'
					args '-v $HOME/.m2:/root/.m2'
				}
			}
			steps {
				sh "PROFILE=none ci/test.sh"
			}
		}

		stage("Test other configurations") {
			parallel {
				stage("test: baseline (jdk11)") {
					agent {
						docker {
							image 'adoptopenjdk/openjdk11:latest'
							args '-v $HOME/.m2:/root/.m2'
						}
					}
					steps {
						sh "PROFILE=none ci/test.sh"
					}
				}
				stage("test: baseline (jdk12)") {
					agent {
						docker {
							image 'adoptopenjdk/openjdk12:latest'
							args '-v $HOME/.m2:/root/.m2'
						}
					}
					steps {
						sh "PROFILE=none ci/test.sh"
					}
				}
			}
		}
	}

	post {
		changed {
			script {
				slackSend(
						color: (currentBuild.currentResult == 'SUCCESS') ? 'good' : 'danger',
						channel: '#spring-hateoas',
						message: "${currentBuild.fullDisplayName} - `${currentBuild.currentResult}`\n${env.BUILD_URL}")
				emailext(
						subject: "[${currentBuild.fullDisplayName}] ${currentBuild.currentResult}",
						mimeType: 'text/html',
						recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']],
						body: "<a href=\"${env.BUILD_URL}\">${currentBuild.fullDisplayName} is reported as ${currentBuild.currentResult}</a>")
			}
		}
	}
}
