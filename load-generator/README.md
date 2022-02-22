# SCM Load Test Generator
This generates test data and then uploads that test data to some server using curl.

## Running 
```
SCM_FRONTEND=$(oc get ksvc scm-frontend -o template --template='{{.status.url}}')
docker run --rm --name scm-datagen -e SCM_UPLOAD_TIMES=10 -e SCM_URL=$SCM_FRONTEND/gzippedFiles quay.io/redhat_naps_da/scm-datagen
```

`SCM_UPLOAD_TIMES` is the number of times to upload an AM3X and DETM tgz (uploading both counts as 1 time).
`SCM_URL` is the api endpoint to upload the document to.

## Building and Running locally

### Building locally
```
docker build -t quay.io/jkeam/scm-datagen -f ./Dockerfile .
```

### Running locally
```
docker run --rm --name scm-datagen -e SCM_UPLOAD_TIMES=10 -e SCM_URL=http://localhost:8180/gzippedFiles quay.io/jkeam/scm-datagen
```

`SCM_UPLOAD_TIMES` is the number of times to upload an AM3X and DETM tgz (uploading both counts as 1 time).
`SCM_URL` is the api endpoint to upload the document to.
