FROM registry.access.redhat.com/ubi8/ubi:8.5

RUN yum -y update && yum -y install make gcc-c++
RUN mkdir -p /app/output/sent && mkdir /app/target
WORKDIR /app
COPY Makefile *.cpp *.h run.sh .
RUN chmod -R 777 /app && make clean && make

ENV SCM_UPLOAD_TIMES 1
ENV SCM_URL http://localhost:8180/gzippedFiles
USER nobody
CMD ["./run.sh"]
