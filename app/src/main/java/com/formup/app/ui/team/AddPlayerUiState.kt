package com.formup.app.ui.team

data class AddPlayerFormState(
    val fullName: String = "",
    val dateOfBirth: String = "",
    val position: String = "",
    val jerseyNumber: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val guardianName: String = "",
    val status: PlayerStatus = PlayerStatus.FIT,
    val nameError: String? = null,
    val positionError: String? = null
)

val PositionOptions = listOf("Goalkeeper", "Defender", "Midfielder", "Forward")