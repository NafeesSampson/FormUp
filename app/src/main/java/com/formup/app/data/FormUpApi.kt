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

class FormUpApi(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    companion object {
        const val BASE_URL = "http://prog7314.runasp.net"
        private const val TIMEOUT_MS = 15_000
    }

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

    suspend fun updateCoach(
        displayName: String,
        preferredLanguage: String? = null
    ) {
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

    suspend fun updateTeam(
        teamName: String,
        ageGroup: Int
    ) {
        request(
            method = "PUT",
            path = "/api/team",
            body = JSONObject()
                .put("teamName", teamName)
                .put("ageGroup", ageGroup)
        )
    }

    suspend fun updateMatchScores(
        matchId: String,
        teamScore: Int,
        opponentScore: Int
    ) {
        request(
            method = "PATCH",
            path = "/api/match/$matchId",
            body = JSONObject()
                .put("teamScore", teamScore)
                .put("opponentScore", opponentScore)
        )
    }

    suspend fun saveSquad(
        matchId: String,
        players: List<SquadUpdate>
    ) {
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

    suspend fun saveAttendance(
        matchId: String,
        attendance: List<AttendanceRecord>
    ) {
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

    suspend fun saveStats(
        matchId: String,
        stats: List<StatUpdate>
    ) {
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

    suspend fun createPlayer(
        name: String,
        position: String,
        dateOfBirth: String?
    ) {
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

    suspend fun deletePlayer(id: String) {
        request(
            method = "DELETE",
            path = "/api/players/$id"
        )
    }

    private fun getObject(path: String): JSONObject {
        return JSONObject(
            request(
                method = "GET",
                path = path
            )
        )
    }

    private fun request(
        method: String,
        path: String,
        body: Any? = null
    ): String {

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

        val responseCode = connection.responseCode

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

data class ApiException(
    val code: Int,
    override val message: String
) : Exception(message)

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
                            minutes = stat.optInt("minutesPlayed")
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

private fun JSONObject.toTeamProfile(): TeamProfile {

    val name =
        optString("teamName")

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