package com.illuzor.kindlistserializer

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NestedListTest {

    private object DataListSerializer : KindListSerializer<Data>(Data.serializer())

    @Serializable
    private data class DataContainer(
        val name: String,
        val data: @Serializable(with = DataListSerializer::class) List<Data>,
    )

    @Serializable
    private data class Data(val name: String, val value: Int)

    private val testCorrectString =
        """{"name":"some name","data":[{"name":"Name1","value":11},{"name":"Name2","value":22},{"name":"Name3","value":33}]}"""

    private val testBrokenString =
        """{"name":"some name", "data" :["bbb",{"name":"Name1","value":11},{"name":"Name2","value":22},{"name":"Name3","value":33},[1,2,3,]}"""

    private val testDataContainer =
        DataContainer(
            name = "some name",
            data = listOf(
                Data("Name1", 11),
                Data("Name2", 22),
                Data("Name3", 33),
            ),
        )

    @Test
    fun `Decode with correct string - success`() {
        assertEquals(
            testDataContainer,
            Json.decodeFromString<DataContainer>(testCorrectString),
        )
    }

    @Test
    fun `Decode with broken string - success`() {
        assertEquals(
            testDataContainer,
            Json.decodeFromString<DataContainer>(testBrokenString),
        )
    }

    @Test
    fun `Encode to string - success`() {
        assertEquals(
            testCorrectString,
            Json.encodeToString(testDataContainer),
        )
    }
}
