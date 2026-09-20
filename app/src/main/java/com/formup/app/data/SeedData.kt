package com.formup.app.data


object SeedData {

    val coach = CoachProfile(
        name = "Thomas Newman",
        role = "Coach",
        badges = listOf("Elite", "Born: 2005"),
        languageCode = "en",
        matchReminders = true,
        weeklySummary = false
    )

    val team = TeamProfile(
        name = "FormUp FC",
        squad = "U18 Varsity Squad",
        season = "2026 Season",
        homeGround = "Riverside Stadium, Field A",
        clubCode = "FORMUP-9X2J",
        inviteLink = "formup.app/join/9x2j"
    )

    val players = listOf(
        Player("de-gea", 1, "David De Gea", "Goalkeeper", baseGoals = 0, baseAssists = 0, baseMatches = 12, baseMinutes = 1080, inLineup = true),
        Player("van-dijk", 4, "Virgil van Dijk", "Defender", baseGoals = 2, baseAssists = 1, baseMatches = 12, baseMinutes = 1070, inLineup = true),
        Player("rice", 6, "Declan Rice", "Midfielder", baseGoals = 1, baseAssists = 5, baseMatches = 12, baseMinutes = 990, inLineup = true),
        Player("de-bruyne", 8, "Kevin De Bruyne", "Midfielder", baseGoals = 4, baseAssists = 10, baseMatches = 12, baseMinutes = 1010, inLineup = true),
        Player("rashford", 10, "Marcus Rashford", "Forward", baseGoals = 14, baseAssists = 4, baseMatches = 12, baseMinutes = 1005, inLineup = true),
        Player("haaland", 9, "Erling Haaland", "Striker", baseGoals = 11, baseAssists = 2, baseMatches = 11, baseMinutes = 930, inLineup = true),
        Player("saka", 7, "Bukayo Saka", "Winger", baseGoals = 8, baseAssists = 6, baseMatches = 12, baseMinutes = 960, inLineup = true),
        Player("grealish", 11, "Jack Grealish", "Winger", status = AvailabilityStatus.Doubtful, baseGoals = 3, baseAssists = 7, baseMatches = 10, baseMinutes = 720),
        Player("smith", 19, "J. Smith", "Forward", baseGoals = 6, baseAssists = 3, baseMatches = 11, baseMinutes = 810),
        Player("johnson", 5, "M. Johnson", "Defender", baseGoals = 0, baseAssists = 1, baseMatches = 12, baseMinutes = 1040, inLineup = true),
        Player("lee", 14, "K. Lee", "Midfielder", status = AvailabilityStatus.Out, baseGoals = 1, baseAssists = 2, baseMatches = 8, baseMinutes = 520),
        Player("chen", 17, "A. Chen", "Forward", baseGoals = 5, baseAssists = 2, baseMatches = 10, baseMinutes = 690),
        Player("davis", 21, "R. Davis", "Defender", baseGoals = 0, baseAssists = 0, baseMatches = 9, baseMinutes = 610, inLineup = true),
        Player("okafor", 23, "T. Okafor", "Midfielder", status = AvailabilityStatus.Doubtful, baseGoals = 2, baseAssists = 3, baseMatches = 9, baseMinutes = 580),
        Player("mendes", 3, "L. Mendes", "Defender", baseGoals = 1, baseAssists = 0, baseMatches = 11, baseMinutes = 940, inLineup = true),
        Player("park", 12, "S. Park", "Goalkeeper", status = AvailabilityStatus.Out, baseGoals = 0, baseAssists = 0, baseMatches = 3, baseMinutes = 270)
    )

