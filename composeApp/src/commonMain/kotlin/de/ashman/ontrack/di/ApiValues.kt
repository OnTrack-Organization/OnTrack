package de.ashman.ontrack.di

// DEFAULT FETCH LIMIT
const val DEFAULT_FETCH_LIMIT = 20

// TMDB: MOVIES AND TV SHOWS
// TMDB supported keine custom fields
const val TMDB_CLIENT_NAME = "TMDBClient"
const val TMDB_URL = "api.themoviedb.org"
const val TMDB_PATH_URL = "3/"
const val DEFAULT_MOVIE_QUERY = "Ghibli"
const val DEFAULT_SHOW_QUERY = "Jojo"

// OPEN LIBRARY: BOOKS
const val OPEN_LIB_CLIENT_NAME = "OpenLibraryClient"
const val OPEN_LIB_URL = "openlibrary.org"
const val DEFAULT_BOOK_QUERY = "Project Hail Mary"

// BOARDGAMEGEEK: BOARD GAMES
const val BGG_CLIENT_NAME = "BoardGameGeekClient"
const val BGG_URL = "boardgamegeek.com"
const val BGG_PATH = "xmlapi2/"
const val DEFAULT_BG_QUERY = "Carcassonne"

// IGDB / TWITCH: VIDEO GAMES
const val IGDB_CLIENT_NAME = "IGDBClient"
const val IGDB_URL = "api.igdb.com"
const val IGDB_PATH = "v4/"
const val DEFAULT_VG_QUERY = "Smash Bros"

const val TWITCH_TOKEN_CLIENT_NAME = "TwitchTokenClient"
const val TWITCH_TOKEN_URL = "id.twitch.tv"
const val TWITCH_TOKEN_PATH = "oauth2/token"

// SPOTIFY: ALBUMS
const val SPOTIFY_CLIENT_NAME = "SpotifyClient"
const val SPOTIFY_URL = "api.spotify.com"
const val SPOTIFY_PATH = "v1/"
const val DEFAULT_ALBUM_QUERY = "Sincere"

const val SPOTIFY_TOKEN_CLIENT_NAME = "SpotifyTokenClient"
const val SPOTIFY_TOKEN_URL = "accounts.spotify.com"
const val SPOTIFY_TOKEN_PATH = "api/token"