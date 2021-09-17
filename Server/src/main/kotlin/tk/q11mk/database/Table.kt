package tk.q11mk.database

import java.sql.ResultSet
import java.sql.SQLSyntaxErrorException
import java.sql.Statement

class Table internal constructor(
    val name: String,
    private val schemaName: String,
    private val stmt: Statement
) {
    val tableName = "`$schemaName`.`$name`"

    val columns get() = runCatching<List<String>> {
        val rs = stmt.query("DESCRIBE $tableName")
        val data = mutableListOf<String>()
        while (rs.next()) {
            data.add(rs.getObject("Field") as String)
        }
        data
    }

    /*@get:Suppress("unchecked_cast")
    val keys get() = runCatching<List<P>> {
        val rs = stmt.query("SELECT `$primaryKeyName` FROM $tableName")
        val data = mutableListOf<P>()
        while (rs.next()) {
            data.add(rs.getObject(primaryKeyName) as P)
        }
        data
    }*/

    fun getColumn(name: String) = runCatching {
        val rs = stmt.query("SELECT `$name` FROM $tableName")
        val data = mutableListOf<Any>()
        while (rs.next()) {
            data.add(rs.getObject(name))
        }
        data
    }

    fun getColumns(vararg names: String) = runCatching {
        val rs = stmt.query("SELECT ${names.joinToString("`,`", "`", "`")} FROM $tableName")
        val data = mutableListOf<List<Any>>()
        while (rs.next()) {
            data.add(names.map { rs.getObject(it) })
        }
        data
    }

    fun insertRow(data: List<Any?>) = runCatching<Unit> {
        stmt.run("INSERT INTO $tableName VALUES (${data.joinToString { it.toSqlData() }})")
    }

    fun <T> set(column: String, primaryKey: Pair<String, Any>, data: T) = runCatching<Unit> {
        stmt.run("UPDATE $tableName SET `$column`=${data.toSqlData()} WHERE `${primaryKey.first}`=${primaryKey.second.toSqlData()}")
    }

    fun insertColumns(data: LinkedHashMap<String, List<Any?>>) = runCatching<Unit> {
        stmt.run("INSERT INTO $tableName (${data.keys.joinToString()}) VALUES (${data.values.joinToString { it.toSqlData() }})")
    }

    fun deleteCell(column: String, primaryKey: Pair<String, Any>) = runCatching<Unit> {
        set(column, primaryKey, null)
    }

    fun getColumns(primaryKey: Pair<String, Any>, columns: List<String>) = runCatching<List<Any?>> {
        val rs = stmt.query("SELECT ${columns.joinToString()} FROM $tableName WHERE `${primaryKey.first}`=${primaryKey.second.toSqlData()}")
        val data = mutableListOf<Any?>()
        while (rs.next()) {
            for (column in columns) data.add(rs.getObject(column))
        }
        data
    }

    fun getRow(primaryKey: Pair<String, Any>) = runCatching<List<Any?>> {
        val rs = stmt.query("SELECT * FROM $tableName WHERE `${primaryKey.first}`=${primaryKey.second.toSqlData()}")
        val data = mutableListOf<Any?>()
        while (rs.next()) {
            repeat(rs.metaData.columnCount) { i -> data.add(rs.getObject(i + 1)) }
        }
        data
    }

    @Suppress("unchecked_cast")
    fun <C> get(primaryKey: Pair<String, Any>, column: String) = runCatching<C> {
        stmt.query("SELECT `$column` FROM $tableName WHERE `${primaryKey.first}`=${primaryKey.second.toSqlData()}").apply(ResultSet::next).getObject(column) as C
    }

    @Suppress("unchecked_cast")
    fun <C> getLike(column: String, like: String, primaryKeyName: String) = runCatching<List<Pair<Any, C>>> {
        val rs = try {
            stmt.query("SELECT `$primaryKeyName`, `$column` FROM $tableName WHERE `$column` LIKE '$like'")
        } catch (e: SQLSyntaxErrorException) {
            println("Empty column")
            return@runCatching listOf()
        }
        val data = mutableListOf<Pair<Any, C>>()
        while (rs.next()) {
            data.add(rs.getObject(primaryKeyName) to rs.getObject(column) as C)
        }
        data
    }

    fun has(primaryKey: Pair<String, Any>) = stmt.query("SELECT `${primaryKey.first}` FROM $tableName WHERE `${primaryKey.first}`=${primaryKey.second.toSqlData()}").next()

    fun <T> renameColumn(column: String, name: String, type: DataType<T>) = runCatching {
        stmt.run("ALTER TABLE `$schemaName`.`${this.name}` CHANGE COLUMN `$column` `$name` $type;")
    }

    fun <T> changeColumnType(column: String, type: DataType<T>) = runCatching {
        stmt.run("ALTER TABLE $tableName MODIFY `$column` $type")
    }

    fun <T> addColumn(name: String, type: DataType<T>) = runCatching {
        stmt.run("ALTER TABLE `$schemaName`.`${this.name}` ADD `$name` $type")
    }

    fun deleteColumn(name: String) = runCatching {
        stmt.run("ALTER TABLE `$schemaName`.`${this.name}` DROP COLUMN `$name`")
    }

    private fun Any?.toSqlData() = if (this is String) "'$this'" else toString()

    //private val columns: MutableList<Column<*>> = mutableListOf(Column(primaryKeyName, Column.Type.INT, null, listOf("NOT NULL", "AUTO_INCREMENT")))

    /*/**
     * Add a column at the end of the table
     *
     * @param name The name of the column
     * @param type The type (e.g. INT, JSON, ...)
     * @param default The default value. If it's a string enclose it in single quotes (')
     * @param flags Additional flags (e.g. NOT NULL, AUTO_INCREMENT)
     * @return A wrapper of the column
     *
     * @author Simon
     */
    fun <T> addColumn(name: String, type: Column.Type<T>, default: T? = null, vararg flags: String) =
        addColumnAfter(name, type, default, columns.last().name, *flags)

    /**
     * Add a column after the column with the specified name
     *
     * @see addColumn
     * @param after The name of the column before the new one
     *
     * @author Simon
     */
    fun <T> addColumnAfter(name: String, type: Column.Type<T>, default: T? = null, after: String, vararg flags: String) = try {
        stmt.executeUpdate("ALTER TABLE `$schemaName`.`${this.name}`\nADD COLUMN `$name` $type ${
            if ("NOT NULL" in flags) "" else "NULL"
        } ${flags.joinToString(" ")} ${default?.let { "DEFAULT $it" } ?: ""} AFTER `$after`;")
        val c = Column(name, type, default, flags.toList())
        columns.add(c)
        Result.success(c)
    } catch (e: SQLException) {
        Result.failure(e)
    }

    @Suppress("unchecked_cast")
    fun <T> getCell(pk: P, column: String): Result<T> = try {
        Result.success(stmt.run("SELECT $column FROM $name WHERE $pkName = $pk").getNString(0)) as Result<T>
    } catch (e: SQLException) {
        Result.failure<Nothing>(e)
    }

    data class Column<T> internal constructor(val name: String, val type: Type<T>, val default: T? = null, val flags: List<String>) {
        class Type<T> private constructor(val str: String) {
            override fun toString() = str

            companion object {
                val STRING = Type<String>("STRING")
                val INT = Type<Int>("INT")
                val JSON = Type<String>("JSON")
            }
        }
    }*/
}