    val fixtures = listOf(
        Fixture(
            id = "rovers-oct07",
            opponent = "Valley FC",
            competition = "League Match",
            dateLabel = "Oct 07",
            kickoff = "15:00",
            venue = "Valley Park",
            isHome = false,
            played = true,
            teamScore = 1,
            opponentScore = 1,
            possession = 51,
            passAccuracy = 79,
            opponentPassAccuracy = 80,
            shots = 9,
            shotsOnTarget = 4,
            opponentShots = 11,
            opponentShotsOnTarget = 5,
            playerStats = mapOf("haaland" to MatchLine(goals = 1, minutes = 90)),
            events = listOf(
                MatchEvent(37, MatchEventType.Goal, "Goal (FormUp FC)", "E. Haaland"),
                MatchEvent(71, MatchEventType.Goal, "Goal (Valley FC)", "Set piece header")
            ),
            tacticalNotes = listOf("Dropped too deep after the hour mark and invited pressure.")
        ),
        Fixture(
            id = "rovers-oct14",
            opponent = "Rovers FC",
            competition = "League Match",
            dateLabel = "Oct 14",
            kickoff = "14:00",
            venue = "City Stadium",
            isHome = false,
            played = true,
            teamScore = 2,
            opponentScore = 0,
            possession = 62,
            passAccuracy = 88,
            opponentPassAccuracy = 74,
            shots = 14,
            shotsOnTarget = 8,
            opponentShots = 6,
            opponentShotsOnTarget = 2,
            playerStats = mapOf(
                "smith" to MatchLine(goals = 1, minutes = 85),
                "de-bruyne" to MatchLine(assists = 1, minutes = 90),
                "chen" to MatchLine(goals = 1, minutes = 25)
            ),
            events = listOf(
                MatchEvent(24, MatchEventType.Goal, "Goal (FormUp FC)", "J. Smith (Assist: K. De Bruyne)"),
                MatchEvent(42, MatchEventType.Card, "Yellow Card (Rovers)", "T. Johnson"),
                MatchEvent(65, MatchEventType.Substitution, "Substitution (FormUp)", "In: K. Lee, Out: R. Davis"),
                MatchEvent(78, MatchEventType.Goal, "Goal (FormUp FC)", "A. Chen")
            ),
            tacticalNotes = listOf(
                "Shifted to 4-2-3-1 after 60 mins and neutralized their flank attacks.",
                "High press in the opening 20 mins created early chances."
            )
        ),
        Fixture(
            id = "metro-oct28",
            opponent = "Metro City FC",
            competition = "League Match",
            dateLabel = "Oct 28",
            kickoff = "14:00",
            venue = "Riverside Stadium, Field A",
            isHome = true
        ),
        Fixture(
            id = "eastside-nov04",
            opponent = "Eastside United",
            competition = "Cup Match",
            dateLabel = "Nov 04",
            kickoff = "11:30",
            venue = "Eastside Ground",
            isHome = false
        ),
        Fixture(
            id = "harbour-nov11",
            opponent = "Harbour Athletic",
            competition = "League Match",
            dateLabel = "Nov 11",
            kickoff = "16:00",
            venue = "Riverside Stadium, Field A",
            isHome = true
        )
    )

    val updates = listOf(
        TeamUpdateItem(
            id = "field-change",
            title = "Field Change Notice",
            timestamp = "2h ago",
            body = "Thursday practice moved to Field B due to maintenance on the main pitch.",
            kind = UpdateKind.Notice
        ),
        TeamUpdateItem(
            id = "medical",
            title = "Medical Clearance",
            timestamp = "Yesterday",
            body = "Alex has been cleared by physio to return to light training this week.",
            kind = UpdateKind.Medical
        )
    )

    val activity = listOf(
        ActivityEntry("a-oct14", "Match vs. Rovers FC", "Won 2-0 · 14 shots", "OCT 14", isTraining = false),
        ActivityEntry("a-oct12", "Tactical Training", "Full session completed", "OCT 12", isTraining = true),
        ActivityEntry("a-oct07", "Match vs. Valley FC", "Drew 1-1 away", "OCT 07", isTraining = false)
    )

    val notifications = listOf(
        AppNotification(
            id = "attendance-nudge",
            kind = NotificationKind.Attendance,
            title = "Attendance Nudge",
            timestamp = "2m ago",
            body = "Five players still haven't RSVP'd for the Metro City FC fixture.",
            actionLabel = "Open Attendance",
            fixtureId = "metro-oct28"
        ),
        AppNotification(
            id = "team-update",
            kind = NotificationKind.Schedule,
            title = "Team Update",
            timestamp = "1h ago",
            body = "Match Location Changed: Saturday's match vs. Metro City FC is now at Field 4.",
            actionLabel = "View Match",
            fixtureId = "metro-oct28"
        ),
        AppNotification(
            id = "fitness-alert",
            kind = NotificationKind.Fitness,
            title = "Fitness Alert",
            timestamp = "Yesterday",
            body = "Injury Update: K. Lee is still out. S. Park remains unavailable.",
            isRead = true,
            actionLabel = "Open Squad"
        ),
        AppNotification(
            id = "stats-update",
            kind = NotificationKind.Stats,
            title = "Stats Update",
            timestamp = "Yesterday",
            body = "New Match Report: review the stats from the 2-0 win vs Rovers FC.",
            isRead = true,
            actionLabel = "View Report",
            fixtureId = "rovers-oct14"
        )
    )
}