package com.formup.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.formup.app.data.ActivityEntry
import com.formup.app.data.AppNotification
import com.formup.app.data.AttendanceRecord
import com.formup.app.data.AvailabilityStatus
import com.formup.app.data.CoachProfile
import com.formup.app.data.Fixture
import com.formup.app.data.FormUpApi
import com.formup.app.data.MatchEvent
import com.formup.app.data.MatchEventType
import com.formup.app.data.MatchLine
import com.formup.app.data.NotificationKind
import com.formup.app.data.Player
import com.formup.app.data.SquadUpdate
import com.formup.app.data.StatUpdate
import com.formup.app.data.TeamProfile
import com.formup.app.data.TeamUpdateItem
import com.formup.app.data.UpdateKind
import com.formup.app.ui.calendar.AttendanceUiState
import com.formup.app.ui.calendar.CalendarDay
import com.formup.app.ui.calendar.CalendarEvent
import com.formup.app.ui.calendar.CalendarUiState
import com.formup.app.ui.calendar.DayMarker
import com.formup.app.ui.calendar.LineupPlayer
import com.formup.app.ui.calendar.MatchDetailsUiState
import com.formup.app.ui.calendar.RosterEntry
import com.formup.app.ui.calendar.RosterRowAction
import com.formup.app.ui.calendar.RosterStatus
import com.formup.app.ui.calendar.SubPlayer
import com.formup.app.ui.home.Availability
import com.formup.app.ui.home.HomeUiState
import com.formup.app.ui.home.LastMatch
import com.formup.app.ui.home.NextMatch
import com.formup.app.ui.home.TeamUpdate
import com.formup.app.ui.home.UpdateTone
import com.formup.app.ui.home.components.HomeTab
import com.formup.app.ui.navigation.Destination
import com.formup.app.ui.navigation.destination
import com.formup.app.ui.navigation.tab
import com.formup.app.ui.notifications.NotificationGroup
import com.formup.app.ui.notifications.NotificationItem
import com.formup.app.ui.notifications.NotificationsUiState
import com.formup.app.ui.profile.ActivityItem
import com.formup.app.ui.profile.ActivityTone
import com.formup.app.ui.profile.LanguageOption
import com.formup.app.ui.profile.ProfileUiState
import com.formup.app.ui.profile.SeasonStat
import com.formup.app.ui.profile.SupportedLanguages
import com.formup.app.ui.profile.TeamInfo
import com.formup.app.ui.stats.DualStat
import com.formup.app.ui.stats.FixtureOption
import com.formup.app.ui.stats.LastMatchAnalysis
import com.formup.app.ui.stats.MatchReportUiState
import com.formup.app.ui.stats.MatchStatRow
import com.formup.app.ui.stats.PerformanceSummary
import com.formup.app.ui.stats.PlayerRating
import com.formup.app.ui.stats.PlayerStatEntry
import com.formup.app.ui.stats.StatsInputUiState
import com.formup.app.ui.stats.StatsUiState
import com.formup.app.ui.stats.SummaryStat
import com.formup.app.ui.stats.TacticalNote
import com.formup.app.ui.stats.TeamScoreInfo
import com.formup.app.ui.stats.TimelineEvent
import com.formup.app.ui.stats.TimelineEventType
import com.formup.app.ui.stats.TopPerformer
import com.formup.app.ui.team.PlayerStatus
import com.formup.app.ui.team.SquadPlayer
import com.formup.app.ui.team.TeamUiState
import com.formup.app.ui.theme.FormUpColors
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

// Central ViewModel holding application state, API sync, and screen navigation stack
class FormUpViewModel : ViewModel() {

    // ---------------------------------------------------------------- state

    private val api = FormUpApi()
    private val playerList = mutableStateListOf<Player>()
    private val fixtureList = mutableStateListOf<Fixture>()
    private val notificationList = mutableStateListOf<AppNotification>()
    private val updateList = mutableStateListOf<TeamUpdateItem>()
    private val activityList = mutableStateListOf<ActivityEntry>()
    private val attendanceCache = mutableStateMapOf<String, Map<String, String>>()
    private val matchSquadSelections = mutableStateMapOf<String, MutableMap<String, String>>()

    var coach by mutableStateOf(CoachProfile("", "Coach", emptyList(), "en", false, false))
        private set
    var team by mutableStateOf(TeamProfile("", "", "", "", "", ""))
        private set

    init {
        refreshFromApi()
    }

    // Fetches full season snapshot (coach profile, team details, roster, fixtures) from backend
    fun refreshFromApi() {
        if (FirebaseAuth.getInstance().currentUser == null) return
        viewModelScope.launch {
            runCatching { api.loadSeason() }
                .onSuccess { snapshot ->
                    playerList.clear()
                    playerList.addAll(snapshot.players)
                    fixtureList.clear()
                    fixtureList.addAll(snapshot.fixtures)
                    coach = snapshot.coach
                    team = snapshot.team
                    rebuildDerivedActivity()
                }
                .onFailure { error ->
                    notify(error.message ?: "Could not load data from FormUp API")
                }
        }
    }

    private fun rebuildDerivedActivity() {
        activityList.clear()
        fixtureList.filter { it.played }.sortedByDescending { it.dateLabel }.take(4).forEach { match ->
            activityList.add(
                ActivityEntry(
                    id = "match-${match.id}",
                    title = "Match vs. ${match.opponent}",
                    subtitle = "${resultWord(match)} ${match.scoreLine}",
                    date = match.dateLabel.uppercase(),
                    isTraining = false
                )
            )
        }
    }

