#! /bin/bash

export PGPASSWORD=$POSTGRESQL_PASSWORD

SCRIPT_DIR=''
pushd "$(dirname "$(readlink -f "$BASH_SOURCE")")" > /dev/null && {
    SCRIPT_DIR="$PWD"
    popd > /dev/null
}


echo "creating SCM database"

psql -h $POSTGRESQL_SERVICE -U $POSTGRESQL_USER -d $POSTGRESQL_DATABASE -w < ${SCRIPT_DIR}/ddl.sql

psql -h $POSTGRESQL_SERVICE -U $POSTGRESQL_USER -d $POSTGRESQL_DATABASE -w -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ${POSTGRESQL_USER};"
psql -h $POSTGRESQL_SERVICE -U $POSTGRESQL_USER -d $POSTGRESQL_DATABASE -w -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO ${POSTGRESQL_USER};"

