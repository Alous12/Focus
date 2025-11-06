package com.hlasoftware.focus.features.activities.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActivityRepositoryImpl(private val firestore: FirebaseFirestore) : ActivityRepository {

    private val activitiesCollection = firestore.collection("activities")

    override suspend fun getActivities(userId: String, date: LocalDate): List<ActivityModel> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return try {
            val snapshot = activitiesCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", dateString)
                .get()
                .await()
            snapshot.toObjects(ActivityModel::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addActivity(activity: ActivityModel) {
        try {
            activitiesCollection.add(activity).await()
        } catch (e: Exception) {

        }
    }
}
