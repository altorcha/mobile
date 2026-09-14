package com.example.practica02

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val CalcBackground = Color(0xFF009364)
val DarkScreen = Color(0xFF2C2C2E)
val ButtonColor = Color(0xFF0F9F6C)
val AppBackground = Color(0xFF1B2E4B)

@Composable
fun CalculatorScreen() {
    var operacion by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    fun onButtonClick(simbolo: String) {
        if (isError && simbolo != "C") {
            operacion = ""
            isError = false
        }

        when (simbolo) {
            "C" -> {
                operacion = ""
                isError = false
            }
            "⌫" -> {
                if (operacion.isNotEmpty()) {
                    operacion = operacion.dropLast(1)
                }
            }
            "%" -> {
                if (operacion.isNotEmpty()) {
                    val res = LogicaCalc.calcular(operacion)
                    if (!res.second) {
                        operacion = LogicaCalc.calcularPorcentaje(res.first)
                    }
                }
            }
            "/", "x", "-", "+" -> {
                if (operacion.isNotEmpty()) {
                    val lastChar = operacion.last()
                    if (lastChar == '+' || lastChar == '-' || lastChar == 'x' || lastChar == '/') {
                        operacion = operacion.dropLast(1) + simbolo
                    } else {
                        operacion += simbolo
                    }
                }
            }
            "=" -> {
                if (operacion.isNotEmpty()) {
                    val res = LogicaCalc.calcular(operacion)
                    if (res.second) {
                        isError = true
                    } else {
                        operacion = res.first
                    }
                }
            }
            else -> {
                if (operacion.length < 35) {
                    operacion += simbolo
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(CalcBackground)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkScreen)
                    .padding(20.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = operacion,
                        textAlign = TextAlign.End,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 1
                    )
                    if (isError) {
                        Text(
                            text = "No se puede dividir por 0",
                            textAlign = TextAlign.End,
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("C", Modifier.weight(1f)) { onButtonClick("C") }
                    CalcBtn("⌫", Modifier.weight(1f)) { onButtonClick("⌫") }
                    CalcBtn("%", Modifier.weight(1f)) { onButtonClick("%") }
                    CalcBtn("/", Modifier.weight(1f)) { onButtonClick("/") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("7", Modifier.weight(1f)) { onButtonClick("7") }
                    CalcBtn("8", Modifier.weight(1f)) { onButtonClick("8") }
                    CalcBtn("9", Modifier.weight(1f)) { onButtonClick("9") }
                    CalcBtn("x", Modifier.weight(1f)) { onButtonClick("x") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("4", Modifier.weight(1f)) { onButtonClick("4") }
                    CalcBtn("5", Modifier.weight(1f)) { onButtonClick("5") }
                    CalcBtn("6", Modifier.weight(1f)) { onButtonClick("6") }
                    CalcBtn("-", Modifier.weight(1f)) { onButtonClick("-") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("1", Modifier.weight(1f)) { onButtonClick("1") }
                    CalcBtn("2", Modifier.weight(1f)) { onButtonClick("2") }
                    CalcBtn("3", Modifier.weight(1f)) { onButtonClick("3") }
                    CalcBtn("+", Modifier.weight(1f)) { onButtonClick("+") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CalcBtn("0", Modifier.weight(2f)) { onButtonClick("0") }
                    CalcBtn(".", Modifier.weight(1f)) { onButtonClick(".") }
                    CalcBtn("=", Modifier.weight(1f)) { onButtonClick("=") }
                }
            }
        }
    }
}

@Composable
fun CalcBtn(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ButtonColor)
            .clickable { onClick() }
    ) {
        Text(text = text, fontSize = 22.sp, color = Color.White)
    }
}