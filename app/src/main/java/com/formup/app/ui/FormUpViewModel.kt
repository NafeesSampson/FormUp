package com.formup.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.formup.app.data.ActivityEntry
import com.formup.app.data.AppNotification
import com.formup.app.data.AvailabilityStatus
import com.formup.app.data.CoachProfile
import com.formup.app.data.Fixture
import com.formup.app.data.MatchEvent
import com.formup.app.data.MatchEventType
import com.formup.app.data.MatchLine
import com.formup.app.data.NotificationKind
import com.formup.app.data.Player
import com.formup.app.data.SeedData
import com.formup.app.data.TeamProfile
import com.formup.app.data.TeamUpdateItem
import com.formup.app.data.UpdateKind
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
import com.formup.app.ui.theme.FormUpColors
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Timer
import kotlin.math.roundToInt


class FormUpViewModel : ViewModel() {

    // ---------------------------------------------------------------- state

    private val playerList = mutableStateListOf<Player>().apply { addAll(SeedData.players) }
    private val fixtureList = mutableStateListOf<Fixture>().apply { addAll(SeedData.fixtures) }
    private val notificationList = mutableStateListOf<AppNotification>().apply { addAll(SeedData.notifications) }
    private val updateList = mutableStateListOf<TeamUpdateItem>().apply { addAll(SeedData.updates) }
    private val activityList = mutableStateListOf<ActivityEntry>().apply { addAll(SeedData.activity) }

    var coach by mutableStateOf(SeedData.coach)
        private set
    var team by mutableStateOf(SeedData.team)
        private set

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
        player.baseGoals + fixtureList.sumOf { it.playerStats[player.id]?.goals ?: 0 }

    fun assistsOf(player: Player): Int =
        player.baseAssists + fixtureList.sumOf { it.playerStats[player.id]?.assists ?: 0 }

    fun matchesOf(player: Player): Int =
        player.baseMatches + fixtureList.count { it.played && it.playerStats.containsKey(player.id) }

    fun minutesOf(player: Player): Int =
        player.baseMinutes + fixtureList.sumOf { it.playerStats[player.id]?.minutes ?: 0 }

    val lineupCount: Int get() = playerList.count { it.inLineup }

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
        notify("Starting XI saved ($lineupCount/$MAX_LINEUP)")
        back()
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
        val lines = entries
            .filter { it.goals > 0 || it.assists > 0 }
            .associate { entry ->
                entry.id to MatchLine(
                    goals = entry.goals,
                    assists = entry.assists,
                    minutes = existing.playerStats[entry.id]?.minutes ?: 90
                )
            }


         val events = buildEvents(lines)

         updateFixture(fixtureId) {
             it.copy(
                 played = true,
                 teamScore = teamScore.coerceAtLeast(0),
                 opponentScore = opponentScore.coerceAtLeast(0),
                 playerStats = lines,
                 events = events
             )
         }

        val saved = fixture(fixtureId) ?: return
        activityList.add(
            0,
            ActivityEntry(
                id = "a-${saved.id}-${System.currentTimeMillis()}",
                title = "Match vs. ${saved.opponent}",
                subtitle = "${resultWord(saved)} ${saved.scoreLine} · ${lines.values.sumOf { it.goals }} logged goals",
                date = saved.dateLabel.uppercase(),
                isTraining = false
            )
        )
        notificationList.add(
            0,
            AppNotification(
                id = "report-${saved.id}-${System.currentTimeMillis()}",
                kind = NotificationKind.Stats,
                title = "Match Report Ready",
                timestamp = "Just now",
                body = "Stats saved for ${saved.opponent}. Tap to open the full report.",
                actionLabel = "View Report",
                fixtureId = saved.id
            )
        )
        notify("Statistics saved")
        back()
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

    /** Opens whatever a notification points at, marking it read on the way. */
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
        notify("Language set to ${option.nativeLabel}")
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
        notify("Profile updated")
        back()
    }

    fun signOut() {
        playerList.clear(); playerList.addAll(SeedData.players)
        fixtureList.clear(); fixtureList.addAll(SeedData.fixtures)
        notificationList.clear(); notificationList.addAll(SeedData.notifications)
        updateList.clear(); updateList.addAll(SeedData.updates)
        activityList.clear(); activityList.addAll(SeedData.activity)
        coach = SeedData.coach
        team = SeedData.team
        _competitionFilter = null
        selectTab(HomeTab.Home)
        notify("Signed out — demo data reset")
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
                    ?.let { "Next up: ${it.opponent} on ${it.dateLabel}. Squad list looks ${availabilityMood()}." }
                    ?: "No fixtures scheduled — add one from the calendar.",
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
        val match = fixture(fixtureId) ?: fixtureList.first()
        return StatsInputUiState(
            resultBadge = if (match.played) "FT ${match.scoreLine} (${match.resultLetter})" else "NOT PLAYED",
            matchDate = "${match.dateLabel}, ${team.season.take(4)}",
            opponent = "vs. ${match.opponent}",
            competition = "${team.squad} · ${match.competition}",
            homeLabel = team.name.uppercase(),
            awayLabel = match.opponent.uppercase(),
            homeScore = match.teamScore,
            awayScore = match.opponentScore,
            players = playerList.map { player ->
                val line = match.playerStats[player.id]
                PlayerStatEntry(
                    id = player.id,
                    number = player.number,
                    name = player.name,
                    position = player.position,
                    goals = line?.goals ?: 0,
                    assists = line?.assists ?: 0,
                    badgeColor = if (player.inLineup) FormUpColors.Primary else FormUpColors.NavIndicator,
                    badgeTextColor = if (player.inLineup) FormUpColors.Surface else FormUpColors.TextPrimary
                )
            }
        )
    }

    fun matchReportState(fixtureId: String): MatchReportUiState {
        val match = fixture(fixtureId) ?: fixtureList.first()
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