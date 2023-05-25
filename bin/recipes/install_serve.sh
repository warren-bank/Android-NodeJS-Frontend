#!/usr/bin/env bash

# ------------------------------------------------------------------------------
# configure package details:
# ------------------------------------------------------------------------------

npm_package_scope='@warren-bank'
npm_package_name='serve'
npm_package_version='latest'

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

run_npm_scripts='1'

source "${DIR}/../lib/install_node_module.sh" "$npm_package_scope" "$npm_package_name" "$npm_package_version" "$local_dirpath" "$remote_dirpath" "$run_npm_scripts" "$use_adb_push"

cd "$local_dirpath"

# ------------------------------------------------------------------------------
# remove symlinks to included libraries:
# ------------------------------------------------------------------------------

npm_module_dir="./${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/node_modules/serve"
rm -rf "$npm_module_dir"

npm_module_dir="./${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/node_modules/serve-handler"
rm -rf "$npm_module_dir"

# ------------------------------------------------------------------------------
# replace unused dependency that intentionally fails when run in Android:
# ------------------------------------------------------------------------------
# https://github.com/sindresorhus/clipboardy/blob/v2.3.0/index.js#L14-L17
# ------------------------------------------------------------------------------

npm_module_dir="./${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/node_modules/clipboardy"
export_local="${npm_module_dir}/index.js"
rm -rf "$npm_module_dir"
mkdir "$npm_module_dir"
echo 'module.exports = {"write": function(){}}' >"$export_local"

if [ "$use_adb_push" == "1" ]; then
  npm_module_dir="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/node_modules/clipboardy"
  export_remote="${npm_module_dir}/index.js"
  adb shell "rm -rf '$npm_module_dir'" 1>/dev/null 2>&1
  adb shell "mkdir '$npm_module_dir'"  1>/dev/null 2>&1
  adb push "$export_local" "$export_remote"
fi

# ------------------------------------------------------------------------------
# replace unused dependency that unintentionally fails when run in Node.js v12.19.0
# ------------------------------------------------------------------------------
# https://github.com/sindresorhus/boxen/blob/v5.1.2/package.json#L37
# https://github.com/sindresorhus/camelcase/blob/v6.3.0/package.json#L14
# https://github.com/sindresorhus/camelcase/blob/v6.3.0/index.js#L3
#   Invalid property name in character class
# ------------------------------------------------------------------------------

npm_module_dir="./${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/node_modules/boxen"
export_local="${npm_module_dir}/index.js"
rm -rf "$npm_module_dir"
mkdir "$npm_module_dir"
echo 'module.exports = function(){}' >"$export_local"

if [ "$use_adb_push" == "1" ]; then
  npm_module_dir="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/node_modules/boxen"
  export_remote="${npm_module_dir}/index.js"
  adb shell "rm -rf '$npm_module_dir'" 1>/dev/null 2>&1
  adb shell "mkdir '$npm_module_dir'"  1>/dev/null 2>&1
  adb push "$export_local" "$export_remote"
fi

# ------------------------------------------------------------------------------
# construct JSON import data for Node.js Frontend:
# ------------------------------------------------------------------------------

serve_js="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/"$(node -e "const bin=${NODE_PACKAGE_JSON_BIN}; console.log(bin.serve)")

export_json=
export_json="${export_json}["
export_json="${export_json}{\"id\":\"0\",\"isActive\":true,\"cwd_dirpath\":\"${remote_dirpath}\",\"js_filepath\":\"${serve_js}\",\"js_options\":[\"--listen\",\"tcp://0.0.0.0:8000\",\"--cors\",\"--no-compression\",\"--no-clipboard\"],\"title\":\"Serve httpd (port 8000)\"}"
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
