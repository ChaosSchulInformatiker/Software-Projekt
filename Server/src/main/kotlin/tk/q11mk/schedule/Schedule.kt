package tk.q11mk.schedule

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import tk.q11mk.JSONDeserializable
import tk.q11mk.JSONSerializable
import tk.q11mk.RequestException

data class Schedule(
    val mo: Day,
    val tu: Day,
    val we: Day,
    val th: Day,
    val fr: Day,
) : JSONSerializable {
    data class Day(
        val lessons: List<Lesson?>
    ) : JSONSerializable {
        override fun serialize(): JSONObject {
            val lessons = JSONArray()

            this.lessons.mapTo(lessons) { it?.serialize() }

            return JSONObject().apply { set("lessons", lessons) }
        }

        companion object : JSONDeserializable<Day> {
            override fun deserialize(json: JSONObject): Day {
                TODO("Not yet implemented")
            }
        }
    }

    data class Lesson(
        val subject: String,
        val teacher: String,
        val room: String?
    ) : JSONSerializable {
        override fun serialize(): JSONObject {
            val lesson = JSONObject()

            lesson["subject"] = subject
            lesson["teacher"] = teacher
            room?.let { lesson["room"] = it }

            return lesson
        }
    }

    override fun serialize(): JSONObject {
        val days = JSONArray()

        arrayOf(mo, tu, we, th, fr).mapTo(days) { it.serialize() }

        return JSONObject().apply { set("days", days) }
    }

    companion object : JSONDeserializable<Schedule> {
        override fun deserialize(json: JSONObject): Schedule {
            val days = (json["days"] as JSONArray).map { Day.deserialize(it as JSONObject) }

            if (days.size != 5)
                throw RequestException("A week has 5 days!")

            return Schedule(days[0], days[1], days[2], days[2], days[4])
        }
    }
}
