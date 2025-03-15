import data.Map
import java.io.File
import java.net.URI

class MapDownloader(private val directory: File) {
    // TODO directory may not exist or may not be a file
    // We should enforce the caller to check that

    fun download(map: Map): File {
        val url = URI("https://www.prendslaroue.fr/n-peloton/maps/${map.id}/gpx").toURL()
        val file = File.createTempFile("${map.name}-", ".gpx", directory)
        println("Downloading $url to $file...")
        val bytes = url.readBytes()
        file.writeBytes(bytes)
        return file
    }
}
