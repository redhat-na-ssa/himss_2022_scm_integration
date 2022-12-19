#!/bin/bash

oc delete job generate-load-job
date
oc process -f ./kubernetes/job.yaml -p SCM_URL=$(oc get ksvc scm-frontend -o template --template='{{.status.url}}') -p SCM_UPLOAD_TIMES=25 | oc create -f -
