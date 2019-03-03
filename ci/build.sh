#!/bin/bash

set -euo pipefail

[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

spring_hateoas-examples_artifactory=$(pwd)/spring-hateoas-examples-artifactory

rm -rf $HOME/.m2/repository/org/springframework/hateoas-examples 2> /dev/null || :

cd spring-hateoas-examples-github

./mvnw -Pdistribute -Dmaven.test.skip=true clean deploy \
	-DaltDeploymentRepository=distribution::default::file://${spring_hateoas-examples_artifactory}
