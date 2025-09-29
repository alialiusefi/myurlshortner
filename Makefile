# Requirements
# - git
# - docker compose
# - kubernetes

BE_PATH = myurlshortner-be
CONSUMER_PATH = myurlshortner-consumer
FE_PATH = myurlshortner-fe

be-artifact: 
	cd $(BE_PATH); ./gradlew imageBuild
	cd $(BE_PATH); git tag myurlshortner-be/$$(./gradlew printVersion -q) -f

consumer-artifact:
	cd $(CONSUMER_PATH); ./gradlew bootBuildImage
	cd $(CONSUMER_PATH); git tag myurlshortner-consumer/$$(./gradlew printVersion -q) -f

fe-artifact:
	cd $(FE_PATH); docker build -t alialiusefi/myurlshortner-fe:$$(npm pkg get version | tr -d '"') .
	cd $(FE_PATH); git tag myurlshortner-fe/$$(npm pkg get version | tr -d '"') -f

all-artifact: be-artifact consumer-artifact fe-artifact

# docker
docker-compose:
	docker compose -f docker-compose.yml -f docker-compose.all.yml up
docker-compose-clean:
	docker compose -f docker-compose.yml -f docker-compose.all.yml down -v
dev: be-artifact consumer-artifact fe-artifact
	docker compose -f docker-compose.yml -f docker-compose.all.yml up

# kubernetes
kubernetes-secrets:
	kubectl apply -f k8s/postgres/prod/secrets.yaml
kubernetes-be: 
	kubectl apply -f k8s/myurlshortner-be/prod/deployment.yaml
kubernetes-consumer: 
	kubectl apply -f k8s/myurlshortner-consumer/prod/deployment.yaml
kubernetes-postgres:
	kubectl apply --server-side -f https://raw.githubusercontent.com/cloudnative-pg/cloudnative-pg/release-1.27/releases/cnpg-1.27.0.yaml
	kubectl apply -f k8s/postgres/prod/cluster.yaml
kuberenetes-apicurio:
	kubectl apply -f k8s/apicurio-registry/prod/pod.yaml
	kubectl apply -f k8s/apicurio-registry/prod/service.yaml
kubernetes: kubernetes-secrets kubernetes-be kubernetes-consumer kubernetes-postgres kuberenetes-apicurio

prod: be-artifact consumer-artifact kubernetes-secrets kubernetes-postgres kuberenetes-apicurio
	kubectl apply -f k8s/myurlshortner-be/prod/deployment.yaml
	kubectl apply -f k8s/myurlshortner-consumer/prod/deployment.yaml
