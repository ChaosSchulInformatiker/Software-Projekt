package tk.q11mk.database

import org.intellij.lang.annotations.Language
import java.sql.ResultSet
import java.sql.Statement

fun Statement.run(@Language("sql") sql: String) {
    //println("Update:\t\t$sql")
    executeUpdate(sql)
}

fun Statement.query(@Language("sql") sql: String): ResultSet {
    //println("Query:\t\t$sql")
    executeQuery(sql)
    return resultSet
}