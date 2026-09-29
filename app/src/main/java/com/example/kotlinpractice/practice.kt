package com.example.kotlinpractice


fun main() {
    val num = 5
    var prime = true
    for( i in 1..num){
        if(num % i == 0) {
            prime = false
        }

    }
//    println("$factorial !")

}
