#FROM ubuntu:14.04
FROM picoded/ubuntu-base


USER root 

RUN apt remove
RUN apt-get -y update
RUN apt-get -y install curl
RUN apt-get -y install software-properties-common
 
# JAVA
RUN apt-get update && \
	apt-get install -f -y openjdk-8-jdk && \
	apt-get install -f -y ant && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;
	
RUN apt-get update && \
	apt-get install -f -y ca-certificates-java && \
	apt-get clean && \
	update-ca-certificates -f && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;
	
# SPARK
ARG SPARK_ARCHIVE=http://d3kbcqa49mib13.cloudfront.net/spark-2.1.0-bin-hadoop2.7.tgz
ENV SPARK_HOME /usr/local/spark-2.1.0-bin-hadoop2.7
 
ENV PATH $PATH:${SPARK_HOME}/bin
RUN curl -s ${SPARK_ARCHIVE} | tar -xz -C /usr/local/
 
WORKDIR $SPARK_HOME

#AIRFLOW
FROM puckel/docker-airflow

