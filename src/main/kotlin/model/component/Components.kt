package model.component

import model.schema.Schema

data class Components(
    var schemas: Map<String, Schema> = emptyMap()
)