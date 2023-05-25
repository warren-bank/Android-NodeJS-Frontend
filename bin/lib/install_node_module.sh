#!/usr/bin/env bash

npm_package_scope="$1"
npm_package_name="$2"
npm_package_version="$3"
local_dirpath="$4"
remote_dirpath="$5"
run_npm_scripts="$6"
use_adb_push="$7"

# ------------------------------------------------------------------------------
# test preconditions:
# ------------------------------------------------------------------------------

print_usage() {
  echo 'usage: '$(basename "${BASH_SOURCE[0]}")' <npm_package_scope> <npm_package_name> <npm_package_version> <local_temp_dirpath> <remote_install_dirpath> <run_npm_scripts> <use_adb_push>'
  exit
}

if [ ! $# -eq 7 ]; then
  print_usage
fi

if [ ! -d "$local_dirpath" ]; then
  echo 'error: local directory path does not exist'
  print_usage
fi

if [ "$use_adb_push" == "1" ]; then
  if ! adb get-state 1>/dev/null 2>&1; then
    echo 'error: no remote Android device is connected'
    exit
  fi

  if ! adb shell "if [ ! -d '${remote_dirpath}' ]; then exit 1; else exit 0; fi" 1>/dev/null 2>&1; then
    echo 'error: remote directory path does not exist'
    exit
  fi
fi

if ! npm --version 1>/dev/null 2>&1; then
  echo 'error: npm is not working'
  exit
fi

# ------------------------------------------------------------------------------
# setup:
# ------------------------------------------------------------------------------

cd "$local_dirpath"
local_dirpath=$(pwd)

# use a temporary directory name to workaround npm special-cases (ex: "npm")
local_package_dirname="${npm_package_name}_cache"

local_package_dirpath="${local_dirpath}/${local_package_dirname}"
[ -d "$local_package_dirpath" ] && rm -rf "$local_package_dirpath"
mkdir "$local_package_dirpath"

if [ -z "$npm_package_scope" ]; then
  npm_module_id="$npm_package_name"
else
  npm_module_id="${npm_package_scope}/${npm_package_name}"
fi

if [ -z "$npm_package_version" ]; then
  npm_package_version='latest'
fi

npm_module_spec="${npm_module_id}@${npm_package_version}"

# ------------------------------------------------------------------------------
# install to local:
# ------------------------------------------------------------------------------

cd "$local_package_dirpath"

if [ "$run_npm_scripts" == "1" ]; then
  run_npm_scripts=''
else
  run_npm_scripts='--ignore-scripts'
fi

npm init -y 1>/dev/null
npm install --save "$npm_module_spec" "$run_npm_scripts" --omit=dev --omit=optional --omit=peer --no-bin-links --no-audit --no-fund 1>/dev/null

# ------------------------------------------------------------------------------
# sanity check:
# ------------------------------------------------------------------------------

export NODE_PACKAGE_REL_HOME="node_modules/${npm_module_id}"

if [ ! -d "${local_package_dirpath}/${NODE_PACKAGE_REL_HOME}" ]; then
  echo 'error: installed node module directory path does not exist'
  exit
fi

# ------------------------------------------------------------------------------
# gather metadata from package.json:
# ------------------------------------------------------------------------------

package_filepath="./${NODE_PACKAGE_REL_HOME}/package.json"

export NODE_PACKAGE_VERSION=$( node -e "console.log(require('${package_filepath}').version)")
export NODE_PACKAGE_JSON_BIN=$(node -e "console.log(require('${package_filepath}').bin)")

# ------------------------------------------------------------------------------
# derive additional metadata:
# ------------------------------------------------------------------------------

export NODE_PACKAGE_DIRNAME="${npm_package_name}_v${NODE_PACKAGE_VERSION}"

# ------------------------------------------------------------------------------
# remove previous install:
# ------------------------------------------------------------------------------

[ -d "${local_dirpath}/${NODE_PACKAGE_DIRNAME}" ]      && rm -rf "${local_dirpath}/${NODE_PACKAGE_DIRNAME}"
[ -e "${local_dirpath}/${NODE_PACKAGE_DIRNAME}.json" ] && rm -f  "${local_dirpath}/${NODE_PACKAGE_DIRNAME}.json"

if [ "$use_adb_push" == "1" ]; then
  adb shell "[ -d '${remote_dirpath}/${NODE_PACKAGE_DIRNAME}' ]      && rm -rf '${remote_dirpath}/${NODE_PACKAGE_DIRNAME}'"      1>/dev/null 2>&1
  adb shell "[ -e '${remote_dirpath}/${NODE_PACKAGE_DIRNAME}.json' ] && rm -f  '${remote_dirpath}/${NODE_PACKAGE_DIRNAME}.json'" 1>/dev/null 2>&1
fi

# ------------------------------------------------------------------------------
# cleanup:
# ------------------------------------------------------------------------------

# rename directory
cd "$local_dirpath"
mv "$local_package_dirname" "$NODE_PACKAGE_DIRNAME"
local_package_dirpath="./${NODE_PACKAGE_DIRNAME}"

# ------------------------------------------------------------------------------
# copy to remote:
# ------------------------------------------------------------------------------

if [ "$use_adb_push" == "1" ]; then
  adb push "${local_package_dirpath}/" "${remote_dirpath}/"
else
  echo adb push "'${local_package_dirpath}/'" "'${remote_dirpath}/'" >>"$use_adb_push"
fi
