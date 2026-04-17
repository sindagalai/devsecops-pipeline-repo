###############################################################################
# Jenkins Inbound Agent — DevSecOps Toolchain
# Tools: Docker CLI, Maven
# SonarQube Scanner & Trivy run as Docker containers from pipelines
###############################################################################


FROM jenkins/inbound-agent:3261.v9c670a_4748a_9-1


USER root


# ── 1. Base packages ───────────────────────────────────────────────────────
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
    && rm -rf /var/lib/apt/lists/*


# ── 2. Docker CLI only (no daemon — talks to host via socket) ─────────────
RUN install -m 0755 -d /etc/apt/keyrings \
    && curl -fsSL https://download.docker.com/linux/debian/gpg \
       -o /etc/apt/keyrings/docker.asc \
    && chmod a+r /etc/apt/keyrings/docker.asc \
    && echo "deb [arch=$(dpkg --print-architecture) \
       signed-by=/etc/apt/keyrings/docker.asc] \
       https://download.docker.com/linux/debian \
       $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
       > /etc/apt/sources.list.d/docker.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends docker-ce-cli \
    && rm -rf /var/lib/apt/lists/*


# Give jenkins user access to docker socket (GID aligned at runtime)
RUN groupadd -f docker && usermod -aG docker jenkins


# ── 3. Workspace setup ───────────────────────────────────────────────────
RUN mkdir -p /home/jenkins/agent/reports \
    && chown -R jenkins:jenkins /home/jenkins/agent


WORKDIR /home/jenkins/agent


# Drop back to non-root
USER jenkins