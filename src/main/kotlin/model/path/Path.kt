package model.path

data class Path(
    var get: Operation? = null,
    var post: Operation? = null,
    var put: Operation? = null,
    var delete: Operation? = null
){
    enum class Method {
        GET, POST, PUT, DELETE
    }
}