#!/usr/bin/env bash

# ------------------------------------------------------------------------------
# configure package details:
# ------------------------------------------------------------------------------
# https://github.com/npm/cli/tree/v8.19.1#requirements
# https://github.com/npm/cli/tree/v9.0.0#requirements
#   v8 (and higher) requires Node.js v12.13.0+
#   v9 (and higher) requires Node.js v14.17.0+ which cannot run in Node.js v12.19.0
# ------------------------------------------------------------------------------

npm_package_scope=''
npm_package_name='npm'
npm_package_version='>=8.19.1 <9.0.0'

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
# add a subdirectory to hold the npm cache:
# ------------------------------------------------------------------------------

npm_cache_dir="./${NODE_PACKAGE_DIRNAME}/cache"
mkdir "$npm_cache_dir"

if [ "$use_adb_push" == "1" ]; then
  npm_cache_dir="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/cache"
  adb shell "mkdir '$npm_cache_dir'" 1>/dev/null 2>&1
fi

# ------------------------------------------------------------------------------
# construct JSON import data for Node.js Frontend:
# ------------------------------------------------------------------------------

npm_cli_js="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/"$(node -e "const bin=${NODE_PACKAGE_JSON_BIN}; console.log(bin.npm)")

export_json=
export_json="${export_json}["
export_json="${export_json}{\"isActive\":true,\"env_vars\":[[\"npm_config_cache\",\"${npm_cache_dir}\"]],\"js_filepath\":\"${npm_cli_js}\",\"js_options\":[\"version\"],\"title\":\"npm version\"},"
export_json="${export_json}{\"isActive\":true,\"env_vars\":[[\"npm_config_cache\",\"${npm_cache_dir}\"]],\"js_filepath\":\"${npm_cli_js}\",\"js_options\":[\"install\",\"--omit=dev\",\"--omit=optional\",\"--omit=peer\",\"--no-bin-links\",\"--ignore-scripts\",\"--no-audit\",\"--no-fund\"],\"title\":\"npm install\"},"
export_json="${export_json}{\"isActive\":true,\"env_vars\":[[\"npm_config_cache\",\"${npm_cache_dir}\"]],\"js_filepath\":\"${npm_cli_js}\",\"js_options\":[\"cache\",\"clean\",\"--force\"],\"title\":\"npm clean cache\"}"
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
