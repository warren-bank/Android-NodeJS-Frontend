#!/usr/bin/env bash

# ------------------------------------------------------------------------------
# configure package details:
# ------------------------------------------------------------------------------

npm_package_scope='@warren-bank'
npm_package_name='hls-proxy'

# ------------------------------------------------------------------------------
# test preconditions:
# ------------------------------------------------------------------------------

local_dirpath="$1"
remote_dirpath="$2"

if [ -z "$npm_package_name" ]; then
  echo 'error: package name is not configured'
  exit
fi

if [ ! $# -eq 2 ]; then
  echo 'usage: '$(basename "${BASH_SOURCE[0]}")' <local_temp_dirpath> <remote_install_dirpath>'
  exit
fi

# ------------------------------------------------------------------------------
# install to device:
# ------------------------------------------------------------------------------

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

source "${DIR}/../lib/install_node_module.sh" "$npm_package_scope" "$npm_package_name" "$local_dirpath" "$remote_dirpath"

# ------------------------------------------------------------------------------
# construct JSON import data for Node.js Frontend:
# ------------------------------------------------------------------------------

hlsd_js="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/"$(node -e "const bin=${NODE_PACKAGE_JSON_BIN}; console.log(bin.hlsd)")

export_json=
export_json="${export_json}["
export_json="${export_json}{\"id\":\"0\",\"isActive\":true,\"js_filepath\":\"${hlsd_js}\",\"js_options\":[\"--port\",\"8080\",\"--useragent\",\"Chrome/85.0.0\",\"--req-insecure\",\"-v\",\"0\"],\"title\":\"HLS proxy (port 8080)\"},"
export_json="${export_json}{\"id\":\"0\",\"isActive\":true,\"js_filepath\":\"${hlsd_js}\",\"js_options\":[\"--port\",\"8080\",\"--useragent\",\"Chrome/85.0.0\",\"--req-insecure\",\"-v\",\"0\",\"--prefetch\",\"--max-segments\",\"20\"],\"title\":\"HLS proxy (port 8080, prefetch)\"}"
export_json="${export_json}]"

# ------------------------------------------------------------------------------
# copy JSON to device:
# ------------------------------------------------------------------------------

export_local="${local_dirpath}/${NODE_PACKAGE_DIRNAME}.json"
export_remote="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}.json"

echo "$export_json" >"$export_local"
adb push "$export_local" "$export_remote"
