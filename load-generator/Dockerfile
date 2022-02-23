FROM registry.access.redhat.com/ubi8/ubi:8.5

RUN yum -y update && yum -y install make gcc-c++

RUN useradd --create-home --shell /bin/bash appuser
RUN mkdir -p /app/output/sent && mkdir /app/target && chown -R appuser.appuser /app
WORKDIR /app
COPY Makefile *.cpp *.h run.sh .
RUN chown -R appuser.appuser . && chmod 744 ./run.sh && make clean && make

ENV SCM_UPLOAD_TIMES 1
ENV SCM_URL http://localhost:8180/gzippedFiles
USER appuser
CMD ["./run.sh"]
