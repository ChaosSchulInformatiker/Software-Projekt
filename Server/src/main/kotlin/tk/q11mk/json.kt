package tk.q11mk

import org.json.simple.JSONObject

interface JSONSerializable {
    fun serialize(): JSONObject
}

interface JSONDeserializable<T> {
    fun deserialize(json: JSONObject): T
}