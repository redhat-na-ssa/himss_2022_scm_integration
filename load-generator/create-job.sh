#!/bin/bash

oc delete job generate-load-job
oc process -f ./kubernetes/job.yaml -p SCM_URL=$(oc get ksvc scm-frontend -o template --template='{{.status.url}}') | oc create -f -
