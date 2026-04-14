# Utilisation de l'image officielle Jenkins inbound agent
FROM jenkins/inbound-agent:latest

# Passage à l'utilisateur root pour l'installation des outils nécessaires
USER root

# Mise à jour et installation des outils nécessaires
RUN apt-get update && apt-get install -y \
    git \
    curl \
    wget \
    unzip \
    jq \
    bash \
    maven \
    docker.io \
    && rm -rf /var/lib/apt/lists/*

# Installer Trivy pour les scans SCA (Software Composition Analysis)
RUN wget https://github.com/aquasecurity/trivy/releases/download/v0.21.0/trivy_0.21.0_Linux-64bit.deb \
    && dpkg -i trivy_0.21.0_Linux-64bit.deb \
    && rm trivy_0.21.0_Linux-64bit.deb

# Installer le Sonar Scanner pour l'analyse SonarQube
RUN wget -O /tmp/sonar-scanner.zip https://github.com/SonarSource/sonar-scanner-cli/releases/download/4.6.2.2472/sonar-scanner-cli-4.6.2.2472-linux.zip \
    && unzip /tmp/sonar-scanner.zip -d /opt \
    && rm /tmp/sonar-scanner.zip

# Définir les variables d'environnement pour SonarQube
ENV SONAR_SCANNER_HOME=/opt/sonar-scanner-4.6.2.2472-linux
ENV PATH=$PATH:$SONAR_SCANNER_HOME/bin

# Exposer le répertoire de travail
WORKDIR /home/jenkins/agent

# Revenir à l'utilisateur Jenkins (privilèges moindres)
USER jenkins