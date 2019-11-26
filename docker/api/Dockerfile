FROM jboss/wildfly:16.0.0.Final
USER root
ENV LANG en_US.UTF-8
RUN mkdir /etc/nba
ADD nba.properties /etc/nba/nba.properties
ADD log4j2.xml /etc/nba/log4j2.xml
ADD standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
#COPY dwca /etc/nba/dwca
RUN yum clean all && yum install -y git
RUN git clone https://github.com/naturalis/DWCAConfig.git --branch v004
RUN mv DWCAConfig /etc/nba/dwca
RUN git clone https://github.com/naturalis/nba-fieldinfo-metadata --branch 2
RUN mv nba-fieldinfo-metadata /etc/nba/metadata
ADD nba.war /opt/jboss/wildfly/standalone/deployments/