    private var _competitionFilter by mutableStateOf<String?>(null)
    val competitionFilter: String? get() = _competitionFilter

    var message by mutableStateOf<String?>(null)
        private set

    val players: List<Player> get() = playerList
    val fixtures: List<Fixture> get() = fixtureList
    val notifications: List<AppNotification> get() = notificationList
    val competitions: List<String> get() = fixtureList.map { it.competition }.distinct()

    // ----------------------------------------------------------- navigation

    private val backStack = mutableStateListOf<Destination>(Destination.Home)

    val current: Destination get() = backStack.last()
    val currentTab: HomeTab get() = current.tab
    val canGoBack: Boolean get() = backStack.size > 1

    fun navigate(destination: Destination) {
        if (backStack.last() != destination) backStack.add(destination)
    }

    fun back(): Boolean {
        if (backStack.size <= 1) return false
        backStack.removeAt(backStack.lastIndex)
        return true
    }

    fun selectTab(tab: HomeTab) {
        val root = tab.destination
        backStack.clear()
        backStack.add(root)
    }

    fun notify(text: String) {
        message = text
    }

    fun consumeMessage() {
        message = null
    }

    // ------------------------------------------------------------- lookups

    fun fixture(id: String): Fixture? = fixtureList.firstOrNull { it.id == id }

    fun player(id: String): Player? = playerList.firstOrNull { it.id == id }

    val nextFixture: Fixture? get() = fixtureList.firstOrNull { !it.played }
    val lastPlayedFixture: Fixture? get() = fixtureList.lastOrNull { it.played }

    private val filteredFixtures: List<Fixture>
        get() = fixtureList.filter { competitionFilter == null || it.competition == competitionFilter }

    private val playedFixtures: List<Fixture> get() = filteredFixtures.filter { it.played }

    fun goalsOf(player: Player): Int =
        fixtureList.sumOf { it.playerStats[player.id]?.goals ?: 0 }

    fun assistsOf(player: Player): Int =
        fixtureList.sumOf { it.playerStats[player.id]?.assists ?: 0 }

    fun matchesOf(player: Player): Int =
        fixtureList.count { it.played && it.playerStats.containsKey(player.id) }

    fun minutesOf(player: Player): Int =
        fixtureList.sumOf { it.playerStats[player.id]?.minutes ?: 0 }

    val lineupCount: Int get() = playerList.count { it.inLineup }

    private var calendarOffset by mutableStateOf(0)

    fun previousCalendarMonth() { calendarOffset-- }
    fun nextCalendarMonth() { calendarOffset++ }

    val teamState: TeamUiState
        get() = TeamUiState(
            players = playerList.map { player ->
                SquadPlayer(
                    id = player.id,
                    fullName = player.name,
                    position = player.position,
                    status = when (player.status) {
                        AvailabilityStatus.Fit -> PlayerStatus.FIT
                        AvailabilityStatus.Doubtful -> PlayerStatus.DOUBT
                        AvailabilityStatus.Out -> PlayerStatus.OUT
                    }
                )
            }
        )

    val calendarState: CalendarUiState
        get() {
            val shown = Calendar.getInstance().apply { add(Calendar.MONTH, calendarOffset) }
            val month = shown.get(Calendar.MONTH)
            val year = shown.get(Calendar.YEAR)
            val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(shown.time)
            shown.set(Calendar.DAY_OF_MONTH, 1)
            val leading = shown.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
            val count = shown.getActualMaximum(Calendar.DAY_OF_MONTH)

            // Convert match fixtures into CalendarEvent.Match
            val matchEvents = fixtureList.map { fixture ->
                CalendarEvent.Match(
                    id = fixture.id,
                    opponent = fixture.opponent,
                    dateLabel = fixture.dateLabel,
                    timeText = fixture.kickoff,
                    location = fixture.venue.ifBlank { "Venue not set" },
                    formation = if (playerList.count { it.inLineup } == 11) "Starting XI selected" else null
                )
            }

            // Generate training session events
            val trainingEvents = listOf(
                CalendarEvent.Training(
                    id = "training-1",
                    title = "Tactical & Set Piece Drill",
                    timeRange = "18:00 - 19:30",
                    location = team.homeGround.ifBlank { "Main Pitch" }
                ),
                CalendarEvent.Training(
                    id = "training-2",
                    title = "Recovery & Conditioning",
                    timeRange = "09:00 - 10:30",
                    location = team.homeGround.ifBlank { "Training Ground" }
                )
            )

            val allEvents: List<CalendarEvent> = matchEvents + trainingEvents

            // Build month grid with match/training markers
            val days = buildList {
                repeat(leading) { add(CalendarDay(0, "", false, null)) }
                for (day in 1..count) {
                    val hasMatch = fixtureList.any { fixtureDateParts(it.dateLabel)?.let { p -> p.first == day && p.second == month } == true }
                    val dayOfWeek = Calendar.getInstance().apply {
                        set(year, month, day)
                    }.get(Calendar.DAY_OF_WEEK)
                    val hasTraining = dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.THURSDAY

                    val marker = when {
                        hasMatch -> DayMarker.MATCH
                        hasTraining -> DayMarker.TRAINING
                        else -> null
                    }

                    add(CalendarDay(day, "", true, marker))
                }
            }

            return CalendarUiState(
                monthLabel = monthLabel,
                days = days,
                selectedDate = if (calendarOffset == 0) Calendar.getInstance().get(Calendar.DAY_OF_MONTH) else 1,
                events = allEvents
            )
        }

