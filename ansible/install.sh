#!/bin/bash

ansible-playbook --extra-vars '{"fips_enabled_cluster":"true"}' playbooks/install.yml
