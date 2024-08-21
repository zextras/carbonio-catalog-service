#!/bin/bash

default_domain="demo.zextras.io"
consul_port=8500

usage() {
    echo "Usage: $0 -n [hostname] [-d domain]"
    exit 1
}

cleanup() {
    echo "Terminating SSH tunnels..."
    kill "$ssh_pid1"
}

if [ "$#" -lt 2 ]; then
    usage
fi

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -n) name="$2"; shift ;;
        -d) domain="$2"; shift ;;
        *) usage ;;
    esac
    shift
done

if [ -z "$name" ]; then
    usage
fi

if [ -z "$domain" ]; then
    domain=$default_domain
fi

trap cleanup EXIT

rsync root@"${name}"-svcs."${domain}":/etc/carbonio/catalog/service-discover/token consul_token

ssh -N -L ${consul_port}:localhost:${consul_port} \
    -L ${postgres_port}:localhost:${postgres_port} \
    root@"${name}"-svcs."${domain}" &

ssh_pid1=$!

echo "SSH tunneling set up for required remote endpoints on ${name}-*.${domain}"

wait $ssh_pid1

