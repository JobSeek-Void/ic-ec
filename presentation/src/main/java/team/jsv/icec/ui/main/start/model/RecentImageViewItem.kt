package team.jsv.icec.ui.main.start.model

data class ImageViewItem(
    val imagePaths : List<RecentImage>
)

data class RecentImage(
    val url: String
)