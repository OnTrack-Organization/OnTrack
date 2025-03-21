#!/bin/bash

SCRIPT_DIR="$(dirname "$(realpath "$0")")"
CERT_LOCATION="$SCRIPT_DIR/../dev/docker/proxy/conf"

mkcert -key-file "$CERT_LOCATION"/dev-key.pem -cert-file "$CERT_LOCATION"/dev-cert.pem api.ontrack.local.riceisnice.dev
