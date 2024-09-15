package de.ashman.ontrack.media.music

fun ArtistDto.toDomain(): Artist = Artist(
    externalUrls = ExternalUrls(
        spotify = externalUrls.spotify
    ),
    followers = Followers(
        href = followers.href,
        total = followers.total
    ),
    genres = genres,
    href = href,
    id = id,
    images = images.map { it.toDomain() },
    name = name,
    popularity = popularity,
    type = type,
    uri = uri
)

fun ImageDto.toDomain(): Image = Image(
    height = height,
    url = url,
    width = width
)
