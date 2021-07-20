package tk.q11mk.database

import org.intellij.lang.annotations.Language
import java.sql.ResultSet
import java.sql.Statement

fun Statement.run(@Language("sql") sql: String): ResultSet {
    executeUpdate(sql)
    return resultSet
}

fun Statement.query(@Language("sql") sql: String): ResultSet {
    executeQuery(sql)
    return resultSet
}