package com.example.miniproyecto01.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miniproyecto01.ui.screens.DetailScreen
import com.example.miniproyecto01.ui.screens.FormScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "form"
    ) {
        // Ruta 1: Formulario
        composable("form") {
            FormScreen(
                onNavigateToDetail = { matricula, nombre, carrera, turno, activo ->
                    // Codificamos strings que puedan tener espacios o tildes
                    val encNombre = URLEncoder.encode(nombre, StandardCharsets.UTF_8.toString())
                    val encCarrera = URLEncoder.encode(carrera, StandardCharsets.UTF_8.toString())

                    navController.navigate("detail/$matricula/$encNombre/$encCarrera/$turno/$activo")
                }
            )
        }

        // Ruta 2: Detalle con argumentos tipados
        composable(
            route = "detail/{matricula}/{nombre}/{carrera}/{turno}/{activo}",
            arguments = listOf(
                navArgument("matricula") { type = NavType.StringType },
                navArgument("nombre") { type = NavType.StringType },
                navArgument("carrera") { type = NavType.StringType },
                navArgument("turno") { type = NavType.StringType },
                navArgument("activo") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val matricula = backStackEntry.arguments?.getString("matricula") ?: ""
            val rawNombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val rawCarrera = backStackEntry.arguments?.getString("carrera") ?: ""
            val turno = backStackEntry.arguments?.getString("turno") ?: ""
            val activo = backStackEntry.arguments?.getBoolean("activo") ?: true

            // Decodificamos de vuelta
            val nombre = URLDecoder.decode(rawNombre, StandardCharsets.UTF_8.toString())
            val carrera = URLDecoder.decode(rawCarrera, StandardCharsets.UTF_8.toString())

            DetailScreen(
                matricula = matricula,
                nombre = nombre,
                carrera = carrera,
                turno = turno,
                activo = activo,
                onBack = { navController.popBackStack() }
            )
        }
    }
}