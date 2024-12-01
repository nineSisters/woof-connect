#!/bin/bash
# shellcheck disable=SC2006

KCHOST=http://localhost:8081
REALM=woof_realm
CLIENT_ID=woof_client
CLIENT_SECRET=kOVIszD7tpCHIh3okWUOkdOADnZvcOVL
UNAME=avetal
PASSWORD=vasya1991

ACCESS_TOKEN=`curl -sS \
  -d "client_id=$CLIENT_ID" \
  -d "username=$UNAME" \
  -d "password=$PASSWORD" \
  -d "client_secret=$CLIENT_SECRET" \
  -d "grant_type=password" \
  "$KCHOST/realms/$REALM/protocol/openid-connect/token" \
    | jq -r '.access_token'`
echo "$ACCESS_TOKEN"
