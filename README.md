### [Node.js Frontend - recipes](https://github.com/warren-bank/Android-NodeJS-Frontend/tree/recipes)

This branch contains bash scripts to run on a (local) computer to install Node.js applications from `npm` on a (remote) Android device

- - - -

### Instructions (with `npm` and `adb`)

#### Requirements:

* on the local computer:
  * [Node.js](https://nodejs.org/)
  * [npm](https://www.npmjs.com/package/npm)
  * [adb](https://developer.android.com/studio/command-line/adb) connected to remote Android device
  * a temporary directory to hold files that are downloaded from the network
    - its contents will be pushed to a directory on the remote Android device
* on the remote Android device:
  * a directory that will be used to store Node.js applications
    - each app will be placed in a separate subdirectory
    - each app may also add a single `json` file to the top-level directory that can be used to import pre-configured settings into _Node.js Frontend_

#### Usage:

* [install.sh](./bin/install.sh)
  - edit:
    ```bash
      remote_dir='/storage/emulated/0/Node.js'
      use_adb_push='1'
    ```
  - run

- - - -

### Instructions (with `npm`)

#### Requirements:

* on the local computer:
  * [Node.js](https://nodejs.org/)
  * [npm](https://www.npmjs.com/package/npm)
  * a temporary directory to hold files that are downloaded from the network
    - its contents will need to be moved manually to a directory on the remote Android device

#### Usage:

* [install.sh](./bin/install.sh)
  - edit:
    ```bash
      remote_dir='/storage/emulated/0/Node.js'
      use_adb_push='0'
    ```
  - run

#### Notes:

* although no files will be pushed to the remote directory by the `install.sh` script,<br>the `remote_dir` path will be included in the pre-configured `*.json` settings

- - - -

### Instructions (with .zip bundle)

#### Usage:

* download .zip bundle: [NodeJS-Frontend-recipes.zip](https://github.com/warren-bank/Android-NodeJS-Frontend/releases/download/recipes_zip_bundle/NodeJS-Frontend-recipes.zip)
* unzip on phone in directory path: `/storage/emulated/0/Node.js`

- - - -

### Instructions (common)

#### Import Pre-configured Settings:

* in _Node.js Frontend_ app:
  - on tab: `exec in same process`
    * import: `/storage/emulated/0/Node.js/npm_v8.19.4.json`
  - on tab: `fork to separate process`
    * import: `/storage/emulated/0/Node.js/hls-proxy_v2.0.3.json`
    * import: `/storage/emulated/0/Node.js/serve_v130002.18.2.json`

- - - -

#### Legal:

* copyright: [Warren Bank](https://github.com/warren-bank)
* license: [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)
