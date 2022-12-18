# SCM Load Test Generator
This generates test data and then uploads that test data to some server using curl.

## OCP Job

Run the `create-job.sh` script.


## Running

```
SCM_FRONTEND=$(oc get ksvc scm-frontend -o template --template='{{.status.url}}')
docker run --rm --name scm-datagen -e SCM_UPLOAD_TIMES=10 -e SCM_URL=$SCM_FRONTEND/gzippedFiles quay.io/redhat_naps_da/scm-datagen
```

`SCM_UPLOAD_TIMES` is the number of times to upload an AM3X and DETM tgz (uploading both counts as 1 time).
`SCM_URL` is the api endpoint to upload the document to.  If you want to test this locally you can set to `http://localhost:8180/gzippedFiles`

## Building

```
docker build -t quay.io/jkeam/scm-datagen -f ./Dockerfile .
```

## Generate Test Files
After building, if you want to just generate test files and not upload them to an API endpoint, you can do so by running the container as such:

```
mkdir output  # if does not exist
docker run --rm --name scm-datagen -v $(pwd)/output:/app/output quay.io/jkeam/scm-datagen ./target/scm-datagen
```

Check the `output` directory for an AM3X and DETM file.
