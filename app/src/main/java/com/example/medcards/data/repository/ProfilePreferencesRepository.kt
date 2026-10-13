package com.example.medcards.data.repository

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException

class ProfilePreferencesRepository(context: Context) {
    private val applicationContext = context.applicationContext
    private val preferences = applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val photoFile = File(applicationContext.filesDir, PROFILE_PHOTO_FILE)

    val displayName: String
        get() = preferences.getString(KEY_DISPLAY_NAME, DEFAULT_DISPLAY_NAME)
            ?: DEFAULT_DISPLAY_NAME

    val photoPath: String?
        get() = preferences.getString(KEY_PHOTO_PATH, null)?.takeIf { photoFile.isFile }

    fun saveDisplayName(name: String) {
        val normalizedName = name.trim()
        require(normalizedName.isNotEmpty()) { "El nombre no puede estar vacío." }
        check(
            preferences.edit().putString(KEY_DISPLAY_NAME, normalizedName).commit()
        ) { "No se pudo guardar el nombre del perfil." }
    }

    @Throws(IOException::class)
    fun saveProfilePhoto(uri: Uri) {
        val input = applicationContext.contentResolver.openInputStream(uri)
            ?: throw IOException("No se pudo abrir la imagen seleccionada.")
        input.use { source ->
            photoFile.outputStream().use { output -> source.copyTo(output) }
        }
        check(
            preferences.edit().putString(KEY_PHOTO_PATH, photoFile.absolutePath).commit()
        ) { "No se pudo guardar la foto del perfil." }
    }

    private companion object {
        const val PREFERENCES_NAME = "profile_preferences"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_PHOTO_PATH = "photo_path"
        const val PROFILE_PHOTO_FILE = "profile_photo"
        const val DEFAULT_DISPLAY_NAME = "Marcelo"
    }
}
