FROM openjdk:16-alpine3.13
RUN mkdir /cgi-bin
COPY ./cgi-bin /cgi-bin
RUN chmod +x /cgi-bin/*
WORKDIR /app
COPY ./target/cgiserver-1.0-SNAPSHOT.jar .
CMD ["java", "-cp", "cgiserver-1.0-SNAPSHOT.jar", "cgiserver.App", "-port", "65000", "-cgiFolder", "/cgi-bin", "-execFolder", "/cgi-bin", "-host", "0.0.0.0" ]
