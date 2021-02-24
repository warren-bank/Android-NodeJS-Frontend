#!/usr/bin/env bash

# ------------------------------------------------------------------------------
# configure package details:
# ------------------------------------------------------------------------------

npm_package_scope=''
npm_package_name='npm'

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
# add a subdirectory to hold the npm cache:
# ------------------------------------------------------------------------------

npm_cache_dir="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/cache"

adb shell "mkdir '$npm_cache_dir'" 1>/dev/null 2>&1

# ------------------------------------------------------------------------------
# construct JSON import data for Node.js Frontend:
# ------------------------------------------------------------------------------

npm_cli_js="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/"$(node -e "const bin=${NODE_PACKAGE_JSON_BIN}; console.log(bin.npm)")

export_json=
export_json="${export_json}["
export_json="${export_json}{\"isActive\":true,\"env_vars\":[[\"npm_config_cache\",\"${npm_cache_dir}\"]],\"js_filepath\":\"${npm_cli_js}\",\"js_options\":[\"install\",\"--omit=dev\",\"--omit=optional\",\"--omit=peer\",\"--no-bin-links\",\"--ignore-scripts\",\"--no-audit\",\"--no-fund\"],\"title\":\"npm install\"},"
export_json="${export_json}{\"isActive\":true,\"env_vars\":[[\"npm_config_cache\",\"${npm_cache_dir}\"]],\"js_filepath\":\"${npm_cli_js}\",\"js_options\":[\"cache\",\"clean\",\"--force\"],\"title\":\"npm clean cache\"}"
export_json="${export_json}]"

# ------------------------------------------------------------------------------
# copy JSON to device:
# ------------------------------------------------------------------------------

export_local="${local_dirpath}/${NODE_PACKAGE_DIRNAME}.json"
export_remote="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}.json"

echo "$export_json" >"$export_local"
adb push "$export_local" "$export_remote"
