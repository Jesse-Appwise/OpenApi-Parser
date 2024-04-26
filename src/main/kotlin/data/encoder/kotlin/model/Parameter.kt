package data.encoder.kotlin.model

import model.path.Parameter

fun Parameter.toKotlinRetrofitParameter(): String {
    return buildString {
        `in`?.let {
            append("@${`in`?.replaceFirstChar(Char::titlecaseChar)}(\"$name\") ")
        }
        append("${if (required) "val" else "var"} $name: ${schema?.toKotlinType() ?: "Any"}")
    }
}