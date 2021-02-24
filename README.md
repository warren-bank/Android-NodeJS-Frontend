### [Node.js Frontend - recipes](https://github.com/warren-bank/Android-NodeJS-Frontend/tree/recipes)

This branch contains bash scripts to run on a (local) computer to install Node.js applications from `npm` on a (remote) Android device

#### Requirements:

* on the local computer:
  * [node](https://nodejs.org/)
  * [npm](https://www.npmjs.com/package/npm)
  * [adb](https://developer.android.com/studio/command-line/adb) connected to remote Android device
  * a temporary directory to hold files that are downloaded from the network, and will be pushed to a directory on the remote Android device
* on the remote Android device:
  * a directory that will be used to store Node.js applications
    - each app will be placed in a separate subdirectory
    - each app may also add a single `json` file to the top-level directory that can be used to import pre-configured settings into _Node.js Frontend_

#### Example of Usage:

```bash
# both directories need to already exist
local_temp_dir="${HOME}/delete_me"
remote_dir='/storage/emulated/0/Node.js'

./recipes/install_npm.sh       "$local_temp_dir" "$remote_dir"
./recipes/install_serve.sh     "$local_temp_dir" "$remote_dir"
./recipes/install_hls_proxy.sh "$local_temp_dir" "$remote_dir"
```

#### Legal:

* copyright: [Warren Bank](https://github.com/warren-bank)
* license: [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)
