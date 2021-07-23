package tk.q11mk.database

import java.sql.ResultSet
import java.sql.SQLSyntaxErrorException
import java.sql.Statement

class Table <P> internal constructor(
    val name: String,
    private val primaryKeyName: String,
    private val schemaName: String,
    private val stmt: Statement
) {
    val tableName = "`$schemaName`.`$name`"

    val columns get() = runCatching {
        val rs = stmt.query("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'schedule' && TABLE_NAME = 'mon'")
        val data = mutableListOf<String>()
        while (rs.next()) {
            data.add(rs.getObject("COLUMN_NAME") as String)
        }
        data
    }

    fun insertRow(data: List<Any?>) = runCatching<Unit> {
        stmt.run("INSERT INTO $tableName VALUES (${data.joinToString { it.toSqlData() }})")
    }

    fun <T> set(column: String, primaryKey: P, data: T) = runCatching<Unit> {
        stmt.run("UPDATE $tableName SET `$column`=${data.toSqlData()} WHERE `$primaryKeyName`=${primaryKey.toSqlData()}")
    }

    fun insertColumns(data: LinkedHashMap<String, List<Any?>>) = runCatching<Unit> {
        stmt.run("INSERT INTO $tableName (${data.keys.joinToString()}) VALUES (${data.values.joinToString { it.toSqlData() }})")
    }

    fun deleteCell(column: String, primaryKey: P) = runCatching<Unit> {
        set(column, primaryKey, null)
    }

    fun getColumns(primaryKey: P, columns: List<String>) = runCatching<List<Any?>> {
        val rs = stmt.query("SELECT ${columns.joinToString()} FROM $tableName WHERE `$primaryKeyName`=${primaryKey.toSqlData()}")
        val data = mutableListOf<Any?>()
        while (rs.next()) {
            for (column in columns) data.add(rs.getObject(column))
        }
        data
    }

    fun getRow(primaryKey: P) = runCatching<List<Any?>> {
        val rs = stmt.query("SELECT * FROM $tableName WHERE `$primaryKeyName`=${primaryKey.toSqlData()}")
        val data = mutableListOf<Any?>()
        while (rs.next()) {
            repeat(rs.metaData.columnCount) { i -> data.add(rs.getObject(i + 1)) }
        }
        data
    }

    @Suppress("unchecked_cast")
    fun <C> get(primaryKey: P, column: String) = runCatching<C> {
        stmt.query("SELECT `$column` FROM $tableName WHERE `$primaryKeyName`=${primaryKey.toSqlData()}").apply(ResultSet::next).getObject(column) as C
    }

    @Suppress("unchecked_cast")
    fun <C> getLike(column: String, like: String) = runCatching<List<Pair<P, C>>> {
        val rs = try {
            stmt.query("SELECT `$primaryKeyName`, `$column` FROM $tableName WHERE `$column` LIKE '$like'")
        } catch (e: SQLSyntaxErrorException) {
            println("Empty column")
            return@runCatching listOf()
        }
        val data = mutableListOf<Pair<P, C>>()
        while (rs.next()) {
            data.add(rs.getObject(primaryKeyName) as P to rs.getObject(column) as C)
        }
        data
    }

    fun has(primaryKey: P) = stmt.query("SELECT `$primaryKeyName` FROM $tableName WHERE `$primaryKeyName`=${primaryKey.toSqlData()}").next()

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