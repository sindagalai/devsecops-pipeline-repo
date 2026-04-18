###############################################################################
# Jenkins Inbound Agent — DevSecOps Toolchain
# - Git / Maven / JDK 21 / Docker CLI / Gitleaks
###############################################################################

FROM jenkins/inbound-agent:3261.v9c670a_4748a_9-1

USER root

RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    wget \
    unzip \
    jq \
    bash \
    gnupg \
    lsb-release \
    ca-certificates \
    procps \
    less \
    iputils-ping \
    netcat-openbsd \
    tar \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

RUN install -m 0755 -d /etc/apt/keyrings \
 && wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public \
    | gpg --dearmor -o /etc/apt/keyrings/adoptium.gpg \
 && echo "deb [signed-by=/etc/apt/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb $(. /etc/os-release && echo "$VERSION_CODENAME") main" \
    > /etc/apt/sources.list.d/adoptium.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends temurin-21-jdk maven \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

RUN install -m 0755 -d /etc/apt/keyrings \
 && curl -fsSL https://download.docker.com/linux/debian/gpg \
    -o /etc/apt/keyrings/docker.asc \
 && chmod a+r /etc/apt/keyrings/docker.asc \
 && echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
    > /etc/apt/sources.list.d/docker.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends docker-ce-cli \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

RUN wget -q https://github.com/gitleaks/gitleaks/releases/download/v8.24.2/gitleaks_8.24.2_linux_x64.tar.gz -O /tmp/gitleaks.tar.gz \
 && tar -xzf /tmp/gitleaks.tar.gz -C /tmp \
 && mv /tmp/gitleaks /usr/local/bin/gitleaks \
 && chmod +x /usr/local/bin/gitleaks \
 && rm -f /tmp/gitleaks.tar.gz

RUN groupadd -f docker \
 && usermod -aG docker jenkins \
 && mkdir -p /home/jenkins/agent/reports \
 && mkdir -p /home/jenkins/agent/gitleaks-rules \
 && chown -R jenkins:jenkins /home/jenkins/agent

ENV JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
ENV MAVEN_HOME=/usr/share/maven
ENV PATH="${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"

WORKDIR /home/jenkins/agent

USER jenkins