package tk.q11mk.schedule

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Schedule.Serializer::class)
data class Schedule(
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
    )

    @Serializable
    data class Lesson(
        val subject: String,
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
