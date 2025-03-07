#!/usr/bin/env bash

# ------------------------------------------------------------------------------
# configure package details:
# ------------------------------------------------------------------------------
# https://semver.npmjs.com/
# ------------------------------------------------------------------------------

npm_package_scope='@warren-bank'
npm_package_name='widevine-license-proxy'
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

run_npm_scripts='0'

source "${DIR}/../lib/install_node_module.sh" "$npm_package_scope" "$npm_package_name" "$npm_package_version" "$local_dirpath" "$remote_dirpath" "$run_npm_scripts" "$use_adb_push"

cd "$local_dirpath"

# ------------------------------------------------------------------------------
# download plugins:
# ------------------------------------------------------------------------------

PLUGINS_DIRNAME="${NODE_PACKAGE_DIRNAME}/plugins"

mkdir "$PLUGINS_DIRNAME"
cd "$PLUGINS_DIRNAME"

# install common libraries
wget --no-check-certificate -q -O 'package.json' 'https://github.com/warren-bank/node-widevine-license-proxy/raw/master/.recipes/package.json'
npm install --omit=dev --omit=optional --omit=peer --no-bin-links --no-audit --no-fund 1>/dev/null

PLUGIN_FILENAME_CHANNEL4='channel4.js'
wget --no-check-certificate -q -O "$PLUGIN_FILENAME_CHANNEL4" 'https://github.com/warren-bank/node-widevine-license-proxy/raw/master/.recipes/02.%20channel4/server-config.js'

cd "$local_dirpath"

# ------------------------------------------------------------------------------
# construct JSON import data for Node.js Frontend:
# ------------------------------------------------------------------------------

wvlpd_js="${remote_dirpath}/${NODE_PACKAGE_DIRNAME}/${NODE_PACKAGE_REL_HOME}/"$(node -e "const bin=${NODE_PACKAGE_JSON_BIN}; console.log(bin.wvlpd)")
plugin_channel4_js="${remote_dirpath}/${PLUGINS_DIRNAME}/${PLUGIN_FILENAME_CHANNEL4}"

export_json=
export_json="${export_json}["
export_json="${export_json}{\"id\":\"0\",\"isActive\":true,\"js_filepath\":\"${wvlpd_js}\",\"js_options\":[\"--port\",\"8081\",\"--useragent\",\"Chrome/134.0.0\",\"--req-insecure\",\"--use\",\"${plugin_channel4_js}\"],\"title\":\"Widevine license proxy (port 8081, channel4)\"}"
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
