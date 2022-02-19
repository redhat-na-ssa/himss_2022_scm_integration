# SCM Load Test Generator
This generates test data and then uploads that test data to some server using curl.

## Building
```
docker build -t quay.io/jkeam/scm-datagen -f ./Dockerfile .
```

## Running
```
docker run --rm --name scm-datagen -e SCM_UPLOAD_TIMES=10 -e SCM_URL=http://localhost:8180/gzippedFiles quay.io/jkeam/scm-datagen
```

`SCM_UPLOAD_TIMES` is the number of times to upload an AM3X and DETM tgz (uploading both counts as 1 time).
`SCM_URL` is the api endpoint to upload the document to.
