# User
Tracks users

## Running outside of K8s
```bash
./gradlew build
docker build . -t user
docker network create core-fantasy
docker run --name user -p 8081:8080 --network core-fantasy -e JWT_GENERATOR_SIGNATURE_SECRET=$(head -c 32 < /dev/zero | tr '\0' '\141') user:latest
```