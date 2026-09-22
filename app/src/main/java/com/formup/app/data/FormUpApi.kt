package com.formup.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

//this class is used to handle all communications between the android app and the backend asp.net web api
class FormUpApi(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    //setting the api link as base_url and the timeout value in millieseconds
    companion object {
        const val BASE_URL = "http://prog7314.runasp.net"
        private const val TIMEOUT_MS = 15_000
    }

    //loading all general appp data such as teams, season info and coach within a single call
    suspend fun loadSeason(): SeasonSnapshot = withContext(Dispatchers.IO) {
        val coach = getObject("/api/coach")
        val team = getObject("/api/team")
        val season = getObject("/api/season-data")

        SeasonSnapshot(
            coach = coach.toCoachProfile(),
            team = team.toTeamProfile(),
            players = season.optJSONArray("players").toPlayers(),
            fixtures = season.optJSONArray("matches").toFixtures()
        )
    }

    //updates the coach profile in the settings such as their display name and their preferred language(POE)
    suspend fun updateCoach(
        displayName: String,
        preferredLanguage: String? = null
    ) = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("displayName", displayName)

        if (preferredLanguage != null) {
            body.put("preferredLanguage", preferredLanguage)
        }

        request(
            method = "PATCH",
            path = "/api/coach",
            body = body
        )
    }

    //Changes the coaches managed team info from settings page
    suspend fun updateTeam(
        teamName: String,
        ageGroup: Int
    ) = withContext(Dispatchers.IO) {
        request(
            method = "PUT",
            path = "/api/team",
            body = JSONObject()
                .put("teamName", teamName)
                .put("ageGroup", ageGroup)
        )
    }

    //updates final score of a specific match
    suspend fun updateMatchScores(
        matchId: String,
        teamScore: Int,
        opponentScore: Int
    ) = withContext(Dispatchers.IO) {
        request(
            method = "PATCH",
            path = "/api/match/$matchId",
            body = JSONObject()
                .put("teamScore", teamScore)
                .put("opponentScore", opponentScore)
        )
    }

    //gets all players for a specific match
    suspend fun getSquad(
        matchId: String
    ): List<SquadUpdate> = withContext(Dispatchers.IO) {

        val response = request(
            method = "GET",
            path = "/api/match/$matchId/squad"
        )

        val array = JSONArray(response)

        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)

                add(
                    SquadUpdate(
                        playerId = item.optString("playerId"),
                        status = item.optString("status", "Available"),
                        selection = item.optString("selection", "NotSelected")
                    )
                )
            }
        }
    }

    //saves the squad selection choices for the match
    suspend fun saveSquad(
        matchId: String,
        players: List<SquadUpdate>
    ) = withContext(Dispatchers.IO) {
        val array = JSONArray()

        players.forEach {
            array.put(
                JSONObject()
                    .put("playerId", it.playerId)
                    .put("status", it.status)
                    .put("selection", it.selection)
            )
        }

        request(
            method = "PUT",
            path = "/api/match/$matchId/squad",
            body = array
        )
    }

    //fetches the attendance records for a match(POE)
    suspend fun getAttendance(
        matchId: String
    ): List<AttendanceRecord> = withContext(Dispatchers.IO) {

        val response = request(
            method = "GET",
            path = "/api/match/$matchId/attendance"
        )

        val array = JSONArray(response)

        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)

                add(
                    AttendanceRecord(
                        playerId = item.optString("playerId"),
                        status = item.optString("status", "NoReply")
                    )
                )
            }
        }
    }

    //saving the match attendance to the match
    suspend fun saveAttendance(
        matchId: String,
        attendance: List<AttendanceRecord>
    ) = withContext(Dispatchers.IO) {
        val array = JSONArray()

        attendance.forEach { record ->
            array.put(
                JSONObject()
                    .put("playerId", record.playerId)
                    .put("status", record.status)
            )
        }

        request(
            method = "PUT",
            path = "/api/match/$matchId/attendance",
            body = array
        )
    }

    //fetches the player attendance records for a training session(POE)
    suspend fun getSessionAttendance(
        sessionId: String
    ): List<AttendanceRecord> = withContext(Dispatchers.IO) {

        val response = request(
            method = "GET",
            path = "/api/session/$sessionId/attendance"
        )

        val array = JSONArray(response)

        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)

                add(
                    AttendanceRecord(
                        playerId = item.optString("playerId"),
                        status = item.optString("status", "NotRecorded")
                    )
                )
            }
        }
    }

    //saves training attendance
    suspend fun saveSessionAttendance(
        sessionId: String,
        attendance: List<AttendanceRecord>
    ) = withContext(Dispatchers.IO) {

        val array = JSONArray()

        attendance.forEach { record ->
            array.put(
                JSONObject()
                    .put("playerId", record.playerId)
                    .put("status", record.status)
            )
        }

        request(
            method = "PUT",
            path = "/api/session/$sessionId/attendance",
            body = array
        )
    }

    //saves the match stats for each individual player
    suspend fun saveStats(
        matchId: String,
        stats: List<StatUpdate>
    ) = withContext(Dispatchers.IO) {
        val array = JSONArray()

        stats.forEach {
            array.put(
                JSONObject()
                    .put("playerId", it.playerId)
                    .put("goals", it.goals)
                    .put("assists", it.assists)
                    .put("cleansheet", it.cleansheet)
                    .put("minutesPlayed", it.minutesPlayed)
                    .put("rating", it.rating)
            )
        }

        request(
            method = "PUT",
            path = "/api/match/$matchId/stats",
            body = array
        )
    }

    //creates a new player for a specific team
    suspend fun createPlayer(
        name: String,
        position: String,
        dateOfBirth: String?
    ) = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("name", name)
            .put("position", position)

        if (dateOfBirth == null) {
            body.put("dateOfBirth", JSONObject.NULL)
        } else {
            body.put("dateOfBirth", dateOfBirth)
        }

        request(
            method = "POST",
            path = "/api/players",
            body = body
        )
    }

    suspend fun deletePlayer(id: String) = withContext(Dispatchers.IO) {
        request(
            method = "DELETE",
            path = "/api/players/$id"
        )
    }

    //creating a match for a specific team and scheduling it
    suspend fun createMatch(
        opponent: String,
        matchDateIso: String,
        venue: String            // must be exactly "Home" or "Away" to match api
    ): String = withContext(Dispatchers.IO) {
        val response = request(
            method = "POST",
            path = "/api/match",
            body = JSONObject()
                .put("opponent", opponent)
                .put("matchDate", matchDateIso)
                .put("venue", venue)
        )
        JSONObject(response).optString("id")
    }

    //creating a training session for specific team, just session date and notes gets saved to api
    suspend fun createSession(
        sessionDateIso: String,
        notes: String?
    ): String = withContext(Dispatchers.IO) {
        val body = JSONObject().put("sessionDate", sessionDateIso)
        body.put("notes", notes ?: JSONObject.NULL)
        val response = request(method = "POST", path = "/api/session", body = body)
        JSONObject(response).optString("id")
    }

    // is a helper function to send a GET request and parse the response as a JSON Object
    private fun getObject(path: String): JSONObject {
        return JSONObject(
            request(
                method = "GET",
                path = path
            )
        )
    }

    //this function is used to perform the actual http requests to the api
    private fun request(
        method: String,
        path: String,
        body: Any? = null
    ): String {

        //opens the http connection to each api endpoint
        val connection =
            (URL(BASE_URL + path).openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                useCaches = false

                setRequestProperty(
                    "Accept",
                    "application/json"
                )
            }

        //attaches the firebase authentication token (jwt) to prove a user is logged in
        if (path != "/health") {
            val user = auth.currentUser
                ?: throw ApiException(
                    401,
                    "You are not signed in."
                )

            val token = Tasks
                .await(user.getIdToken(false))
                .token
                ?: throw ApiException(
                    401,
                    "Firebase did not return an ID token."
                )

            connection.setRequestProperty(
                "Authorization",
                "Bearer $token"
            )
        }

        //writing the data to the request body for the post, put or patch requests
        if (
            body != null &&
            method != "GET" &&
            method != "DELETE"
        ) {
            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection.outputStream.use {
                it.write(
                    body
                        .toString()
                        .toByteArray(Charsets.UTF_8)
                )
            }
        }

        val responseCode = connection.responseCode //checking the servers response to our reqyests

        //throwing an error if the response code is a failure code so anything that isnt between 200 and 300
        val stream =
            if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

        val response =
            stream
                ?.bufferedReader()
                ?.use { it.readText() }
                .orEmpty()

        connection.disconnect()

        if (responseCode !in 200..299) {

            val serverMessage =
                runCatching {
                    JSONObject(response)
                        .optString("title")
                }.getOrNull()
                    ?.takeIf { it.isNotBlank() }

            val message =
                serverMessage
                    ?: response.ifBlank {
                        "API request failed ($responseCode)"
                    }

            throw ApiException(
                responseCode,
                message
            )
        }

        return response
    }
}

