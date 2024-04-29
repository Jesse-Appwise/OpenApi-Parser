package data.decoder

import model.ApiDocumentation
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.introspector.Property
import org.yaml.snakeyaml.introspector.PropertyUtils
import util.removeCharToCamelCase
import java.io.InputStream

class YamlDecoder {

    private val decoder: Yaml

    init {
        val constructor = Constructor(ApiDocumentation::class.java).apply {
            propertyUtils = object : PropertyUtils() {
                override fun getProperty(type: Class<out Any>?, name: String?): Property {
                    return super.getProperty(type, name?.removeCharToCamelCase('/'))
                }
            }

            propertyUtils.isSkipMissingProperties = true
        }
        decoder = Yaml(constructor)
    }

    fun decode(inputStream: InputStream): ApiDocumentation {
        return decoder.loadAs(inputStream, ApiDocumentation::class.java)
    }

}