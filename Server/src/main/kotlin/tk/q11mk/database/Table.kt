package tk.q11mk.database

import java.sql.ResultSet
import java.sql.Statement

class Table <P> internal constructor(
    val name: String,
    private val primaryKeyName: String,
    private val schemaName: String,
    private val stmt: Statement
) {
    fun insertRow(data: List<Any?>) = runCatching<Unit> {
        stmt.run("INSERT INTO `$schemaName`.`$name` VALUES (${data.joinToString()})")
    }

    fun <T> set(column: String, primaryKey: P, data: T) = runCatching<Unit> {
        stmt.run("UPDATE `$schemaName`.`$name` SET `$column`=$data WHERE `$primaryKeyName`=$primaryKey")
    }

    fun insertColumns(data: LinkedHashMap<String, List<Any?>>) = runCatching<Unit> {
        stmt.run("INSERT INTO `$schemaName`.`$name` (${data.keys.joinToString()}) VALUES (${data.values.joinToString()})")
    }

    fun deleteCell(column: String, primaryKey: P) = runCatching<Unit> {
        set(column, primaryKey, null)
    }

    fun getColumns(primaryKey: P, columns: List<String>) = runCatching<List<Any?>> {
        val rs = stmt.run("SELECT ${columns.joinToString()} FROM `$schemaName`.`$name` WHERE `$primaryKeyName`=$primaryKey")
        val data = mutableListOf<Any?>()
        while (rs.next()) {
            for (column in columns) data.add(rs.getObject(column))
        }
        data
    }

    fun getRow(primaryKey: P) = runCatching<List<Any?>> {
        val rs = stmt.query("SELECT * FROM `$schemaName`.`$name` WHERE `$primaryKeyName`=$primaryKey")
        val data = mutableListOf<Any?>()
        while (rs.next()) {
            repeat(rs.metaData.columnCount) { i -> data.add(rs.getObject(i)) }
        }
        data
    }

    @Suppress("unchecked_cast")
    fun <C> get(primaryKey: P, column: String) = runCatching<C> {
        stmt.query("SELECT `$column` FROM `$schemaName`.`$name` WHERE `$primaryKeyName`=$primaryKey").apply(ResultSet::next).getObject(column) as C
    }

    fun <T> renameColumn(column: String, name: String, type: DataType<T>) = runCatching {
        stmt.run("ALTER TABLE `$schemaName`.`${this.name}` CHANGE COLUMN `$column` `$name` $type;")
    }

    fun <T> changeColumnType(column: String, type: DataType<T>) = runCatching {
        stmt.run("ALTER TABLE `$schemaName`.`$name` MODIFY `$column` $type")
    }

    fun <T> addColumn(name: String, type: DataType<T>) = runCatching {
        stmt.run("ALTER TABLE `$schemaName`.`$name` ADD `$name` $type")
    }

    fun deleteColumn(name: String) = runCatching {
        stmt.run("ALTER TABLE `$schemaName`.`$name` DROP COLUMN `$name`")
    }

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