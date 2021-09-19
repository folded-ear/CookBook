package com.brennaswitzer.cookbook.util

import java.sql.ResultSet

fun printResultSet(resultSet: ResultSet) {
    val cc = resultSet.metaData.columnCount
    val headers = (1..cc).map { resultSet.metaData.getColumnName(it) }
    val rows = mutableListOf(headers)
    while (!resultSet.isAfterLast) {
        rows.add((1..cc).map { resultSet.getObject(it).toString() })
        resultSet.next()
    }
    val lengths = (1..cc).map { 0 }.toIntArray()
    rows.forEach { r ->
        r.forEachIndexed { i, c ->
            lengths[i] = Math.max(lengths[i], c.length)
        }
    }
    rows.forEach { r ->
        r.forEachIndexed { i, c ->
            print(" " + c.padEnd(lengths[i]) + " ")
        }
        println()
    }
}
