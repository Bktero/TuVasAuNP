set -e
set -x

docker build -t bktero/tuvasaunp .
docker push bktero/tuvasaunp
