package data.encoder.kotlin

import com.cesarferreira.pluralize.singularize
import data.encoder.kotlin.model.writeKotlinSchemaClass
import data.encoder.kotlin.model.writeKotlinRetrofitService
import model.ApiDocumentation

class KotlinNetworkEncoder(
    private var documentation: ApiDocumentation,
    private var outputPath: String
) {

    fun encode() {
        encodeServices()
        encodeModels()
    }

    private fun encodeServices() {
        documentation.paths.map { (key, value) ->
            key to value
        }.groupBy { (path, _) ->
            path.split("/").first { it.isNotEmpty() && !it.matches("v\\d+".toRegex()) }
        }.onEach {
            it.value.toMap().writeKotlinRetrofitService("$outputPath/service", it.key.singularize())
        }
    }

    private fun encodeModels() {
        documentation.components.schemas.forEach { (key, value) ->
            value.writeKotlinSchemaClass("$outputPath/model/dto", key)
        }
    }

}