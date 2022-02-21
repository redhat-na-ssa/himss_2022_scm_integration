#!/bin/bash

max=$SCM_UPLOAD_TIMES
url=$SCM_URL
max=${max:-1}
url=${url:-'http://localhost:8180/gzippedFiles'}

# send files to server
# takes one arg, the url
function send_files() {
  for file in ./output/*.tgz
  do
    echo -e "\nSending $file to $1"
    curl -v -F "data=@$file" $1
    sleep 1
  done
}

# main
#   assuming ./output/sent and target/scm-datagen exists
for (( i = 0; i < $max; i++ ))
do
  # generate new data
  ./target/scm-datagen

  # send data
  send_files $url

  # delete data
  mv ./output/*.tgz ./output/sent
done
