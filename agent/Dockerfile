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
RUN wget -O /tmp/sonar-scanner.zip https://github.com/SonarSource/sonar-scanner-cli/releases/download/4.6.2.2472/sonar-scanner-cli-4.6.2.2472-linux.zip \
    && unzip /tmp/sonar-scanner.zip -d /opt \
    && rm /tmp/sonar-scanner.zip

# Définir les variables d'environnement pour SonarQube
ENV SONAR_SCANNER_HOME=/opt/sonar-scanner-4.6.2.2472-linux
ENV PATH=$PATH:$SONAR_SCANNER_HOME/bin

# Installer Gitleaks pour scanner les secrets dans les dépôts
RUN wget -O /tmp/gitleaks.tar.gz https://github.com/zricethezav/gitleaks/releases/download/v7.7.0/gitleaks-linux-amd64-v7.7.0.tar.gz \
    && tar -xvzf /tmp/gitleaks.tar.gz -C /usr/local/bin/ \
    && rm /tmp/gitleaks.tar.gz

# Exposer le répertoire de travail
WORKDIR /home/jenkins/agent

# Revenir à l'utilisateur Jenkins (privilèges moindres)
USER jenkins