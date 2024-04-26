package model.path

data class ResponseBody(
    var description: String? = null,
    var content: Map<String, MediaType> = emptyMap()
){
    val jsonResponseSchema get() = content["application/json"]?.schema
}