    private fun fixtureDateParts(label: String): Pair<Int, Int>? {
        val parsed = listOf("MMM dd", "MMM d").firstNotNullOfOrNull { pattern ->
            runCatching { SimpleDateFormat(pattern, Locale.US).apply { isLenient = false }.parse(label) }.getOrNull()
        } ?: return null
        return Calendar.getInstance().apply { time = parsed }.let { it.get(Calendar.DAY_OF_MONTH) to it.get(Calendar.MONTH) }
    }

    fun addPlayer(player: SquadPlayer) {
        viewModelScope.launch {
            runCatching {
                api.createPlayer(player.fullName, player.position, null)
                api.loadSeason()
            }.onSuccess { snapshot ->
                playerList.clear(); playerList.addAll(snapshot.players)
                fixtureList.clear(); fixtureList.addAll(snapshot.fixtures)
                coach = snapshot.coach
                team = snapshot.team
                rebuildDerivedActivity()
                notify("${player.fullName} added to the roster")
                back()
            }.onFailure { notify(it.message ?: "Could not add player") }
        }
    }

    fun attendanceState(fixtureId: String): AttendanceUiState {
        val match = fixture(fixtureId)
        val statuses = attendanceCache[fixtureId] ?: emptyMap()
        val roster = playerList.map { player ->
            val raw = statuses[player.id] ?: "NotRecorded"
            val status = when (raw) {
                "Present" -> RosterStatus.ATTENDING
                "Absent", "AbsentNoExcuse" -> RosterStatus.ABSENT
                else -> RosterStatus.NO_REPLY
            }
            RosterEntry(
                id = player.id,
                name = player.name,
                subtitle = player.position,
                statusBadge = if (raw == "AbsentNoExcuse") "NO EXCUSE" else null,
                status = status,
                action = if (status != RosterStatus.ATTENDING) RosterRowAction.MARK_PRESENT else RosterRowAction.NONE
            )
        }
        return AttendanceUiState(
            dateTimeLabel = listOfNotNull(match?.dateLabel, match?.kickoff, match?.venue).filter { it.isNotBlank() }.joinToString(" · ").uppercase(),
            eventTitle = match?.let { "Match vs. ${it.opponent}" } ?: "Match attendance",
            squadSize = roster.size,
            attending = roster.count { it.status == RosterStatus.ATTENDING },
            absentCount = roster.count { it.status == RosterStatus.ABSENT },
            noReplyCount = roster.count { it.status == RosterStatus.NO_REPLY },
            roster = roster
        )
    }

    fun loadMatchAttendance(fixtureId: String) {
        viewModelScope.launch {
            runCatching { api.getAttendance(fixtureId) }
                .onSuccess { records ->
                    attendanceCache[fixtureId] = records
                        .filter { it.status != "NotRecorded" }
                        .associate { it.playerId to it.status }
                }
                .onFailure { notify(it.message ?: "Could not load attendance") }
        }
    }

    fun setMatchAttendance(fixtureId: String, playerId: String, status: String) {
        val updated = (attendanceCache[fixtureId] ?: emptyMap()) + (playerId to status)
        attendanceCache[fixtureId] = updated
        viewModelScope.launch {
            runCatching {
                api.saveAttendance(fixtureId, updated.map { (pid, st) -> AttendanceRecord(pid, st) })
            }.onFailure { notify(it.message ?: "Could not save attendance") }
        }
        notify("${player(playerId)?.name ?: "Player"} marked ${status.lowercase()}")
    }

    fun markAllPresent(fixtureId: String) {
        val toMark = attendanceState(fixtureId).roster
            .filter { it.action == RosterRowAction.MARK_PRESENT }
            .map { it.id }
        if (toMark.isEmpty()) return
        val updated = (attendanceCache[fixtureId] ?: emptyMap()) + toMark.associateWith { "Present" }
        attendanceCache[fixtureId] = updated
        viewModelScope.launch {
            runCatching {
                api.saveAttendance(fixtureId, updated.map { (pid, st) -> AttendanceRecord(pid, st) })
            }.onFailure { notify(it.message ?: "Could not save attendance") }
        }
        notify("All no-replies marked present")
    }

    fun markAttendancePresent(playerId: String) {
        setAvailability(playerId, AvailabilityStatus.Fit)
    }

    fun markAllAttendancePresent() {
        playerList.indices.forEach { i ->
            if (playerList[i].status == AvailabilityStatus.Doubtful) playerList[i] = playerList[i].copy(status = AvailabilityStatus.Fit)
        }
        notify("All no-replies marked attending")
    }

