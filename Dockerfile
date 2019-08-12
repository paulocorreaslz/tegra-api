FROM maven:3.5-jdk-8
expose 9090
ADD /target/tegra-api.war tegra-api.war
ENTRYPOINT ["java","-jar","tegra-api.war"]