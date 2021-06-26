package tk.q11mk.database

import java.sql.SQLException
import java.sql.Statement

class Schema internal constructor(val name: String, private val stmt: Statement) {

    /**
     * Creates a table in this schema
     *
     * @param name The name of the table
     * @param pkName The name of the primary Key. This is added as a not null auto increment int.
     * @return A wrapper of the table
     *
     * @author Simon
     */
    fun createTable(name: String, pkName: String) = try {
        val sql = """CREATE TABLE `${this.name}`.`$name` (
`$pkName` INT NOT NULL AUTO_INCREMENT,
PRIMARY KEY (`$pkName`));"""
        stmt.executeUpdate(sql)
        Result.success(Table<Int>(name, pkName, this.name, stmt))
    } catch (e: SQLException) {
        Result.failure(e)
    }

    fun <P> createTable(name: String, pkName: String, type: Table.Column.Type<P>, vararg flags: String = arrayOf("NOT NULL", "AUTO_INCREMENT")) = try {
        val sql = """CREATE TABLE `${this.name}`.`$name` (
`$pkName` $type ${
            if ("NOT NULL" in flags) "" else "NULL"
        } ${flags.joinToString(" ")},
PRIMARY KEY (`$pkName`));"""
        stmt.executeUpdate(sql)
        Result.success(Table<P>(name, pkName, this.name, stmt))
    } catch (e: SQLException) {
        Result.failure(e)
    }

    fun <P> getTable(name: String): Result<Table<P>> = try {
        throw SQLException()
    } catch (e: SQLException) {
        Result.failure<Nothing>(e)
    }
}