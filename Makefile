# Requirements
# - git
# - docker compose
# - kubernetes

BE_PATH = myurlshortner-be
CONSUMER_PATH = myurlshortner-consumer


be-artifact: 
	cd $(BE_PATH); ./gradlew imageBuild
	cd $(BE_PATH); git tag myurlshortner-be/$$(./gradlew printVersion -q) -f

consumer-artifact:
	cd $(CONSUMER_PATH); ./gradlew bootBuildImage
	cd $(CONSUMER_PATH); git tag myurlshortner-consumer/$$(./gradlew printVersion -q) -f

all-artifact: be-artifact consumer-artifact

# docker
docker-compose:
	docker compose -f docker-compose.yml -f docker-compose.all.yml up
docker-compose-clean:
	docker compose -f docker-compose.yml -f docker-compose.all.yml down -v
dev: be-artifact consumer-artifact
	docker compose -f docker-compose.yml -f docker-compose.all.yml up

# kubernetes
kubernetes-secrets:
	kubectl apply -f k8s/postgres/prod/secrets.yaml
kubernetes-be: 
	kubectl apply -f k8s/myurlshortner-be/prod/deployment.yaml
kubernetes-consumer: 
	kubectl apply -f k8s/myurlshortner-consumer/prod/deployment.yaml
kubernetes: kubernetes-secrets kubernetes-be kubernetes-consumer

prod: be-artifact consumer-artifact
	kubectl apply -f k8s/postgres/prod/secrets.yaml
	kubectl apply -f k8s/myurlshortner-be/prod/deployment.yaml
	kubectl apply -f k8s/myurlshortner-consumer/prod/deployment.yaml
