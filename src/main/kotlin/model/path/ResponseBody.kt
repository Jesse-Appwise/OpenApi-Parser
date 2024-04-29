package model.path

data class ResponseBody(
    var description: String? = null,
    var content: Content? = null
)

data class Content(
    var applicationJson: MediaType? = null,
    var textPlain: String? = null
)