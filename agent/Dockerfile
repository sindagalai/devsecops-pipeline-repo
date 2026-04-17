###############################################################################
# Jenkins Inbound Agent — DevSecOps Toolchain
# Purpose:
# - Execute Jenkins pipelines
# - Provide Git / Maven / Java / Docker CLI / jq / curl
# - Security scanners are NOT installed here
#   (SonarScanner, Gitleaks, Trivy will run as Docker containers from pipelines)
###############################################################################

FROM jenkins/inbound-agent:3261.v9c670a_4748a_9-1

USER root

# -----------------------------------------------------------------------------
# 1. Install base packages
# -----------------------------------------------------------------------------
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    wget \
    unzip \
    jq \
    bash \
    maven \
    gnupg \
    lsb-release \
    ca-certificates \
    openjdk-21-jdk \
    procps \
    less \
    iputils-ping \
    netcat-openbsd \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# -----------------------------------------------------------------------------
# 2. Install Docker CLI only
#    (the agent will talk to host Docker via /var/run/docker.sock)
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
# 3. Prepare docker group and workspace
# -----------------------------------------------------------------------------
RUN groupadd -f docker \
 && usermod -aG docker jenkins \
 && mkdir -p /home/jenkins/agent \
 && mkdir -p /home/jenkins/agent/reports \
 && chown -R jenkins:jenkins /home/jenkins/agent

# -----------------------------------------------------------------------------
# 4. Environment variables
# -----------------------------------------------------------------------------
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV MAVEN_HOME=/usr/share/maven
ENV PATH="${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"

WORKDIR /home/jenkins/agent

USER jenkins