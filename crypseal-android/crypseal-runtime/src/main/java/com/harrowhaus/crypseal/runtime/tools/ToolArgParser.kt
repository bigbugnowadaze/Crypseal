package com.harrowhaus.crypseal.runtime.tools

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException

/**
 * Parses a JSON string into a Map<String, Any> for tool argument dispatch.
 * Uses Android's built-in org.json (no external dependencies needed).
 */
object ToolArgParser {

    /**
     * Parse a JSON object string into a flat Map<String, Any>.
     * Returns a ToolArgResult indicating success/failure.
     */
    fun parse(json: String?): ToolArgResult {
        if (json.isNullOrBlank()) {
            return ToolArgResult(emptyMap(), null)
        }
        return try {
            val obj = JSONObject(json)
            val map = jsonObjectToMap(obj)
            ToolArgResult(map, null)
        } catch (e: JSONException) {
            ToolArgResult(null, "Malformed JSON args: ${e.message}")
        }
    }

    private fun jsonObjectToMap(obj: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        for (key in obj.keys()) {
            val value = obj.get(key)
            map[key] = convertValue(value)
        }
        return map
    }

    private fun jsonArrayToList(arr: JSONArray): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until arr.length()) {
            list.add(convertValue(arr.get(i)))
        }
        return list
    }

    private fun convertValue(value: Any): Any {
        return when (value) {
            is JSONObject -> jsonObjectToMap(value)
            is JSONArray -> jsonArrayToList(value)
            JSONObject.NULL -> ""
            else -> value // String, Int, Long, Double, Boolean
        }
    }
}

data class ToolArgResult(
    val args: Map<String, Any>?,
    val error: String?
) {
    val success: Boolean get() = args != null
}
