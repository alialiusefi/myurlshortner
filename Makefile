# Requirements
# - git
# - docker compose
# - kubernetes
# - java
# - kubectl

BE_PATH = myurlshortner-be
CONSUMER_PATH = myurlshortner-consumer
FE_PATH = myurlshortner-fe

be-artifact: 
	cd $(BE_PATH); ./gradlew imageBuild
	cd $(BE_PATH); git tag myurlshortner-be/$$(./gradlew printVersion -q) -f

consumer-artifact:
	cd $(CONSUMER_PATH); ./gradlew bootBuildImage
	cd $(CONSUMER_PATH); git tag myurlshortner-consumer/$$(./gradlew printVersion -q) -f

fe-docker-artifact:
	cd $(FE_PATH); docker build -t alialiusefi/myurlshortner-fe-docker:$$(npm pkg get version | tr -d '"') -f docker.Dockerfile .
	cd $(FE_PATH); git tag myurlshortner-fe/$$(npm pkg get version | tr -d '"') -f

fe-kube-artifact:
	cd $(FE_PATH); docker build -t alialiusefi/myurlshortner-fe-kube:$$(npm pkg get version | tr -d '"') -f kube.Dockerfile .
	cd $(FE_PATH); git tag myurlshortner-fe/$$(npm pkg get version | tr -d '"') -f

all-artifact: be-artifact consumer-artifact fe-docker-artifact fe-kube-artifact

# docker
docker-compose:
	docker compose -f docker-compose.yml -f docker-compose.all.yml up
docker-compose-clean:
	docker compose -f docker-compose.yml -f docker-compose.all.yml down -v
dev: be-artifact consumer-artifact fe-docker-artifact
	docker compose -f docker-compose.yml -f docker-compose.all.yml up

# kubernetes
kubernetes-secrets:
	kubectl apply -f k8s/postgres/prod/secrets.yaml
kubernetes-be: 
	kubectl apply -f k8s/myurlshortner-be/prod/deployment.yaml
	kubectl apply -f k8s/myurlshortner-be/prod/service.yaml
kubernetes-consumer: 
	kubectl apply -f k8s/myurlshortner-consumer/prod/deployment.yaml
kubernetes-postgres:
	kubectl apply --server-side -f https://raw.githubusercontent.com/cloudnative-pg/cloudnative-pg/release-1.27/releases/cnpg-1.27.0.yaml
	kubectl apply -f k8s/postgres/prod/cluster.yaml
kubernetes-apicurio:
	kubectl apply -f k8s/apicurio-registry/prod/pod.yaml
	kubectl apply -f k8s/apicurio-registry/prod/service.yaml
kubernetes-fe:
	kubectl apply -f k8s/myurlshortner-fe/prod/deployment.yaml
	kubectl apply -f k8s/myurlshortner-fe/prod/service.yaml
kubernetes-apigateway:
	kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.3.0/standard-install.yaml
	kubectl kustomize "https://github.com/nginx/nginx-gateway-fabric/config/crd/gateway-api/standard?ref=v2.1.2" | kubectl apply -f -
	kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v2.1.2/deploy/default/deploy.yaml
	kubectl apply -f k8s/apigateway/prod/apigateway.yaml
kubernetes: kubernetes-secrets kubernetes-be kubernetes-consumer kubernetes-postgres kubernetes-apicurio kubernetes-fe kubernetes-apigateway

prod: be-artifact consumer-artifact fe-kube-artifact kubernetes

prod-stop:
	kubectl scale deployment nginx-gateway --replicas=0 -n nginx-gateway
	kubectl scale deployment cnpg-controller-manager --replicas=0 -n cnpg-system 
	kubectl scale deployments --all --replicas=0 -n default
	kubectl delete pod apicurio-registry
	kubectl delete pod postgres-1

