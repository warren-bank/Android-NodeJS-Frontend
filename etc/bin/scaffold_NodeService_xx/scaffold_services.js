const path = require('path')
const fs   = require('fs')

const count = parseInt( process.argv[2], 10 )

const get_int_strlen = (int) => (`${ int }`).length

const get_padding = (count) => ('0').repeat(count)

const count_strlen = get_int_strlen(count)

const get_padded_id = (id) => {
  const id_strlen = get_int_strlen(id)

  return `${ get_padding(count_strlen - id_strlen) }${ id }`
}

const get_service_file_text = (id) => {
  const padded_id = get_padded_id(id)

  return `package com.github.warren_bank.nodejs_frontend.services.fork;

public class NodeService_${ padded_id } extends AbstractNodeService {

  protected String getId() {
    return "${ padded_id }";
  }

}
`
}

const relative_file_path = '../../../android-studio-project/NodeJS-Frontend/src/main/java/com/github/warren_bank/nodejs_frontend/services/fork'
const absolute_file_path = path.resolve(relative_file_path)

const get_service_file_path = (id) => {
  const padded_id = get_padded_id(id)

  return path.join(absolute_file_path, `NodeService_${ padded_id }.java`)
}

const no_clobber = false

for (let id = 1; id <= count; id++) {
  const service_file_path = get_service_file_path(id)
  const service_file_text = get_service_file_text(id)

  if (no_clobber && fs.existsSync(service_file_path))
    continue

  fs.writeFileSync(
    service_file_path,
    service_file_text,
    {encoding: 'utf8'}
  )
}
