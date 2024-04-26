package model.path

data class RequestBody(
    var description: String? = null,
    var content: Map<String, MediaType> = emptyMap()
)