#!/bin/bash

# SPDX-FileCopyrightText: 2025 Zextras <https://www.zextras.com>
#
# SPDX-License-Identifier: AGPL-3.0-only

HOST=$1
DOMAIN=${2-"demo.zextras.io"}

usage() {
    echo "Usage: $0 hostname-prefix [domain]"
    echo ""
    echo "Example:"
    echo "$0 kc-dev1"
    echo "# it will access to: kc-dev1-svcs.demo.zextras.io"
    exit 1
}

cleanup() {
    kill "$tunnel_pid"
}

if [ "$#" -lt 1 ]; then
    usage
fi

trap cleanup EXIT

./tunnel.sh -n "$HOST" -d "$DOMAIN" &
tunnel_pid=$!

mvn quarkus:dev

kill "$tunnel_pid"