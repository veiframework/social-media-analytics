FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/library/alpine:3.22.2
RUN sed -i 's/https:\/\/dl-cdn.alpinelinux.org/http:\/\/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories
RUN apk update && apk add openjdk8-jre bash curl
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
