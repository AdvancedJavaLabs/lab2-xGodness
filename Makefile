common:
	@cd ./common && ./gradlew --no-daemon shadowJar

client:
	@cd ./client && ./gradlew --no-daemon shadowJar

splitter:
	@cd ./splitter && ./gradlew --no-daemon shadowJar

processor:
	@cd ./processor && ./gradlew --no-daemon shadowJar

aggregator:
	@cd ./aggregator && ./gradlew --no-daemon shadowJar

.PHONY: build common client splitter processor aggregator
build: common client splitter processor aggregator
	@echo "Build successful"

up:
	@docker compose --env-file .env up --build --remove-orphans -d
	@docker compose logs -f
