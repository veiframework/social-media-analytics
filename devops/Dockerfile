FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/mcr.microsoft.com/playwright/python:v1.55.0-jammy

RUN cat <<EOF > /etc/apt/sources.list
deb https://mirrors.aliyun.com/ubuntu/ jammy main restricted universe multiverse
deb-src https://mirrors.aliyun.com/ubuntu/ jammy main restricted universe multiverse

deb https://mirrors.aliyun.com/ubuntu/ jammy-security main restricted universe multiverse
deb-src https://mirrors.aliyun.com/ubuntu/ jammy-security main restricted universe multiverse

deb https://mirrors.aliyun.com/ubuntu/ jammy-updates main restricted universe multiverse
deb-src https://mirrors.aliyun.com/ubuntu/ jammy-updates main restricted universe multiverse

deb https://mirrors.aliyun.com/ubuntu/ jammy-backports main restricted universe multiverse
deb-src https://mirrors.aliyun.com/ubuntu/ jammy-backports main restricted universe multiverse
EOF


RUN apt update && apt install openjdk-8-jre bash curl zip -y
RUN sed -i 's/^jdk.tls.disabledAlgorithms=.*$/jdk.tls.disabledAlgorithms=RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL, include jdk.disabled.namedCurves/' $(find / -name java.security)
ENV TZ=Asia/Shanghai

ARG APP_NAME
ARG APP_FILE_PATH
ARG JAVA_OPTS

RUN mkdir -p /${APP_NAME}
WORKDIR /${APP_NAME}

COPY devops/application.yml application.yml

COPY ${APP_FILE_PATH}${APP_NAME}.jar ${APP_NAME}.jar
COPY  devops/start.sh start.sh

# 替换模板变量
RUN sed -i "s/{{APP_NAME}}/${APP_NAME}/g" start.sh



## 可以不写,不影响真正监听的端口
## EXPOSE  9201



ENTRYPOINT ["bash","-x","start.sh"]
