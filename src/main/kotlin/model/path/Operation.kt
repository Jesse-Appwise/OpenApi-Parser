package model.path

data class Operation(
    var summary: String? = null,
    var description: String? = null,
    var operationId: String? = null,
    var tags: List<String> = emptyList(),
    var parameters: List<Parameter> = emptyList(),
    var requestBody: RequestBody? = null,
    var responses: Map<String, ResponseBody> = emptyMap()
){
    val successResponse get() = responses.filterKeys { it.toInt() in 200 .. 299 }.values.firstOrNull()
}