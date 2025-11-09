#==========================================
# File Name:    Dockerfile
# Created By:   Chris Ennis
# Created On:   09/24/25
# Purpose:      This is used to build Docker images used for production.
# Updated By:   Chris Ennis on 09/27/25 – Added comment block and directions for pushing new images.
# Updated By:   <Name> on <YYYY-MM-DD> – <Summary of change>
#==========================================

FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
ARG JAR_FILE=target/loreweave-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

#1.  commit and push to GitHub
#2.  .\mvnw.cmd clean package -DskipTests (Eventually we can remove -DskipTests)
#3.  git rev-parse --short HEAD (Most recent HEAD for updates git log to find any lost commits)
#4.  docker build --no-cache -t "registry.digitalocean.com/loreweave-app/loreweave-app:HEAD" .
#5.  docker push "registry.digitalocean.com/loreweave-app/loreweave-app:HEAD"
#6.  Update App Spec in Digital Ocean settings so that the tag matches the current HEAD