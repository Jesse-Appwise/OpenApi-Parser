package data.decoder

import model.ApiDocumentation
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer
import java.io.InputStream

class YamlDecoder {

    private val decoder: Yaml

    init {
        val representer = Representer().apply {
            propertyUtils.isSkipMissingProperties = true
        }
        val constructor = Constructor(ApiDocumentation::class.java)
        decoder = Yaml(constructor, representer)
    }

    fun decode(inputStream: InputStream): ApiDocumentation {
        return decoder.loadAs(inputStream, ApiDocumentation::class.java)
    }

}