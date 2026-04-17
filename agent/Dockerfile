###############################################################################
# Jenkins Inbound Agent — DevSecOps Toolchain
# - Git / Maven / JDK 21 / Docker CLI
# - SonarScanner, Gitleaks, Trivy run as Docker containers from pipelines
###############################################################################

FROM jenkins/inbound-agent:3261.v9c670a_4748a_9-1

USER root

# -----------------------------------------------------------------------------
# 1. Base packages
# -----------------------------------------------------------------------------
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
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# -----------------------------------------------------------------------------
# 2. Install Eclipse Temurin JDK 21 + Maven
# -----------------------------------------------------------------------------
RUN install -m 0755 -d /etc/apt/keyrings \
 && wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public \
    | gpg --dearmor -o /etc/apt/keyrings/adoptium.gpg \
 && echo "deb [signed-by=/etc/apt/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb $(. /etc/os-release && echo "$VERSION_CODENAME") main" \
    > /etc/apt/sources.list.d/adoptium.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends temurin-21-jdk maven \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# -----------------------------------------------------------------------------
# 3. Install Docker CLI only
# -----------------------------------------------------------------------------
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

# -----------------------------------------------------------------------------
# 4. Docker group + workspace
# -----------------------------------------------------------------------------
RUN groupadd -f docker \
 && usermod -aG docker jenkins \
 && mkdir -p /home/jenkins/agent/reports \
 && chown -R jenkins:jenkins /home/jenkins/agent

# -----------------------------------------------------------------------------
# 5. Environment
# -----------------------------------------------------------------------------
ENV JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
ENV MAVEN_HOME=/usr/share/maven
ENV PATH="${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"

WORKDIR /home/jenkins/agent

USER jenkins