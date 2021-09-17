package tk.q11mk.schedule

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import tk.q11mk.database.Table
import tk.q11mk.database.getScheduleTable

@Serializable(with = Schedule.Serializer::class)
data class Schedule @Deprecated("Use Day") constructor(
    val mo: Day,
    val tu: Day,
    val we: Day,
    val th: Day,
    val fr: Day,
) {
    operator fun get(index: Int) = when (index) {
        0 -> mo
        1 -> tu
        2 -> we
        3 -> th
        4 -> fr
        else -> throw IndexOutOfBoundsException()
    }

    @Serializable
    data class Day(
        val lessons: List<Lesson?>
    ) {
        companion object {
            val csvSplitRegex = Regex(",\\s*")
            val visonRegex = Regex("""","|":"""")

            fun fromRequest(dayIndex: Int, clazz: String, subjectsCSV: String): Day {
                val subjects = subjectsCSV.split(csvSplitRegex)

                val dayTable = getScheduleTable(dayIndex)

                val teachers = dayTable.columns.getOrThrow().toMutableList()
                teachers.remove("hour")

                val lessons = mutableListOf<Lesson?>()

                for (t in teachers) {
                    becauseVisarIsIncompetent(dayTable, t, subjects, lessons, """%"class":"[%$clazz]"%""")
                    becauseVisarIsIncompetent(dayTable, t, subjects, lessons, """%"class":"[%$clazz,%]"%""")
                }

                return Day(lessons)
            }
        }
    }

    @Serializable
    data class DBLesson(
        @SerialName("class") val clazz: String,
        val subject: String,
        val room: String? = null,
        val substituted: Boolean = false,
        val substitute_teacher: String? = null,
        val substitute_room: String? = null,
    )

    @Serializable
    data class Lesson(
        val subject: String,
        val substituted: Boolean,
        val teacher: String,
        val room: String?
    )

    class Serializer : KSerializer<Schedule> {
        override val descriptor = ScheduleSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: Schedule) {
            encoder.encodeSerializableValue(ScheduleSurrogate.serializer(), ScheduleSurrogate(arrayOf(
                value.mo, value.tu, value.we, value.th, value.fr
            )))
        }

        override fun deserialize(decoder: Decoder): Schedule {
            val days = decoder.decodeSerializableValue(ScheduleSurrogate.serializer()).days
            return Schedule(days[0], days[1], days[2], days[3], days[4])
        }
    }

    @Serializable
    @SerialName("Schedule")
    private class ScheduleSurrogate(@Suppress("unused") val days: Array<Day>) {
        init { require(days.size == 5) }
    }
}

fun becauseVisarIsIncompetent(dayTable: Table, t: String, subjects: List<String>, lessons: MutableList<Schedule.Lesson?>, like: String) {
    val likes = dayTable.getLike<String>(t, like, "hour").getOrThrow()
    for (l in likes) {
        val split = l.second.substring(1, l.second.length - 1).split(Schedule.Day.visonRegex)
        val subject = split[5] + (split[7].takeUnless { it =="0" } ?: "")
        if (subject !in subjects) continue
        while (l.first.toString().toInt() > lessons.size) lessons.add(null)
        val substituted = split[11].toBoolean()
        lessons[l.first.toString().toInt() - 1] = Schedule.Lesson(
            subject,
            substituted,
            if (substituted) split[13] else t,
            if (substituted) split[15] else split[9]
        )
    }
}