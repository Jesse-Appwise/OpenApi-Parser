package model.schema

data class Schema(
    var type: Types? = null,
    var description: String? = null,
    var properties: Map<String, Schema> = emptyMap(),
    var items: Schema? = null,
    var `$ref`: String? = null,
    var allOf: List<Schema> = emptyList()
) {

    enum class Types {
        string, number, integer, boolean, array, `object`, `null`;
    }

    val inheritedSchemes get() = allOf.filter { it.`$ref` != null }

}