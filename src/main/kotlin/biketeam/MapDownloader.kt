package biketeam

import biketeam.data.Map
import java.io.File
import java.net.URI

/**
 * MapDownloader is able to download maps from Biketeam.
 *
 * @property directory the download directory (it must exist)
 */
class MapDownloader(private val directory: File) {

    fun download(map: Map): File {
        val url = URI("https://www.prendslaroue.fr/n-peloton/maps/${map.id}/gpx").toURL()
        val file = File.createTempFile("${map.name}-", ".gpx", directory)
        println("Downloading $url to $file...")
        val bytes = url.readBytes()
        file.writeBytes(bytes)
        return file
    }
}
