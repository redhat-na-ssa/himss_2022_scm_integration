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
    echo -e "\n"
    du -sh $file
    echo -e "Sending $file to $1"
    curl --insecure -v -F "data=@$file" $1
  done
}

# main
#   assuming ./output/sent and target/scm-datagen exists
for (( i = 0; i < $max; i++ ))
do
  # generate new data
  ./target/scm-datagen -r 5000

  # send data
  send_files $url

  # mark as sent
  mv ./output/*.tgz ./output/sent
done
