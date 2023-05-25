#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# both directories need to already exist
local_temp_dir="${DIR}/../temp"
remote_dir='/storage/emulated/0/Node.js'
use_adb_push='0'

if [ ! "$use_adb_push" == "1" ]; then
  use_adb_push="${local_temp_dir}/adb_push.sh"

  if [ -e "$use_adb_push" ]; then
    rm -f "$use_adb_push"
  fi

  echo '#!/usr/bin/env bash' >"$use_adb_push"
  chmod +x "$use_adb_push"
fi

"${DIR}/recipes/install_npm.sh"       "$local_temp_dir" "$remote_dir" "$use_adb_push"
"${DIR}/recipes/install_serve.sh"     "$local_temp_dir" "$remote_dir" "$use_adb_push"
"${DIR}/recipes/install_hls_proxy.sh" "$local_temp_dir" "$remote_dir" "$use_adb_push"
