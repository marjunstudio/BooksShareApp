package to.msn.wings.booksshareapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20
    ): BooksResponse
}

data class BooksResponse(
    val items: List<VolumeInfo>? = null
)

data class VolumeInfo(
    val id: String,
    val volumeInfo: BookInfo,
    val saleInfo: SaleInfo? = null
)

data class BookInfo(
    val title: String,
    val authors: List<String>? = null,
    val imageLinks: ImageLinks? = null,
    val pageCount: Int? = null,
    val printType: String? = null,
    val categories: List<String>? = null,
    val averageRating: Double? = null,
    val ratingsCount: Int? = null,
    val language: String? = null,
    val description: String? = null,
    val publisher: String? = null,
    val publishedDate: String? = null,
    val industryIdentifiers: List<IndustryIdentifier>? = null
)

data class ImageLinks(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

data class SaleInfo(
    val isEbook: Boolean = false,
    val saleability: String,
    val listPrice: Price? = null,
    val retailPrice: Price? = null,
    val buyLink: String? = null
)

data class Price(
    val amount: Double? = null,
    val currencyCode: String? = null
) 