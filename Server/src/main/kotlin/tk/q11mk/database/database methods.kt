package tk.q11mk.database

import tk.q11mk.utils.getSecretProperty

val database = Database(getSecretProperty("db_url"), getSecretProperty("db_username"), getSecretProperty("db_password"))

val schedulesSchema = database.getSchema("schedules").getOrElse { database.createSchema("schedules").getOrThrow() }

fun getScheduleTable(day: String) = schedulesSchema.getTable<Int>(day).getOrElse { schedulesSchema.createTable(day, "lesson" to DataType.INT).getOrThrow() }
fun getLesson(day: String, lesson: Int, teacher: String) = getScheduleTable(day).get<String>(lesson, teacher).getOrNull()

val constantsSchema = database.getSchema("constants").getOrElse { database.createSchema("constants").getOrThrow() }

val teachersTable = constantsSchema.getTable<String>("teachers").getOrElse { constantsSchema.createTable("teachers", "short" to DataType.STRING(4), "last_name" to DataType.STRING, "first_name" to DataType.STRING).getOrThrow() }
fun getTeacher(short: String) = teachersTable.get<String>(short, "first_name").getOrNull() to teachersTable.get<String>(short, "last_name").getOrNull()

val roomsTable = constantsSchema.getTable<String>("rooms").getOrElse { constantsSchema.createTable("rooms", "id" to DataType.STRING(8), "name" to DataType.STRING).getOrThrow() }
fun getRoomName(id: String) = roomsTable.get<String>(id, "name").getOrNull()

val classesTable = constantsSchema.getTable<String>("classes").getOrElse { constantsSchema.createTable("classes", "name" to DataType.STRING(5), "room" to DataType.STRING(8), "teacher" to DataType.STRING(4)).getOrThrow() }
fun getClass(name: String) = classesTable.get<String>(name, "teacher").getOrNull() to classesTable.get<String>(name, "room").getOrNull()

val accountsSchema = database.getSchema("accounts").getOrElse { database.createSchema("accounts").getOrThrow() }

val idsTable = constantsSchema.getTable<String>("ids").getOrElse { constantsSchema.createTable("ids", "id" to DataType.STRING(10), "last_name" to DataType.STRING, "first_name" to DataType.STRING, "email" to DataType.STRING, "teacher" to DataType.BOOL).getOrThrow() }
fun getAccountFromId(id: Long) = idsTable.get<String>(id.toString(), "first_name").getOrNull()

val accountClassesTable = constantsSchema.getTable<String>("classes").getOrElse { constantsSchema.createTable("classes", "id" to DataType.STRING(10), "class" to DataType.STRING(5), "subjects" to DataType.CSV<String>(50)).getOrThrow() }
fun getClassFromId(id: Long) = idsTable.get<String>(id.toString(), "class").getOrNull() to idsTable.get<String>(id.toString(), "subjects")

/*val substitutesSchema = database.getSchema("substitutes").getOrElse { database.createSchema("substitutes").getOrThrow() }
val substitutesTable = substitutesSchema.getTable<>()*/