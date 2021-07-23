package tk.q11mk.database

import tk.q11mk.utils.getSecretProperty

val database = Database(getSecretProperty("db_url"), getSecretProperty("db_username"), getSecretProperty("db_password"))

val schedulesSchema = database.getSchema("schedules").getOrElse { database.createSchema("schedules").getOrThrow() }

fun getScheduleTable(dayIndex: Int) = schedulesSchema.getTable<Int>(dayIndex.toString()).getOrElse { schedulesSchema.createTable(dayIndex.toString(), "lesson" to DataType.INT).getOrThrow() }

/**
 * Gets the lesson content.
 * Day is 0-based: 0 is monday, 4 is friday
 * Lesson is 1-based: 1-6 correspond to lesson no. 1-6, 7 is MP, 8-11 are 7-10
 */
fun getLesson(dayIndex: Int, lesson: Int, teacher: String) = getScheduleTable(dayIndex).get<String>(lesson, teacher).getOrNull()

val constantsSchema = database.getSchema("constants").getOrElse { database.createSchema("constants").getOrThrow() }

val teachersTable = constantsSchema.getTable<String>("teachers").getOrElse { constantsSchema.createTable("teachers", "short" to DataType.STRING(4), "last_name" to DataType.STRING, "first_name" to DataType.STRING).getOrThrow() }
fun getTeacher(short: String) = teachersTable.get<String>(short, "first_name").getOrNull() to teachersTable.get<String>(short, "last_name").getOrNull()

val roomsTable = constantsSchema.getTable<String>("rooms").getOrElse { constantsSchema.createTable("rooms", "id" to DataType.STRING(8), "name" to DataType.STRING).getOrThrow() }
fun getRoomName(id: String) = roomsTable.get<String>(id, "name").getOrNull()

val classesTable = constantsSchema.getTable<String>("classes").getOrElse { constantsSchema.createTable("classes", "name" to DataType.STRING(5), "room" to DataType.STRING(8), "teacher" to DataType.STRING(4)).getOrThrow() }
fun getClass(name: String) = classesTable.get<String>(name, "teacher").getOrNull() to classesTable.get<String>(name, "room").getOrNull()

val accountsSchema = database.getSchema("accounts").getOrElse { database.createSchema("accounts").getOrThrow() }

val idsTable = accountsSchema.getTable<String>("ids").getOrElse { accountsSchema.createTable("ids", "id" to DataType.STRING(10), "last_name" to DataType.STRING, "first_name" to DataType.STRING, "email" to DataType.STRING(64), "teacher" to DataType.BOOL).getOrThrow() }
fun getAccountFromId(id: Long) = idsTable.getRow(id.toString()).getOrNull()?.takeIf { it.size == 5 }?.let { Account(it[1] as String, it[2] as String, it[3] as String, it[4] as Boolean) }
data class Account(val lastName: String, val firstName: String, val email: String, val isTeacher: Boolean)

val accountClassesTable = accountsSchema.getTable<String>("classes").getOrElse { accountsSchema.createTable("classes", "id" to DataType.STRING(10), "class" to DataType.STRING(5), "subjects" to DataType.CSV<String>(50)).getOrThrow() }
fun getClassFromId(id: Long) = accountClassesTable.get<String>(id.toString(), "class").getOrNull() to accountClassesTable.get<String>(id.toString(), "subjects").getOrNull()

/*val substitutesSchema = database.getSchema("substitutes").getOrElse { database.createSchema("substitutes").getOrThrow() }
val substitutesTable = substitutesSchema.getTable<>()*/