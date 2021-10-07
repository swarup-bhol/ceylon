# service

REST API application

- use `.\mvnw.cmd spring-boot:run` to run the application

Run Latest Application

- install docker and (docker-compose if running on linux)
- copy `docker-compose.yml` and `docker-compose.production.yml` file to the working directory(directory the application should start from)
- create `.env` file
- login to repository using `docker login registry.gitlab.com -u <username> -p <token>` (use the username and the token provided )
- run `docker-compose -f docker-compose.yml -f docker-compose.production.yml up -d`
- goto `localhost:8080/api` to get api spec
