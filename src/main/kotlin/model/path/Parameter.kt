package model.path

import model.schema.Schema

data class Parameter(
    var name: String? = null,
    var `in`: String? = null,
    var description: String? = null,
    var required: Boolean = false,
    var schema: Schema? = null
) {
    enum class Location {
        path, query, header, cookie
    }
}