# Keycloak: Event Listener SPI & Publish events to Kafka
source: https://medium.com/keycloak/keycloak-event-listener-spi-publish-to-kafka-db1fabd285c1
# Setup

Kafka running at port 9092 or update in Producer.java
`BOOTSTRAP_SERVER = "127.0.0.1:9092"`


## build 
    `mvn clean install`

## Deploy to Keycloak instance
 
 `cp keycloak-spi-kafka.jar /keycloak-x.x.x/standalone/deployments`
  
  Restart the keycloak server 
  `./standalone.sh`
  
  Run on local keycloak
  ```
  rm ~/oltranz/keycloak-15.0.2/standalone/deployments/keycloak-spi-kafka* ; cp /mnt/c/Users/amusoke/iCloudDrive/Oltranz/code_repos/keycloak-spi-kafka/target/keycloak-spi-kafka* ~/oltranz/keycloak-15.0.2/standalone/deployments/
  ```