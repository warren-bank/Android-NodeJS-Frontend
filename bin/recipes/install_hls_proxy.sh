#!/usr/bin/env bash

# ------------------------------------------------------------------------------
# configure package details:
# ------------------------------------------------------------------------------
# https://semver.npmjs.com/
# ------------------------------------------------------------------------------
# https://github.com/warren-bank/HLS-Proxy#major-versions
#   v1.x.x requires Node.js v8.6.0+
#   v2.x.x requires Node.js v8.6.0+
#   v3.0.0 to v3.4.8 requires Node.js v16.0.0+ which cannot run in Node.js v12.19.0
#   v3.5.0 (and higher) requires Node.js v12.0.0+
# ------------------------------------------------------------------------------

npm_package_scope='@warren-bank'
npm_package_name='hls-proxy'
npm_package_version='<3.0.0 || >=3.5.0'

# ------------------------------------------------------------------------------
# test preconditions:
# ------------------------------------------------------------------------------

local_dirpath="$1"
remote_dirpath="$2"
use_adb_push="$3"

if [ -z "$npm_package_name" ]; then
  echo 'error: package name is not configured'
  exit
fi

if [ ! $# -eq 3 ]; then
  echo 'usage: '$(basename "${BASH_SOURCE[0]}")' <local_temp_dirpath> <remote_install_dirpath> <use_adb_push>'
  exit
fi

# ------------------------------------------------------------------------------
# install to device:
# ------------------------------------------------------------------------------

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

run_npm_scripts='0'

source "${DIR}/../lib/install_node_module.sh" "$npm_package_scope" "$npm_package_name" "$npm_package_version" "$local_dirpath" "$remote_dirpath" "$run_npm_scripts" "$use_adb_push"

cd "$local_dirpath"

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

export_local="./${NODE_PACKAGE_DIRNAME}.json"
export_remote="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}.json"

echo "$export_json" >"$export_local"

if [ "$use_adb_push" == "1" ]; then
  adb push "$export_local" "$export_remote"
else
  echo adb push "'${export_local}'" "'${export_remote}'" >>"$use_adb_push"
fi
