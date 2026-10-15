set -e
set -x

./gradlew check
docker compose build
docker compose push
