package data.encoder.kotlin.model

import model.schema.Schema
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import util.toModelClassName


class SchemaKtTest {

    @Nested
    @DisplayName("toKotlinDefaultValue()")
    inner class ToKotlinDefaultValue {
        @Test
        fun `should return empty string for string type`() {
            // Given
            val schemaType = Schema.Types.string
            // When
            val result = schemaType.toKotlinDefaultValue()
            // Then
            assertEquals("\"\"", result)
        }

        @Test
        fun `should return 0 for number type`() {
            // Given
            val schemaType = Schema.Types.number
            // When
            val result = schemaType.toKotlinDefaultValue()
            // Then
            assertEquals("0.0", result)
        }

        @Test
        fun `should return 0 for integer type`() {
            // Given
            val schemaType = Schema.Types.integer
            // When
            val result = schemaType.toKotlinDefaultValue()
            // Then
            assertEquals("0", result)
        }

        @Test
        fun `should return false for boolean type`() {
            // Given
            val schemaType = Schema.Types.boolean
            // When
            val result = schemaType.toKotlinDefaultValue()
            // Then
            assertEquals("false", result)
        }

        @Test
        fun `should return null for object type`() {
            // Given
            val schemaType = Schema.Types.`object`
            // When
            val result = schemaType.toKotlinDefaultValue()
            // Then
            assertEquals("null", result)
        }

        @Test
        fun `should return empty list for array type`() {
            // Given
            val schemaType = Schema.Types.array
            // When
            val result = schemaType.toKotlinDefaultValue()
            // Then
            assertEquals("emptyList()", result)
        }

        @Test
        fun `should return null for null type`() {
            // Given
            val schemaType = Schema.Types.`null`
            // When
            val result = schemaType.toKotlinDefaultValue()
            // Then
            assertEquals("null", result)
        }
    }

    @Nested
    @DisplayName("toKotlinType()")
    inner class ToKotlinType{
        @Test
        fun `should return String for string type`(){
            // Given
            val schema = Schema(
                type = Schema.Types.string
            )
            // When
            val result = schema.toKotlinType()
            // Then
            assertEquals("String", result)
        }

        @Test
        fun `should return Double for number type`(){
            // Given
            val schema = Schema(
                type = Schema.Types.number
            )
            // When
            val result = schema.toKotlinType()
            // Then
            assertEquals("Double", result)
        }

        @Test
        fun `should return Int for integer type`(){
            // Given
            val schema = Schema(
                type = Schema.Types.integer
            )
            // When
            val result = schema.toKotlinType()
            // Then
            assertEquals("Int", result)
        }

        @Test
        fun `should return Boolean for boolean type`(){
            // Given
            val schema = Schema(
                type = Schema.Types.boolean
            )
            // When
            val result = schema.toKotlinType()
            // Then
            assertEquals("Boolean", result)
        }

        @Test
        fun `should return Any for object type if no name is provided`(){
            // Given
            val schema = Schema(
                type = Schema.Types.`object`
            )
            // When
            val result = schema.toKotlinType()
            // Then
            assertEquals("Any", result)
        }

        @Test
        fun `should return the give Class name for object type if the name is provided`(){
            // Given
            val schema = Schema(
                type = Schema.Types.`object`
            )
            // When
            val result = schema.toKotlinType("ClassName")
            // Then
            assertEquals("ClassName", result)
        }

        @Test
        fun `should return List of Any for array type if no name is provided`(){
            // Given
            val schema = Schema(
                type = Schema.Types.array
            )
            // When
            val result = schema.toKotlinType()
            // Then
            assertEquals("List<Any>", result)
        }

        @Test
        fun `should return List of the given Class name for array type if the name is provided`(){
            // Given
            val schema = Schema(
                type = Schema.Types.array
            )
            // When
            val result = schema.toKotlinType("ClassName")
            // Then
            assertEquals("List<ClassName>", result)
        }

    }

    @Nested
    @DisplayName("toKotlinInheritance()")
    inner class ToKotlinInheritance{
        @Test
        fun `should return empty string if there is no inherited schemes`(){
            // Given
            val schema = Schema(
                allOf = emptyList()
            )
            // When
            val result = schema.toKotlinInheritance()
            // Then
            assertEquals("", result)
        }

        @Test
        fun `should return the last part of the ref if there is inherited schemes`(){
            // Given
            val schema = Schema(
                allOf = listOf(
                    Schema(`$ref` = "#/components/schemas/ClassName")
                )
            )
            // When
            val result = schema.toKotlinInheritance()
            // Then
            assertEquals("ClassName()", result)
        }
    }


    @Nested
    @DisplayName("toKotlinProperty()")
    inner class ToKotlinProperty{
        @Test
        fun `should return the name and the schema as a string with default value`(){
            // Given
            val schema = Schema(
                type = Schema.Types.string
            )
            val pair = Pair("name", schema)
            // When
            val result = pair.toKotlinProperty()
            // Then
            assertEquals("val name: String = ${schema.type?.toKotlinDefaultValue()}", result)
        }

        @Test
        fun `should return a nullable property for object type`(){
            // Given
            val schema = Schema(
                type = Schema.Types.`object`
            )
            val pair = Pair("name", schema)
            // When
            val result = pair.toKotlinProperty()
            // Then
            assertEquals("val name: ${schema.toKotlinType("name".toModelClassName())}? = null", result)
        }

        @Test
        fun `should annotate with a serialized name and change the name to camelcase when the name is snakeCase`(){
            // Given
            val schema = Schema(
                type = Schema.Types.string
            )
            val pair = Pair("snake_case", schema)
            // When
            val result = pair.toKotlinProperty()
            // Then
            assertEquals("@SerializedName(\"snake_case\") val snakeCase: String = ${schema.type?.toKotlinDefaultValue()}", result)
        }
    }

    @Test
    fun toKotlinClass() {
    }

    @Test
    fun writeKotlinSchemaClass() {
    }
}