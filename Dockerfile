FROM maven:3.5-jdk-8
expose 9090
ADD /target/tegra-api.jar tegra-api.jar
ENTRYPOINT ["java","-jar","tegra-api.jar"]