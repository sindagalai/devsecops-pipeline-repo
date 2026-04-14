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

# Installer le Sonar Scanner pour l'analyse SonarQube
RUN wget -O /tmp/sonar-scanner.zip https://github.com/SonarSource/sonar-scanner-cli/releases/download/4.7.0.2747/sonar-scanner-cli-4.7.0.2747-linux.zip \
    && unzip /tmp/sonar-scanner.zip -d /opt \
    && rm /tmp/sonar-scanner.zip

# Définir les variables d'environnement pour SonarQube
ENV SONAR_SCANNER_HOME=/opt/sonar-scanner-4.7.0.2747-linux
ENV PATH=$PATH:$SONAR_SCANNER_HOME/bin

# Exposer le répertoire de travail
WORKDIR /home/jenkins/agent

# Revenir à l'utilisateur Jenkins (privilèges moindres)
USER jenkins