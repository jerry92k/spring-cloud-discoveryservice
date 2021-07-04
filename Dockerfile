FROM openjdk:17-ea-slim
VOLUME /tmp
COPY target/apigateway-service-1.0.jar ApigatewayService.jar
ENTRYPOINT ["java","-jar","ApigatewayService.jar"]