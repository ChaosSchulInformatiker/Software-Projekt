package tk.q11mk

import tk.q11mk.database.Database
import tk.q11mk.database.Table
import tk.q11mk.utils.getSecretProperty

val database = Database(getSecretProperty("db_url"), getSecretProperty("db_username"), getSecretProperty("db_password"))

val schedulesSchema = database.getSchema("schedules").getOrDefault(database.createSchema("schedules").getOrThrow())

fun getScheduleTable(day: String) = schedulesSchema.getTable<Int>(day).getOrDefault(schedulesSchema.createTable(day, "lesson").getOrThrow())
fun getLesson(day: String, lesson: Int, teacher: String) = getScheduleTable(day).getCell<String>(lesson, teacher).getOrNull()

val constantsSchema = database.getSchema("constants").getOrDefault(database.createSchema("constants").getOrThrow())

val teachersTable = constantsSchema.getTable<String>("teachers").getOrDefault(constantsSchema.createTable("teachers", "short", Table.Column.Type.STRING, "NOT_NULL").getOrThrow())
fun getTeacher(short: String) = teachersTable.getCell<String>(short, "first_name").getOrNull() to teachersTable.getCell<String>(short, "last_name").getOrNull()

val roomsTable = constantsSchema.getTable<String>("rooms").getOrDefault(constantsSchema.createTable("rooms", "id", Table.Column.Type.STRING, "NOT_NULL").getOrThrow())
fun getRoomName(id: String) = roomsTable.getCell<String>(id, "name").getOrNull()

val classesTable = constantsSchema.getTable<String>("classes").getOrDefault(constantsSchema.createTable("classes", "name", Table.Column.Type.STRING, "NOT_NULL").getOrThrow())
fun getClass(name: String) = classesTable.getCell<String>(name, "teacher").getOrNull() to classesTable.getCell<String>(name, "room").getOrNull()

val accountsSchema = database.getSchema("accounts").getOrDefault(database.createSchema("accounts").getOrThrow())

val idsTable = constantsSchema.getTable<String>("ids").getOrDefault(constantsSchema.createTable("ids", "id", Table.Column.Type.STRING, "NOT_NULL").getOrThrow())
fun getAccountFromId(id: Long) = idsTable.getCell<String>(id.toString(), "first_name").getOrNull()

val accountClassesTable = constantsSchema.getTable<String>("classes").getOrDefault(constantsSchema.createTable("classes", "id", Table.Column.Type.STRING, "NOT_NULL").getOrThrow())
fun getClassFromId(id: Long) = idsTable.getCell<String>(id.toString(), "class").getOrNull() to idsTable.getCell<List<String>>(id.toString(), "subjects")