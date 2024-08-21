#!/bin/bash

: <<'END_COMMENT'
  Script that enables to retrieve information about a service from Consul (how many instances, health state, their addresses, etc.)
  Useful when we are in a multi-host environment
END_COMMENT

consul_port=8500

usage() {
    echo "Usage: $0 -s [serviceID] -h [host]"
    exit 1
}

wait_ssh_connection() {
  max_attempts=10
  attempt=0
  while ! nc -z localhost ${consul_port} &> /dev/null; do
      sleep 0.2
      ((attempt++))
      if (( attempt >= max_attempts )); then
          echo "SSH tunnel failed to establish after $max_attempts attempts."
          exit 1
      fi
  done
}

cleanup() {
    kill "$ssh_pid"
}

parse_args () {

  if [ "$#" -lt 2 ]; then
      usage
  fi

  while [[ "$#" -gt 0 ]]; do
      case $1 in
          -s) serviceID="$2"; shift ;;
          -h) host="$2"; shift ;;
          *) usage ;;
      esac
      shift
  done

  if [ -z "$serviceID" ] || [ -z "$host" ]; then
      usage
  fi
}

parse_args "$@"

trap cleanup EXIT

# Create an SSH tunnel to the Consul server
ssh -L ${consul_port}:localhost:${consul_port} \
    root@"${host}"-svcs.demo.zextras.io -N &> /dev/null &

ssh_pid=$!

wait_ssh_connection

consul_server="localhost:${consul_port}"

echo "Search consul info for $serviceID:"
consulResponse=$(curl -s "http://$consul_server/v1/health/service/$serviceID")

if [ -z "$consulResponse" ]; then
    echo "No service found with ID $serviceID"
    exit 1
fi

echo "$consulResponse" | jq --arg serviceID "service:$serviceID" 'group_by(.Node.Node)[] | {
  Node: .[0].Node.Node | gsub("agent-"; ""),
  Address: .[0].Node.Address,
  Healthy: any(.[0].Checks[]; .CheckID == $serviceID and (.Status == "passing"))
}'
