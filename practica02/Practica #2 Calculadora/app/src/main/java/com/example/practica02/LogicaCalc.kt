package com.example.practica02

object LogicaCalc {
    fun calcular(operacion: String): Pair<String, Boolean> {
        try {
            val tokens = mutableListOf<String>()
            var currentNum = ""
            for (char in operacion) {
                if (char == '+' || char == '-' || char == 'x' || char == '/') {
                    if (currentNum.isNotEmpty()) {
                        tokens.add(currentNum)
                        currentNum = ""
                    }
                    tokens.add(char.toString())
                } else {
                    currentNum += char
                }
            }
            if (currentNum.isNotEmpty()) {
                tokens.add(currentNum)
            }

            var i = 0
            while (i < tokens.size) {
                if (tokens[i] == "x" || tokens[i] == "/") {
                    val left = tokens[i - 1].toDoubleOrNull() ?: 0.0
                    val right = tokens[i + 1].toDoubleOrNull() ?: return Pair(operacion, false)

                    val res = if (tokens[i] == "x") {
                        left * right
                    } else {
                        if (right == 0.0) return Pair(operacion, true)
                        left / right
                    }
                    tokens[i - 1] = res.toString()
                    tokens.removeAt(i)
                    tokens.removeAt(i)
                    i -= 1
                }
                i++
            }

            i = 0
            while (i < tokens.size) {
                if (tokens[i] == "+" || tokens[i] == "-") {
                    val left = tokens[i - 1].toDoubleOrNull() ?: 0.0
                    val right = tokens[i + 1].toDoubleOrNull() ?: return Pair(operacion, false)

                    val res = if (tokens[i] == "+") left + right else left - right
                    tokens[i - 1] = res.toString()
                    tokens.removeAt(i)
                    tokens.removeAt(i)
                    i -= 1
                }
                i++
            }

            val finalResult = tokens.firstOrNull()?.toDoubleOrNull() ?: 0.0
            val formatRes = if (finalResult % 1.0 == 0.0) {
                finalResult.toLong().toString()
            } else {
                finalResult.toString()
            }
            return Pair(formatRes, false)
        } catch (e: Exception) {
            return Pair(operacion, false)
        }
    }

    fun calcularPorcentaje(numStr: String): String {
        val num = numStr.toDoubleOrNull() ?: return ""
        val res = num / 100.0
        return if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
    }
}