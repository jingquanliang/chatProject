FROM centos:latest
MAINTAINER jingquanliang

#install jdk and tomcat

ADD jdk-8u171-linux-x64.tar.gz /home/jingquanliang/
COPY apache-tomcat-9.0.7 /home/jingquanliang/apache-tomcat-9.0.7/
ADD framework-core/build/libs/framework-core-0.0.1-SNAPSHOT.jar app.jar

#jdk enviroment
ENV JAVA_HOME=/home/jingquanliang/jdk1.8.0_171
ENV JRE_HOME=$JAVA_HOME/jre
ENV CLASSPATH=$JAVA_HOME/lib:$JAVA_HOME/jre/lib
ENV PATH=$JAVA_HOME/bin:$PATH

EXPOSE 8080
EXPOSE 9999

#tomcat self start
#CMD ["/home/schoolapp/apache-tomcat-7.0.76/bin/catalina.sh","run"]
ENTRYPOINT ["java","-jar","/app.jar"]