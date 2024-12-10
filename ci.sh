#!/bin/bash

repoName="wocoio"
services="cloud-iam-manager"

DOCKER_TOKEN=$(curl -s -H "Content-Type: application/json" -X POST -d '{"username": "'${DOCKER_USERNAME}'", "password": "'${DOCKER_PASSWORD}'"}' https://hub.docker.com/v2/users/login/ | jq -r .token)

tag=1.0.0

for service in ${services}; do
  results=$(curl https://hub.docker.com/v2/repositories/wocoio/${service}/tags -H "Authorization: JWT ${DOCKER_TOKEN}")
  if [[ ${results} != *unauthorized* ]]; then
    tags=$(echo ${results} | jq .results.[].name)

    service_new_tag=${tag}

    service_tags=$(echo ${tags} | sed 's/"//g' | sort -rV)
    if [[ ! (-z ${service_tags}) ]]; then
      service_latest_tag=$(echo ${service_tags} | head -n 1)
      service_new_tag=$(echo ${service_latest_tag} | awk -F'.' '{print $1 "." ($2+1) "." 0}')
    fi
  else
    echo "results = ${results}"
    exit 123
  fi

  echo ""
  echo "service_new_tag = ${service_new_tag}"
  echo ""

  docker build -t ${repoName}/${service}:${service_new_tag} .
  docker push ${repoName}/${service}:${service_new_tag}
done
