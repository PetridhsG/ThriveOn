package gr.aueb.thriveon.domain.utils

inline fun <reified T> Any?.asTypedList(): List<T> {
    return (this as? List<*>)?.mapNotNull { it as? T } ?: emptyList()
}

fun Any?.asStringLongMap(): Map<String, Long> {
    return (this as? Map<*, *>)?.mapNotNull { (key, value) ->
        val k = key as? String
        val v = when (value) {
            is Number -> value.toLong()
            else -> null
        }
        if (k != null && v != null) k to v else null
    }?.toMap() ?: emptyMap()
}

fun Any?.asNestedStringMap(): Map<String, Map<String, String>> {
    return (this as? Map<*, *>)?.mapNotNull { (outerKey, innerValue) ->
        val key = outerKey as? String
        val valueMap = (innerValue as? Map<*, *>)?.mapNotNull { (k, v) ->
            val innerKey = k as? String
            val innerVal = v as? String
            if (innerKey != null && innerVal != null) innerKey to innerVal else null
        }?.toMap()

        if (key != null && valueMap != null) key to valueMap else null
    }?.toMap() ?: emptyMap()
}

fun Any?.asStringListMap(): Map<String, List<String>> {
    return (this as? Map<*, *>)?.mapNotNull { (key, value) ->
        val k = key as? String
        val vList = (value as? List<*>)?.mapNotNull { it as? String }
        if (k != null && vList != null) k to vList else null
    }?.toMap() ?: emptyMap()
}

fun Any?.asStringToListOfMaps(): Map<String, List<Map<String, Any>>> {
    return (this as? Map<*, *>)?.mapNotNull { (key, value) ->
        val k = key as? String
        val vList = (value as? List<*>)?.mapNotNull { item ->
            (item as? Map<*, *>)?.mapNotNull { (ik, iv) ->
                val innerKey = ik as? String ?: return@mapNotNull null
                val innerVal = iv ?: return@mapNotNull null
                innerKey to innerVal
            }?.toMap()
        }
        if (k != null && vList != null) k to vList else null
    }?.toMap() ?: emptyMap()
}

fun Any?.asMutableStringToListOfMaps(): MutableMap<String, List<Map<String, Any>>> {
    return (this as? Map<*, *>)?.mapNotNull { (key, value) ->
        val k = key as? String
        val vList = (value as? List<*>)?.mapNotNull { item ->
            (item as? Map<*, *>)?.mapNotNull { (ik, iv) ->
                val ikStr = ik as? String ?: return@mapNotNull null
                val ivVal = iv ?: return@mapNotNull null
                ikStr to ivVal
            }?.toMap()
        }
        if (k != null && vList != null) k to vList else null
    }?.toMap()?.toMutableMap() ?: mutableMapOf()
}

fun Any?.getListOfMapsForDate(dateKey: String): List<Map<String, Any>> {
    val rootMap = this as? Map<*, *> ?: return emptyList()
    val listForDate = rootMap[dateKey] as? List<*> ?: return emptyList()

    return listForDate.mapNotNull { item ->
        (item as? Map<*, *>)?.mapNotNull { (ik, iv) ->
            val key = ik as? String ?: return@mapNotNull null
            val value = iv ?: return@mapNotNull null
            key to value
        }?.toMap()
    }
}

fun Any?.asStringAnyMap(): Map<String, Any> {
    return (this as? Map<*, *>)?.mapNotNull { (key, value) ->
        val k = key as? String
        val v = value
        if (k != null && v != null) k to v else null
    }?.toMap() ?: emptyMap()
}

fun Any?.asMutableStringLongMap(): MutableMap<String, Long> {
    return (this as? Map<*, *>)?.mapNotNull { (k, v) ->
        val key = k as? String
        val value = (v as? Number)?.toLong()
        if (key != null && value != null) key to value else null
    }?.toMap()?.toMutableMap() ?: mutableMapOf()
}

fun Any?.asMutableStringMap(): MutableMap<String, String> {
    return (this as? Map<*, *>)?.mapNotNull { (k, v) ->
        val key = k as? String
        val value = v as? String
        if (key != null && value != null) key to value else null
    }?.toMap()?.toMutableMap() ?: mutableMapOf()
}

fun Any?.getStringListForKey(key: String): List<String> {
    val map = this as? Map<*, *> ?: return emptyList()
    val list = map[key] as? List<*> ?: return emptyList()
    return list.mapNotNull { it as? String }
}

fun Any?.asStringIntMap(): Map<String, Int> {
    return (this as? Map<*, *>)?.mapNotNull { (k, v) ->
        val key = k as? String
        val value = (v as? Number)?.toInt()
        if (key != null && value != null) key to value else null
    }?.toMap() ?: emptyMap()
}

fun Any?.asStringMap(): Map<String, String> {
    return (this as? Map<*, *>)?.mapNotNull { (k, v) ->
        val key = k as? String
        val value = v as? String
        if (key != null && value != null) key to value else null
    }?.toMap() ?: emptyMap()
}

fun Any?.asStringList(): List<String> {
    return (this as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
}
