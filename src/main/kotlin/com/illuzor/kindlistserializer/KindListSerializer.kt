package com.illuzor.kindlistserializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder

fun <T> kindListSerializer(
    itemSerializer: KSerializer<T>,
    throwableHandler: ThrowableHandler? = null
): KSerializer<List<T>> = KindListSerializer(itemSerializer, throwableHandler)

private class KindListSerializer<T>(
    private val itemSerializer: KSerializer<T>,
    private val throwableHandler: ThrowableHandler?,
) : KSerializer<List<T>> {

    private val listSerializer = ListSerializer(itemSerializer)

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<T>) =
        listSerializer.serialize(encoder, value)

    override fun deserialize(decoder: Decoder): List<T> {
        val values = mutableListOf<T?>()
        val compositeDecoder = decoder.beginStructure(descriptor)

        while (true) {
            val index = compositeDecoder.decodeElementIndex(descriptor)
            if (index == CompositeDecoder.DECODE_DONE) break
            values.add(readElement(compositeDecoder))
        }
        compositeDecoder.endStructure(descriptor)

        return values.filterNotNull()
    }

    private fun readElement(compositeDecoder: CompositeDecoder): T? {
        return try {
            val jsonDecoder = compositeDecoder as JsonDecoder
            val jsonElement = jsonDecoder.decodeJsonElement()
            jsonDecoder.json.decodeFromJsonElement(itemSerializer, jsonElement)
        } catch (e: Exception) {
            throwableHandler?.handle(e)
            null
        }
    }
}