    fun matchDetailsState(fixtureId: String): MatchDetailsUiState {
        val match = fixture(fixtureId) ?: return MatchDetailsUiState(
            competitionTag = "Match", title = "Match", dateLabel = "—", timeLabel = "—", venueName = "—", venueDetail = "",
            inCount = 0, outCount = 0, tbdCount = 0, squadSize = 0, formation = "No lineup selected", startingXi = emptyList(), substitutes = emptyList()
        )
        val starters = playerList.filter { it.inLineup }.take(11)
        val subs = playerList.filter { !it.inLineup }.take(7)
        val spots = listOf(
            .50f to .90f, .15f to .70f, .38f to .70f, .62f to .70f, .85f to .70f,
            .25f to .45f, .50f to .42f, .75f to .45f, .20f to .16f, .50f to .12f, .80f to .16f
        )
        return MatchDetailsUiState(
            competitionTag = match.competition.ifBlank { "Match" },
            title = "Match vs. ${match.opponent}",
            dateLabel = match.dateLabel,
            timeLabel = match.kickoff,
            venueName = match.venue.ifBlank { "Venue not set" },
            venueDetail = "",
            inCount = playerList.count { it.status == AvailabilityStatus.Fit },
            outCount = playerList.count { it.status == AvailabilityStatus.Out },
            tbdCount = playerList.count { it.status == AvailabilityStatus.Doubtful },
            squadSize = playerList.size,
            formation = if (starters.size == 11) "Starting XI" else "${starters.size}/11 selected",
            startingXi = starters.mapIndexed { index, player ->
                val spot = spots[index]
                LineupPlayer(if (player.number > 0) player.number else index + 1, player.name, spot.first, spot.second, index == 0)
            },
            substitutes = subs.mapIndexed { index, player -> SubPlayer(if (player.number > 0) player.number else index + 12, player.name, player.position) }
        )
    }

    // ---------------------------------------------------- squad / lineup updates

    fun loadMatchSquad(matchId: String) {
        viewModelScope.launch {
            runCatching {
                api.getSquad(matchId)
            }.onSuccess { squad ->
                val selections = squad.associate {
                    it.playerId to it.selection
                }.toMutableMap()
                matchSquadSelections[matchId] = selections
            }.onFailure {
                notify(it.message ?: "Could not load lineup")
            }
        }
    }

    fun lineupSelection(
        matchId: String,
        playerId: String
    ): String {
        return matchSquadSelections[matchId]
            ?.get(playerId)
            ?: "NotSelected"
    }

    fun startingCount(matchId: String): Int {
        return matchSquadSelections[matchId]
            ?.count { it.value == "Starting" }
            ?: 0
    }

    fun substituteCount(matchId: String): Int {
        return matchSquadSelections[matchId]
            ?.count { it.value == "Substitute" }
            ?: 0
    }

    fun toggleLineupSelection(
        matchId: String,
        playerId: String
    ) {
        val current = lineupSelection(matchId, playerId)
        val startingCount = startingCount(matchId)

        val next = when (current) {
            "NotSelected" -> {
                if (startingCount >= 11) {
                    notify("Starting XI is full")
                    return
                }
                "Starting"
            }
            "Starting" -> "Substitute"
            "Substitute" -> "NotSelected"
            else -> "NotSelected"
        }

        val selections = matchSquadSelections.getOrPut(matchId) { mutableMapOf() }
        selections[playerId] = next
        matchSquadSelections[matchId] = selections.toMutableMap()
    }

    fun clearMatchLineup(matchId: String) {
        matchSquadSelections[matchId] = mutableMapOf()
        notify("Lineup cleared")
    }

    fun saveMatchLineup(matchId: String) {
        val selections = matchSquadSelections[matchId] ?: mutableMapOf()
        val starting = selections.count { it.value == "Starting" }

        if (starting != 11) {
            notify("Select exactly 11 starting players")
            return
        }

        viewModelScope.launch {
            runCatching {
                val squad = playerList.map { player ->
                    SquadUpdate(
                        playerId = player.id,
                        status = when (player.status) {
                            AvailabilityStatus.Fit -> "Available"
                            AvailabilityStatus.Doubtful -> "Available"
                            AvailabilityStatus.Out -> "Available"
                        },
                        selection = selections[player.id] ?: "NotSelected"
                    )
                }

                api.saveSquad(
                    matchId = matchId,
                    players = squad
                )
            }.onSuccess {
                notify("Lineup saved")
                back()
            }.onFailure {
                notify(it.message ?: "Could not save lineup")
            }
        }
    }

    // -------------------------------------------------------------- actions

    fun setAvailability(playerId: String, status: AvailabilityStatus) {
        updatePlayer(playerId) { it.copy(status = status) }
        val name = player(playerId)?.name ?: "Player"
        notify("$name marked ${status.label}")
    }

    fun toggleLineup(playerId: String) {
        val target = player(playerId) ?: return
        if (!target.inLineup && lineupCount >= MAX_LINEUP) {
            notify("Starting XI is full — remove a player first")
            return
        }
        if (!target.inLineup && target.status == AvailabilityStatus.Out) {
            notify("${target.name} is marked Out")
            return
        }
        updatePlayer(playerId) { it.copy(inLineup = !it.inLineup) }
    }

    fun markAllFit() {
        for (i in playerList.indices) {
            playerList[i] = playerList[i].copy(status = AvailabilityStatus.Fit)
        }
        notify("Everyone marked Fit")
    }

    fun clearLineup() {
        for (i in playerList.indices) playerList[i] = playerList[i].copy(inLineup = false)
        notify("Lineup cleared")
    }

    fun saveLineup() {
        val match = nextFixture ?: run { notify("No upcoming fixture"); return }
        viewModelScope.launch {
            runCatching {
                api.saveSquad(
                    match.id,
                    playerList.map {
                        SquadUpdate(
                            playerId = it.id,
                            status = when (it.status) {
                                AvailabilityStatus.Fit -> "Available"
                                AvailabilityStatus.Doubtful -> "Doubtful"
                                AvailabilityStatus.Out -> "Out"
                            },
                            selection = if (it.inLineup) "Starting" else "NotSelected"
                        )
                    }
                )
            }.onSuccess {
                notify("Starting XI saved ($lineupCount/$MAX_LINEUP)")
                back()
            }.onFailure { notify(it.message ?: "Could not save squad") }
        }
    }

