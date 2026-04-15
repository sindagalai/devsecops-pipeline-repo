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

# Exposer le répertoire de travail
WORKDIR /home/jenkins/agent

# Revenir à l'utilisateur Jenkins (privilèges moindres)
USER jenkins