package com.course.studentservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StudentServiceApplication

fun main(args: Array<String>) {
    runApplication<StudentServiceApplication>(*args)
}