    private fun updatePlayer(playerId: String, transform: (Player) -> Player) {
        val index = playerList.indexOfFirst { it.id == playerId }
        if (index >= 0) playerList[index] = transform(playerList[index])
    }

    private fun updateFixture(fixtureId: String, transform: (Fixture) -> Fixture) {
        val index = fixtureList.indexOfFirst { it.id == fixtureId }
        if (index >= 0) fixtureList[index] = transform(fixtureList[index])
    }

    fun saveMatchStats(
        fixtureId: String,
        entries: List<PlayerStatEntry>,
        teamScore: Int,
        opponentScore: Int
    ) {
        val existing = fixture(fixtureId) ?: return
        viewModelScope.launch {
            runCatching {
                api.updateMatchScores(fixtureId, teamScore.coerceAtLeast(0), opponentScore.coerceAtLeast(0))
                api.saveStats(
                    fixtureId,
                    playerList.map { player ->
                        val entry = entries.firstOrNull { it.id == player.id }
                        val line = existing.playerStats[player.id]
                        StatUpdate(
                            playerId = player.id,
                            goals = entry?.goals ?: line?.goals ?: 0,
                            assists = entry?.assists ?: line?.assists ?: 0,
                            cleansheet = opponentScore == 0,
                            minutesPlayed = entry?.minutesPlayed ?: line?.minutes ?: 0,
                            rating = entry?.rating ?: line?.rating ?: 0.0
                        )
                    }
                )
                val refreshed = api.loadSeason()
                refreshed
            }.onSuccess { snapshot ->
                playerList.clear(); playerList.addAll(snapshot.players)
                fixtureList.clear(); fixtureList.addAll(snapshot.fixtures)
                coach = snapshot.coach
                team = snapshot.team
                rebuildDerivedActivity()
                notify("Statistics saved")
                back()
            }.onFailure { notify(it.message ?: "Could not save statistics") }
        }
    }

    private fun buildEvents(lines: Map<String, MatchLine>): List<MatchEvent> {
        val scorers = lines.entries.filter { it.value.goals > 0 }
        if (scorers.isEmpty()) return emptyList()
        var minute = 18
        val events = mutableListOf<MatchEvent>()
        scorers.forEach { (playerId, line) ->
            repeat(line.goals) {
                val name = player(playerId)?.name ?: "Unknown"
                events.add(
                    MatchEvent(
                        minute = minute,
                        type = MatchEventType.Goal,
                        headline = "Goal (${team.name})",
                        detail = name
                    )
                )
                minute = (minute + 17).coerceAtMost(90)
            }
        }
        return events.sortedBy { it.minute }
    }

    fun markNotificationRead(id: String) {
        val index = notificationList.indexOfFirst { it.id == id }
        if (index >= 0) notificationList[index] = notificationList[index].copy(isRead = true)
    }

    fun markAllNotificationsRead() {
        for (i in notificationList.indices) notificationList[i] = notificationList[i].copy(isRead = true)
        notify("All notifications marked as read")
    }

    // Opens deep-linked destination associated with a notification
    fun openNotification(id: String) {
        val notification = notificationList.firstOrNull { it.id == id } ?: return
        markNotificationRead(id)
        when (notification.kind) {
            NotificationKind.Attendance ->
                notification.fixtureId?.let { navigate(Destination.Attendance(it)) }
            NotificationKind.Schedule ->
                notification.fixtureId?.let { navigate(Destination.MatchDetails(it)) }
            NotificationKind.Stats ->
                notification.fixtureId?.let { navigate(Destination.MatchReport(it)) }
            NotificationKind.Fitness -> navigate(Destination.Team)
        }
    }

    fun dismissUpdate(id: String) {
        updateList.removeAll { it.id == id }
        notify("Update dismissed")
    }

    fun setCompetitionFilter(competition: String?) {
        _competitionFilter = competition
        notify(competition?.let { "Filtered to $it" } ?: "Showing all competitions")
    }

    fun exportStats() {
        val count = playedFixtures.size
        notify("Exporting $count matches to PDF…")
    }

    fun setLanguage(option: LanguageOption) {
        coach = coach.copy(languageCode = option.code)
        viewModelScope.launch {
            runCatching { api.updateCoach(coach.name, option.code) }
                .onSuccess { notify("Language set to ${option.nativeLabel}") }
                .onFailure { notify(it.message ?: "Could not update language") }
        }
    }

    fun setMatchReminders(enabled: Boolean) {
        coach = coach.copy(matchReminders = enabled)
    }

    fun setWeeklySummary(enabled: Boolean) {
        coach = coach.copy(weeklySummary = enabled)
    }

    fun updateProfile(name: String, role: String) {
        coach = coach.copy(name = name.ifBlank { coach.name }, role = role.ifBlank { coach.role })
    }

    fun updateTeam(name: String, squad: String, season: String, homeGround: String) {
        team = team.copy(
            name = name.ifBlank { team.name },
            squad = squad.ifBlank { team.squad },
            season = season.ifBlank { team.season },
            homeGround = homeGround.ifBlank { team.homeGround }
        )
    }