//used to represent an error returned by the server
data class ApiException(
    val code: Int,
    override val message: String
) : Exception(message)

//these are just data models that are used throughout the app
data class SeasonSnapshot(
    val coach: CoachProfile,
    val team: TeamProfile,
    val players: List<Player>,
    val fixtures: List<Fixture>
)

data class SquadUpdate(
    val playerId: String,
    val status: String,
    val selection: String
)

data class AttendanceRecord(
    val playerId: String,
    val status: String
)

data class StatUpdate(
    val playerId: String,
    val goals: Int,
    val assists: Int,
    val cleansheet: Boolean,
    val minutesPlayed: Int,
    val rating: Double
)

//used to convert a raw json array into a list of player objects
private fun JSONArray?.toPlayers(): List<Player> {

    if (this == null) {
        return emptyList()
    }

    return (0 until length()).map { index ->

        val player = getJSONObject(index)

        Player(
            id = player.getString("id"),
            number = 0,
            name = player.optString("name"),
            position = player.optString("position"),
            status = AvailabilityStatus.Fit
        )
    }
}
//used to convert a raw json array into a list of fixture objects
private fun JSONArray?.toFixtures(): List<Fixture> {

    if (this == null) {
        return emptyList()
    }

    return (0 until length()).map { index ->
        getJSONObject(index).toFixture()
    }
}

