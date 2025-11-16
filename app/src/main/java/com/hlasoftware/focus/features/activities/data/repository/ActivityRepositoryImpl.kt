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
            val snapshot = firestore.collection("activities")
                .whereEqualTo("userId", userId)
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

    override suspend fun getActivitiesForMonth(userId: String, year: Int, month: Int): List<ActivityModel> {
        return try {
            val snapshot = firestore.collection("activities")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(ActivityModel::class.java)?.copy(id = document.id)
            }.filter { activity ->
                val activityDate = LocalDate.parse(activity.date, DateTimeFormatter.ISO_LOCAL_DATE)
                activityDate.year == year && activityDate.monthValue == month
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
                "userId" to userId,
                "title" to title,
                "description" to description,
                "date" to date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                "startTime" to time?.format(DateTimeFormatter.ofPattern("HH:mm")),
                "endTime" to null,
                "type" to "TASK"
            )

            firestore.collection("activities")
                .add(activityData)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteActivity(activityId: String) {
        try {
            firestore.collection("activities").document(activityId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getActivityById(activityId: String): ActivityModel? {
        return try {
            val document = firestore.collection("activities").document(activityId).get().await()
            document.toObject(ActivityModel::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
