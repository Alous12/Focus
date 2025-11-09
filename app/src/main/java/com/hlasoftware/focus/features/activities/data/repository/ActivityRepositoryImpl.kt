package com.hlasoftware.focus.features.activities.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ActivityRepositoryImpl(private val firestore: FirebaseFirestore) : ActivityRepository {

    override suspend fun getActivities(userId: String, date: LocalDate): List<ActivityModel> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return try {
            // Apunta a la colección principal 'activities' y filtra por userId
            val snapshot = firestore.collection("activities")
                .whereEqualTo("userId", userId) // Filtra las actividades del usuario
                .whereEqualTo("date", dateString)
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(ActivityModel::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun createActivity(
        userId: String,
        title: String,
        description: String,
        date: LocalDate,
        time: LocalTime?
    ) {
        try {
            val activityData = hashMapOf(
                "userId" to userId, // ¡Importante! Añadir el ID del usuario al documento
                "title" to title,
                "description" to description,
                "date" to date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                "startTime" to time?.format(DateTimeFormatter.ofPattern("HH:mm")),
                "endTime" to null, // Puedes ajustar esto si lo necesitas
                "type" to "TASK" // Tipo por defecto, puedes ajustarlo
            )

            // Apunta a la colección principal 'activities'
            firestore.collection("activities")
                .add(activityData)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
