package com.formup.app.ui.auth

data class TeamFormState(
    val logoUri: String? = null,
    val teamName: String = "",
    val sport: String = "",
    val ageGroupLevel: String = "",
    val seasonStartDate: String = "",
    val nameError: String? = null,
    val sportError: String? = null,
    val levelError: String? = null
)

val SportOptions = listOf("Soccer", "Basketball", "Rugby", "Netball", "Hockey", "Cricket")

val AgeGroupLevelOptions = listOf(
    "Youth (U10)", "Youth (U12)", "Youth (U14)", "Youth (U16)", "Youth (U18)",
    "Adult - Amateur", "Adult - Semi-Pro", "Adult - Professional"
)

data class CreatedTeam(
    val teamName: String,
    val sport: String,
    val ageGroupLevel: String,
    val seasonStartDate: String,
    val logoUri: String?,
    val joinCode: String
)

/**
 * TODO: this is a client-side placeholder. Once the API exists, team creation
 * should return the join code from the server (see Coaches/Teams endpoints);
 * this generator goes away and CreatedTeam.joinCode comes from that response.
 */
fun generateTeamCode(): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // no O/0/I/1 to avoid ambiguity
    return (1..6).map { chars.random() }.joinToString("")
}