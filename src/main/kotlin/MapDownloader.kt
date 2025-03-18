import data.Map
import java.io.File
import java.net.URI
import kotlin.system.exitProcess

class MapDownloader(private val directory: File) {

    init {
        // Make sure the directory exists; otherwise, creating files will fail
        println("Creating directory $directory...")
        val created = directory.mkdirs()

        // FIXME it seems that (at least once on the VPS) the "working" dir may be created
        //  but not the "downloads" dir
        if (created) {
            println("OK")
        } else {
            println("Failure")
            exitProcess(1)
        }

        // Check if the directory exists
        if (directory.exists() && directory.isDirectory) {
            println("Directory exists and is a directory")
        } else {
            println("Directory does not exist or is not a directory")
            exitProcess(1)
        }
    }

    fun download(map: Map): File {
        val url = URI("https://www.prendslaroue.fr/n-peloton/maps/${map.id}/gpx").toURL()
        val file = File.createTempFile("${map.name}-", ".gpx", directory)
        println("Downloading $url to $file...")
        val bytes = url.readBytes()
        file.writeBytes(bytes)
        return file
    }
}