    fun saveProfileEdits() {
        viewModelScope.launch {
            runCatching {
                api.updateCoach(coach.name)
                val ageGroup = team.squad.filter { it.isDigit() }.toIntOrNull() ?: 0
                api.updateTeam(team.name, ageGroup)
            }.onSuccess {
                notify("Profile updated")
                back()
            }.onFailure { notify(it.message ?: "Could not update profile") }
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        playerList.clear()
        fixtureList.clear()
        notificationList.clear()
        updateList.clear()
        activityList.clear()
        coach = CoachProfile("", "Coach", emptyList(), "en", false, false)
        team = TeamProfile("", "", "", "", "", "")
        _competitionFilter = null
        selectTab(HomeTab.Home)
        notify("Signed out")
    }

    // -------------------------------------------------------- derived state

    val unreadCount: Int get() = notificationList.count { !it.isRead }

    val homeState: HomeUiState
        get() {
            val next = nextFixture
            val last = lastPlayedFixture
            return HomeUiState(
                coachName = "Coach ${coach.name.substringBefore(' ')}",
                prompt = next
                    ?.let { "Next up: ${it.opponent} on ${it.dateLabel}. Squad availability: ${availabilityMood()}." }
                    ?: "No fixtures scheduled.",
                nextMatch = NextMatch(
                    opponent = next?.let { "vs. ${it.opponent}" } ?: "No upcoming match",
                    date = next?.dateLabel ?: "—",
                    kickoff = next?.let { "${it.kickoff} Kickoff" } ?: "—",
                    venue = next?.venue ?: "—"
                ),
                availability = Availability(
                    fit = playerList.count { it.status == AvailabilityStatus.Fit },
                    doubtful = playerList.count { it.status == AvailabilityStatus.Doubtful },
                    out = playerList.count { it.status == AvailabilityStatus.Out }
                ),
                lastMatch = LastMatch(
                    homeTeam = team.name,
                    homeScore = last?.teamScore ?: 0,
                    awayTeam = last?.opponent ?: "—",
                    awayScore = last?.opponentScore ?: 0,
                    status = if (last != null) "FT" else "—",
                    stats = listOf(
                        "Possession" to "${last?.possession ?: 0}%",
                        "Shots on Target" to "${last?.shotsOnTarget ?: 0}"
                    )
                ),
                updates = updateList.map {
                    TeamUpdate(
                        title = it.title,
                        timestamp = it.timestamp,
                        body = it.body,
                        tone = if (it.kind == UpdateKind.Medical) UpdateTone.Medical else UpdateTone.Notice
                    )
                },
                unreadNotifications = unreadCount > 0
            )
        }

    private fun availabilityMood(): String {
        val out = playerList.count { it.status != AvailabilityStatus.Fit }
        return if (out <= 2) "healthy" else "thin"
    }

    val statsState: StatsUiState
        get() {
            val played = playedFixtures
            val matches = played.size.coerceAtLeast(1)
            val wins = played.count { it.teamScore > it.opponentScore }
            val cleanSheets = played.count { it.opponentScore == 0 }
            val goals = played.sumOf { it.teamScore }
            val possession = if (played.isEmpty()) 0 else played.sumOf { it.possession } / matches
            val winRate = (wins * 100f / matches).roundToInt()
            val goalsPerMatch = goals.toFloat() / matches

            return StatsUiState(
                squadName = team.squad,
                season = competitionFilter?.let { "${team.season} · $it" } ?: team.season,
                summary = listOf(
                    SummaryStat(
                        label = "Win Rate",
                        value = "$winRate%",
                        delta = if (winRate >= 50) "↑" else null,
                        progress = winRate / 100f,
                        barColor = FormUpColors.Primary,
                        highlighted = true
                    ),
                    SummaryStat(
                        label = "Goals / Match",
                        value = String.format("%.1f", goalsPerMatch),
                        progress = (goalsPerMatch / 5f).coerceIn(0f, 1f),
                        barColor = FormUpColors.Amber
                    ),
                    SummaryStat(
                        label = "Clean Sheets",
                        value = "$cleanSheets / ${played.size} Games",
                        progress = cleanSheets.toFloat() / matches,
                        barColor = FormUpColors.Primary
                    ),
                    SummaryStat(
                        label = "Possession Avg",
                        value = "$possession%",
                        progress = possession / 100f,
                        barColor = FormUpColors.Primary
                    )
                ),
                goalsLeaders = leaders { goalsOf(it) to "Goals" },
                assistsLeaders = leaders { assistsOf(it) to "Assists" },
                lastMatch = lastPlayedFixture.let { fixture ->
                    LastMatchAnalysis(
                        opponent = fixture?.opponent ?: "—",
                        resultText = fixture?.resultText ?: "—",
                        rows = listOf(
                            MatchStatRow(
                                label = "Possession",
                                valueText = "${fixture?.possession ?: 0}% - ${100 - (fixture?.possession ?: 0)}%",
                                progress = (fixture?.possession ?: 0) / 100f,
                                barColor = FormUpColors.Primary
                            ),
                            MatchStatRow(
                                label = "Pass Accuracy",
                                valueText = "${fixture?.passAccuracy ?: 0}%",
                                progress = (fixture?.passAccuracy ?: 0) / 100f,
                                barColor = FormUpColors.Amber
                            ),
                            MatchStatRow(
                                label = "Shots on Target",
                                valueText = "${fixture?.shotsOnTarget ?: 0} / ${fixture?.shots ?: 0}",
                                progress = fixture?.let {
                                    if (it.shots == 0) 0f else it.shotsOnTarget.toFloat() / it.shots
                                } ?: 0f,
                                barColor = FormUpColors.Primary
                            )
                        )
                    )
                }
            )
        }

    private fun leaders(selector: (Player) -> Pair<Int, String>): List<TopPerformer> =
        playerList
            .map { it to selector(it) }
            .sortedByDescending { it.second.first }
            .take(3)
            .mapIndexed { index, (player, stat) ->
                TopPerformer(
                    number = player.number,
                    name = player.name,
                    position = player.position,
                    matches = matchesOf(player),
                    statValue = "${stat.first} ${stat.second}",
                    badgeColor = if (index == 0) FormUpColors.Amber else FormUpColors.NavIndicator,
                    badgeTextColor = if (index == 0) FormUpColors.Surface else FormUpColors.TextPrimary
                )
            }

    val profileState: ProfileUiState
        get() {
            val played = playedFixtures
            val teamGoals = played.sumOf { it.teamScore }
            val assists = playerList.sumOf { assistsOf(it) }
            val minutes = played.size * 90
            return ProfileUiState(
                name = coach.name,
                role = coach.role,
                badges = coach.badges,
                seasonStats = listOf(
                    SeasonStat("Goals", teamGoals.toString(), Icons.Filled.SportsScore),
                    SeasonStat("Assists", assists.toString(), Icons.Filled.Handshake),
                    SeasonStat("Matches", played.size.toString(), Icons.Filled.EventAvailable),
                    SeasonStat("Minutes", formatNumber(minutes), Icons.Filled.Timer)
                ),
                recentActivity = activityList.take(4).map {
                    ActivityItem(
                        title = it.title,
                        subtitle = it.subtitle,
                        date = it.date,
                        tone = if (it.isTraining) ActivityTone.Training else ActivityTone.Match
                    )
                },
                teamInfo = TeamInfo(
                    teamName = team.name,
                    squad = team.squad,
                    season = team.season,
                    homeGround = team.homeGround,
                    clubCode = team.clubCode
                ),
                language = SupportedLanguages.firstOrNull { it.code == coach.languageCode }
                    ?: SupportedLanguages.first(),
                matchReminders = coach.matchReminders,
                weeklySummary = coach.weeklySummary,
                unreadNotifications = unreadCount > 0
            )
        }

    private fun formatNumber(value: Int): String =
        if (value >= 1000) "${value / 1000},${(value % 1000).toString().padStart(3, '0')}" else value.toString()

    val notificationsState: NotificationsUiState
        get() = NotificationsUiState(
            items = notificationList.map { notification ->
                NotificationItem(
                    id = notification.id,
                    icon = when (notification.kind) {
                        NotificationKind.Attendance -> Icons.Filled.NotificationsActive
                        NotificationKind.Schedule -> Icons.Filled.CalendarMonth
                        NotificationKind.Fitness -> Icons.Filled.MonitorHeart
                        NotificationKind.Stats -> Icons.Filled.QueryStats
                    },
                    iconBackground = when (notification.kind) {
                        NotificationKind.Schedule -> FormUpColors.AmberTint
                        else -> FormUpColors.PrimaryTint
                    },
                    iconTint = when (notification.kind) {
                        NotificationKind.Schedule -> FormUpColors.AmberIcon
                        NotificationKind.Attendance -> FormUpColors.Primary
                        else -> FormUpColors.TextSecondary
                    },
                    title = notification.title,
                    timestamp = notification.timestamp,
                    body = notification.body,
                    group = if (notification.isRead) NotificationGroup.Earlier else NotificationGroup.New,
                    actionLabel = notification.actionLabel
                )
            }
        )

    val unreadNotificationIds: Set<String>
        get() = notificationList.filter { !it.isRead }.map { it.id }.toSet()

    fun statsInputState(fixtureId: String): StatsInputUiState {
        val match = fixture(fixtureId) ?: fixtureList.firstOrNull() ?: return StatsInputUiState(
            fixtureId = fixtureId,
            resultBadge = "NO MATCHES",
            matchDate = "—",
            opponent = "—",
            competition = "—",
            homeLabel = team.name.ifBlank { "TEAM" }.uppercase(),
            awayLabel = "—",
            homeScore = 0,
            awayScore = 0,
            availableFixtures = emptyList(),
            players = emptyList()
        )
        return StatsInputUiState(
            fixtureId = match.id,
            resultBadge = if (match.played) "FT ${match.scoreLine} (${match.resultLetter})" else "NOT PLAYED",
            matchDate = "${match.dateLabel}, ${team.season.take(4)}",
            opponent = "vs. ${match.opponent}",
            competition = "${team.squad} · ${match.competition}",
            homeLabel = team.name.uppercase(),
            awayLabel = match.opponent.uppercase(),
            homeScore = match.teamScore,
            awayScore = match.opponentScore,
            availableFixtures = fixtureList.map {
                FixtureOption(it.id, "${it.opponent} • ${it.dateLabel}")
            },
            players = playerList.map { player ->
                val line = match.playerStats[player.id]
                PlayerStatEntry(
                    id = player.id,
                    number = player.number,
                    name = player.name,
                    position = player.position,
                    goals = line?.goals ?: 0,
                    assists = line?.assists ?: 0,
                    minutesPlayed = line?.minutes ?: 0,
                    rating = line?.rating ?: 0.0,
                    badgeColor = if (player.inLineup) FormUpColors.Primary else FormUpColors.NavIndicator,
                    badgeTextColor = if (player.inLineup) FormUpColors.Surface else FormUpColors.TextPrimary
                )
            }
        )
    }

    fun matchReportState(fixtureId: String): MatchReportUiState {
        val match = fixture(fixtureId) ?: fixtureList.firstOrNull() ?: return MatchReportUiState(
            statusLine = "NO MATCHES",
            awayTeam = TeamScoreInfo("—", "—", 0),
            homeTeam = TeamScoreInfo(team.name.take(3).uppercase(), team.name.ifBlank { "Team" }, 0, true),
            overview = "No match data is available yet.",
            tacticalNotes = emptyList(),
            timeline = emptyList(),
            teamStats = emptyList(),
            myPerformance = PerformanceSummary("—", "NO STATS", "0'", "0", "—", "—", "—"),
            ratings = emptyList()
        )
        val lines = match.playerStats
        val ratings = lines.entries
            .mapNotNull { (playerId, line) -> player(playerId)?.let { it to line } }
            .sortedByDescending { ratingValue(it.second) }
            .mapIndexed { index, (player, line) ->
                PlayerRating(
                    rank = player.number,
                    name = player.name,
                    note = buildString {
                        append(player.position.take(3).uppercase())
                        if (line.goals > 0) append(" · ${line.goals} Goal${if (line.goals > 1) "s" else ""}")
                        if (line.assists > 0) append(" · ${line.assists} Assist${if (line.assists > 1) "s" else ""}")
                    },
                    rating = String.format("%.1f", ratingValue(line)),
                    isStandout = index == 0
                )
            }

        val best = lines.entries.maxByOrNull { ratingValue(it.value) }

        return MatchReportUiState(
            statusLine = "${if (match.played) "FULL TIME" else "SCHEDULED"} · ${match.dateLabel.uppercase()} · ${match.venue.uppercase()}",
            awayTeam = TeamScoreInfo(
                abbreviation = match.opponent.take(3).uppercase(),
                name = match.opponent,
                score = match.opponentScore
            ),
            homeTeam = TeamScoreInfo(
                abbreviation = team.name.take(3).uppercase(),
                name = team.name,
                score = match.teamScore,
                isFormUp = true
            ),
            overview = overviewFor(match),
            tacticalNotes = match.tacticalNotes.mapIndexed { index, note ->
                TacticalNote(note, positive = index == 0)
            },
            timeline = match.events.map { event ->
                TimelineEvent(
                    minute = "${event.minute}'",
                    type = when (event.type) {
                        MatchEventType.Goal -> TimelineEventType.Goal
                        MatchEventType.Card -> TimelineEventType.Card
                        MatchEventType.Substitution -> TimelineEventType.Substitution
                    },
                    headline = event.headline,
                    detail = event.detail
                )
            },
            teamStats = listOf(
                DualStat(
                    "Possession",
                    (100 - match.possession).toFloat(),
                    match.possession.toFloat(),
                    "${100 - match.possession}%",
                    "${match.possession}%"
                ),
                DualStat("Total Shots", match.opponentShots.toFloat(), match.shots.toFloat(), "${match.opponentShots}", "${match.shots}"),
                DualStat("Shots on Target", match.opponentShotsOnTarget.toFloat(), match.shotsOnTarget.toFloat(), "${match.opponentShotsOnTarget}", "${match.shotsOnTarget}"),
                DualStat("Pass Accuracy", match.opponentPassAccuracy.toFloat(), match.passAccuracy.toFloat(), "${match.opponentPassAccuracy}%", "${match.passAccuracy}%")
            ),
            myPerformance = PerformanceSummary(
                rating = best?.let { String.format("%.1f", ratingValue(it.value)) } ?: "—",
                ratingLabel = best?.let { player(it.key)?.name?.uppercase() } ?: "NO STATS LOGGED",
                minutes = "${best?.value?.minutes ?: 0}'",
                goals = "${best?.value?.goals ?: 0}",
                passAccuracy = "${match.passAccuracy}%",
                distance = "9.2km",
                topSpeed = "28.4 km/h"
            ),
            ratings = ratings
        )
    }

    private fun ratingValue(line: MatchLine): Double =
        (6.4 + line.goals * 1.2 + line.assists * 0.7 + line.minutes / 300.0).coerceAtMost(10.0)

    private fun overviewFor(fixture: Fixture): String = when {
        !fixture.played -> "This fixture hasn't been played yet. Log the result from Stats Input once it's done."
        fixture.teamScore > fixture.opponentScore ->
            "${team.name} controlled ${fixture.possession}% of the ball and converted ${fixture.shotsOnTarget} of ${fixture.shots} shots on target to beat ${fixture.opponent}."
        fixture.teamScore < fixture.opponentScore ->
            "A tough afternoon against ${fixture.opponent}: ${fixture.shotsOnTarget} shots on target wasn't enough despite ${fixture.possession}% possession."
        else ->
            "An even contest with ${fixture.opponent} — ${fixture.possession}% possession and ${fixture.shotsOnTarget} shots on target ended level."
    }

    private fun resultWord(fixture: Fixture): String = when (fixture.resultLetter) {
        "W" -> "Won"
        "L" -> "Lost"
        else -> "Drew"
    }

    companion object {
        const val MAX_LINEUP = 11
    }
}
