package com.illuzor.kindlistserializer

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KindListSerializerTest {

    @Serializable
    private data class Data(val name: String, val value: Int)

    private val testData =
        listOf(
            Data("Name1", 11),
            Data("Name2", 22),
            Data("Name3", 33),
        )

    private val testCorrectString =
        """[{"name":"Name1","value":11},{"name":"Name2","value":22},{"name":"Name3","value":33}]"""

    private val testBrokenString =
        """[{"name":"Name1","value":11},{"name":[]},"vvv",["a", "b", "v"],{"name":"Name3"},{"name":"Name2","value":22},{"name":"Name3","value":33},{"name":"Name3"}]"""

    @Test
    fun `Decode with broken string - success`() {
        val serializer = kindListSerializer(Data.serializer())

        assertEquals(
            testData,
            Json.decodeFromString(serializer, testBrokenString)
        )
    }

    @Test
    fun `Decode with correct string - success`() {
        val serializer = kindListSerializer(Data.serializer())

        assertEquals(
            testData,
            Json.decodeFromString(serializer, testCorrectString)
        )
    }

    @Test
    fun `Encode to string - success`() {
        val serializer = kindListSerializer(Data.serializer())

        assertEquals(
            testCorrectString,
            Json.encodeToString(serializer, testData)
        )
    }
}
