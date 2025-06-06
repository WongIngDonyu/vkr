package com.example.vkr.presentation.screens.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.vkr.presentation.components.rememberMapViewWithLifecycle
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraPosition
import com.example.vkr.presentation.components.*
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PolygonMapObject
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.vkr.R


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val viewModel: SearchViewModel = viewModel()
    val teams by viewModel.teams.collectAsState()
    val showDialog = viewModel.showDialog
    val teamAreas = teams.map { entity ->
        val points = parsePoints(entity.areaPoints)
        TeamArea(teamId = entity.id, teamName = entity.name, points = points, color = entity.color)
    }
    val defaultFillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.33f).toArgb()
    val defaultStrokeColor = MaterialTheme.colorScheme.onSurface.toArgb()
    Box(modifier = Modifier.fillMaxSize()) {
        if (teamAreas.isNotEmpty()) {
            AndroidView(
                factory = {
                    mapView.map.move(
                        CameraPosition(
                            Point(55.529338019374315, 37.51481091362802),
                            16.0f,
                            0.0f,
                            0.0f
                        )
                    )
                    mapView.map.apply {
                        isZoomGesturesEnabled = false
                        isScrollGesturesEnabled = true
                        isRotateGesturesEnabled = false
                        isTiltGesturesEnabled = false
                    }
                    val mapObjects = mapView.map.mapObjects
                    mapObjects.clear()
                    val polygons = mutableMapOf<PolygonMapObject, TeamArea>()
                    teamAreas.forEach { area ->
                        if (area.points.size >= 3) {
                            val polygon = Polygon(LinearRing(area.points), emptyList())
                            val obj = mapObjects.addPolygon(polygon)
                            obj.fillColor = if (area.color != 0) area.color else defaultFillColor
                            obj.strokeColor = defaultStrokeColor
                            obj.strokeWidth = 2f
                            polygons[obj] = area
                            val center = getPolygonCenter(area.points)
                            val label = mapObjects.addPlacemark(center)
                            label.setText(area.teamName)
                        }
                    }
                    mapView.map.addInputListener(object : InputListener {
                        override fun onMapTap(map: Map, point: Point) {
                            for ((polygon, area) in polygons) {
                                if (isPointInsidePolygon(polygon.geometry.outerRing.points, point)) {
                                    viewModel.onTeamClicked(area.teamId, navController)
                                    break
                                }
                            }
                        }
                        override fun onMapLongTap(map: Map, point: Point) {}
                    })
                    mapView
                },
                modifier = Modifier.matchParentSize()
            )
        }
        if (teams.isNotEmpty()) {
            Image(
                painter = painterResource(id = R.drawable.test61),
                contentDescription = "Командное изображение",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
                    .width(280.dp)
                    .heightIn(max = 140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onHideDialog,
            confirmButton = {
                TextButton(onClick = viewModel::onHideDialog) {
                    Text("Закрыть")
                }
            },
            title = { Text("Полный рейтинг команд") },
            text = {
                Column {
                    teams.sortedByDescending { it.points }.forEachIndexed { index, team ->
                        Text(
                            text = "${index + 1}. ${team.name} — ${team.points} очков",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        )
    }
}