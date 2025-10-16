package com.hlasoftware.focus.ui.theme.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// Importamos los colores necesarios (asumiendo que están definidos en ui.theme)
import com.hlasoftware.focus.ui.theme.MidnightGreen // Para el círculo sólido
import com.hlasoftware.focus.ui.theme.AuthBackground // Color de fondo principal

@Composable
fun BackgroundShapes(modifier: Modifier = Modifier) {
    // Definimos un color para la flecha curva, similar al que se ve en la imagen (un rojo/magenta tenue)
    val arrowColor = Color(0xFFC04B5D)

    Box(modifier = modifier.fillMaxSize()) {

        // Contenedor para las formas en la esquina superior derecha (0.0f a 0.5f de altura)
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Ocupa la mayor parte del ancho
                .fillMaxHeight(0.4f) // Solo la parte superior
                .align(Alignment.TopEnd) // Alineado a la esquina superior derecha
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // --- 1. Círculo Sólido ---
            // Lo posicionamos para que su centro esté justo fuera de la esquina superior derecha,
            // pero su borde abarque una gran área.
            val circleRadius = canvasWidth * 0.8f
            val circleCenter = Offset(canvasWidth * 0.9f, -canvasHeight * 0.4f) // Moviéndolo hacia arriba y a la derecha

            drawCircle(
                color = MidnightGreen,
                radius = circleRadius,
                center = circleCenter
            )

            // --- 2. Flecha Curva (Usando Path) ---
            val arrowStrokeWidth = 8.dp.toPx()

            val path = Path().apply {
                // INICIO: Cerca del borde superior, a la izquierda del círculo
                moveTo(canvasWidth * 0.1f, canvasHeight * 0.3f)

                // CURVA 1: Pasa cerca del centro del círculo (sección curva hacia arriba)
                // Usamos cubicTo para crear una curva suave
                cubicTo(
                    canvasWidth * 0.5f, -canvasHeight * 0.1f,  // Control 1 (Tira la curva hacia el exterior)
                    canvasWidth * 0.9f, canvasHeight * 0.2f,   // Control 2 (Dirige la curva hacia abajo)
                    canvasWidth * 0.8f, canvasHeight * 0.6f    // PUNTO MEDIO (El punto más bajo de la curva)
                )
            }

            // Dibujar el trazo principal de la flecha
            drawPath(
                path = path,
                color = arrowColor,
                style = Stroke(
                    width = arrowStrokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Dibuja la punta de la flecha al final del Path
            val endPoint = Offset(canvasWidth * 0.8f, canvasHeight * 0.6f)
            val arrowHeadSize = 18.dp.toPx()

            val arrowHeadPath = Path().apply {
                moveTo(endPoint.x, endPoint.y)

                // La punta apunta hacia la derecha y ligeramente hacia abajo, siguiendo el final de la curva
                // Basado en el final de la curva, creamos un triángulo que simula la punta de la flecha
                lineTo(endPoint.x - arrowHeadSize * 0.5f, endPoint.y - arrowHeadSize * 0.6f)
                lineTo(endPoint.x + arrowHeadSize * 0.2f, endPoint.y - arrowHeadSize * 0.1f)
                close()
            }

            // Dibujar la punta rellena
            drawPath(
                path = arrowHeadPath,
                color = arrowColor,
            )
        }
    }
}