private fun JSONObject.toFixture(): Fixture {

    val matchDate = optString("matchDate")

    val date = parseDate(matchDate)

    val teamScore =
        if (isNull("teamScore")) {
            0
        } else {
            optInt("teamScore")
        }

    val opponentScore =
        if (isNull("opponentScore")) {
            0
        } else {
            optInt("opponentScore")
        }

    val statsObject =
        optJSONObject("stats")

    val playerStats =
        buildMap<String, MatchLine> {

            if (statsObject != null) {

                statsObject.keys().forEach { playerId ->

                    val stat =
                        statsObject.optJSONObject(playerId)
                            ?: return@forEach

                    put(
                        playerId,
                        MatchLine(
                            goals = stat.optInt("goals"),
                            assists = stat.optInt("assists"),
                            minutes = stat.optInt("minutesPlayed"),
                            rating = stat.optDouble("rating", 0.0)
                        )
                    )
                }
            }
        }

    return Fixture(
        id = getString("id"),
        opponent = optString("opponent"),
        competition = "Match",
        dateLabel = date.first,
        kickoff = date.second,
        venue = optString("venue"),
        isHome = true,
        played =
            !isNull("teamScore") &&
                    !isNull("opponentScore"),
        teamScore = teamScore,
        opponentScore = opponentScore,
        playerStats = playerStats
    )
}
//used to convert a raw json coach data into a coachprofile objects
private fun JSONObject.toCoachProfile(): CoachProfile {

    return CoachProfile(
        name =
            optString("displayName")
                .ifBlank {
                    optString(
                        "email",
                        "Coach"
                    )
                },
        role = "Coach",
        badges = emptyList(),
        languageCode =
            optString(
                "preferredLanguage",
                "en"
            ),
        matchReminders = false,
        weeklySummary = false
    )
}

//used to convert a raw json array into a list of player objects
private fun JSONObject.toTeamProfile(): TeamProfile {

    val name =
        if (isNull("teamName")) "" else optString("teamName")

    val ageGroup =
        if (isNull("ageGroup")) {
            null
        } else {
            optInt("ageGroup")
        }

    return TeamProfile(
        name = name,
        squad =
            ageGroup?.let {
                "U$it"
            } ?: "",
        season = "",
        homeGround = "",
        clubCode = "",
        inviteLink = ""
    )
}
//used to convert server date strings from api into readable date and time data for the user
private fun parseDate(
    value: String
): Pair<String, String> {

    if (value.isBlank()) {
        return "—" to "—"
    }

    return runCatching {

        val patterns =
            listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                "yyyy-MM-dd'T'HH:mm:ssX"
            )

        val parsed =
            patterns
                .asSequence()
                .mapNotNull { pattern ->
                    runCatching {
                        SimpleDateFormat(
                            pattern,
                            Locale.US
                        ).apply {
                            timeZone =
                                TimeZone.getDefault()
                        }.parse(value)
                    }.getOrNull()
                }
                .firstOrNull()
                ?: Date(0)

        val date =
            SimpleDateFormat(
                "MMM dd",
                Locale.getDefault()
            ).format(parsed)

        val time =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(parsed)

        date to time

    }.getOrElse {
        "—" to "—"
    }
}