package com.hlasoftware.focus.features.create_workgroup.domain.usecase

import android.net.Uri
import com.hlasoftware.focus.features.create_workgroup.domain.repository.CreateWorkgroupRepository
import com.hlasoftware.focus.features.profile.domain.usecase.GetProfileUseCase

class CreateWorkgroupUseCase(
    private val createWorkgroupRepository: CreateWorkgroupRepository,
    private val getProfileUseCase: GetProfileUseCase
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        imageUri: Uri?,
        adminId: String
    ): Result<Unit> {
        return try {
            // Primero, obtenemos el perfil del admin usando el UseCase correcto
            val adminProfileResult = getProfileUseCase(adminId)

            // Usamos .fold para manejar los dos casos posibles del Result: éxito o fracaso
            adminProfileResult.fold(
                onSuccess = { adminProfile ->
                    // Si todo ha ido bien, tenemos el perfil y podemos acceder al nombre
                    val adminName = adminProfile.name.takeIf { it.isNotEmpty() } ?: "Unknown"
                    // Ahora sí, creamos el grupo con el nombre del admin
                    createWorkgroupRepository.createWorkgroup(name, description, imageUri, adminId, adminName)
                },
                onFailure = { exception ->
                    // Si no se puede obtener el perfil, propagamos el error
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
