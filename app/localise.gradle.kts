import java.io.File
import java.net.URL
import java.util.Properties

// ====================================================================
// FUNCIÓN DE LECTURA SEGURA (LOCAL.PROPERTIES)
// ====================================================================
fun getLocalProperty(key: String): String {
    val localProperties = Properties()
    // Busca local.properties en la raíz del proyecto (donde está el .gitignore)
    val localPropertiesFile = project.rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { input ->
            localProperties.load(input)
        }
    } else {
        error("Error: El archivo 'local.properties' no se encuentra en la raíz del proyecto. Asegúrese de crearlo.")
    }
    return localProperties.getProperty(key) ?: error("Error: La clave '$key' no se encuentra en 'local.properties'. Por favor, añádala con su valor.")
}

// ====================================================================
// MAPEO DE LOCALES (Ajusta esto cuando añadas más idiomas)
// ====================================================================
val localeMapping = mapOf(
    // 1. ESPAÑOL BOLIVIA (es-BO) -> Carpeta base 'values'.
    "es-BO" to "values",

    // 2. INGLÉS (en-US) -> Carpeta con sufijo 'values-en' o 'values-en-rUS'.
    "en-US" to "values-en",

    // 3. ESPAÑOL ESPAÑA (es-ES) -> Carpeta con sufijo 'values-es' o 'values-es-rES'.
    "es-ES" to "values-es"
)

// ====================================================================
// TAREA DE DESCARGA
// ====================================================================
tasks.register("downloadLocoStrings") {
    group = "localization"
    description = "Downloads strings.xml files from Localise.biz via API"

    // La clave debe llamarse LOCO_API_KEY en tu local.properties
    val locoApiKey = getLocalProperty("LOCO_API_KEY")

    // Ruta de la carpeta de recursos dentro de tu módulo 'app'
    val resDir = file("src/main/res")

    doLast {
        localeMapping.forEach { (apiCode, resFolder) ->
            downloadFile(
                apiKey = locoApiKey,
                apiCode = apiCode,
                resFolder = resFolder,
                resDir = resDir
            )
        }
    }
}

// ====================================================================
// FUNCIÓN DE DESCARGA POR URL
// ====================================================================
fun downloadFile(apiKey: String, apiCode: String, resFolder: String, resDir: File) {
    println("-> Descargando [$apiCode] en $resFolder")

    val outputFile = file("$resDir/$resFolder/strings.xml")

    // Crea la carpeta de destino si no existe (ej: values-es-rBO)
    outputFile.parentFile.mkdirs()

    // URL de exportación para un solo locale en formato Android XML
    val exportUrl = "https://localise.biz/api/export/locale/$apiCode.xml?key=$apiKey&format=android"

    try {
        URL(exportUrl).openStream().use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        println("✅ Descarga de $apiCode exitosa en $resFolder/strings.xml.")
    } catch (e: Exception) {
        // En caso de error, muestra un mensaje útil.
        println("❌ ERROR al descargar $apiCode. Verifique que el locale '$apiCode' exista en Localise.biz y la API Key sea correcta: ${e.message}")
    }
}

// ====================================================================
// TAREA DE SUBIDA DE CADENAS BASE (PARA CUANDO AÑADES NUEVAS FRASES)
// ====================================================================
tasks.register("uploadBaseStrings") {
    group = "localization"
    description = "Uploads the base strings.xml file to Localise.biz"

    val locoApiKey = getLocalProperty("LOCO_API_KEY")
    // Asegúrate de que esta ruta sea correcta para tu strings.xml base
    val baseStringsFile = file("src/main/res/values/strings.xml")
    // NOTA: Tu strings.xml base está en 'values' (sin sufijo)

    doLast {
        exec {
            // USAR SOLAMENTE -F PARA TODOS LOS PARÁMETROS
            commandLine("curl",
                "https://localise.biz/api/import/android",
                // La clave API también debe ir con -F
                "-F", "key=$locoApiKey",
                // El índice también va con -F
                "-F", "index=id",
                // El locale también va con -F (usamos es-BO como base)
                "-F", "locale=es-BO",
                // El archivo a subir

                "-F", "status=translated",

                "-F", "file=@${baseStringsFile.absolutePath}")
        }
        println("⬆️ Cadenas base subidas a Localise.biz.")
    }
}