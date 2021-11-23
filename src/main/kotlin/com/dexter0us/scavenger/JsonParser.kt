package com.dexter0us.scavenger

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class JsonParser {
    fun parser(body: String): MutableSet<String> {
        val jsonObject = JsonParser.parseString(body) as JsonObject

        val keys = mutableSetOf<String>()
        parseAllKeys(keys, jsonObject)
        return keys
    }

    private fun parseAllKeys(keys: MutableSet<String>, obj: JsonObject) {
        keys.addAll(obj.keySet())

        obj.entrySet().filter { entry -> entry.value is JsonObject }
            .forEach { entry ->
                parseAllKeys(keys, (entry.value as JsonObject))
            }
        obj.entrySet().filter { entry -> entry.value is JsonArray }
            .forEach { entry ->
                entry.value.asJsonArray.forEach { subEntry ->
                    parseAllKeys(keys, (subEntry as JsonObject))
                }
            }
    }
}
