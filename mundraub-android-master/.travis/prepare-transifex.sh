#!/bin/bash
#
# This file is used to update the translations
#
# See https://docs.transifex.com/integrations/github/
# about transifex
#

set -e

cd "`dirname \"$0\"`"

if [ -z "$TRANSIFEX_PASSWORD" ]; then
  echo "Set the variable TRANSIFEX_PASSWORD to your API key from https://www.transifex.com/user/settings/api/"
  echo "Otherwise your translations can not be deployed."
  exit 1
fi

pip3 install --user --upgrade -r ../requirements.txt

(
  echo "[https://www.transifex.com]"
  echo "api_hostname = https://api.transifex.com"
  echo "hostname = https://www.transifex.com"
  echo "password = $TRANSIFEX_PASSWORD"
  echo "username = api"
) > ~/.transifexrc
echo "Created "

