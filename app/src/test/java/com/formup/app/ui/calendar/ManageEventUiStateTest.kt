package com.formup.app.ui.calendar
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.TimeZone

class ManageEventUiStateTest {

    private lateinit var originalTimeZone: TimeZone
    @Before
    fun setUpTest(){
        originalTimeZone = TimeZone.getDefault() //getting the default original timezone to use for testing
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")) //setting it to utc
    }

    //restores machines local timezone so it doesnt interfere with future tests
    @After
    fun tearDown() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun toIsoUtc_ReturnsCorrectUtcStringTest(){
        val dateAsString = "10/14/2026"
        val timeAsString = "18:00"

        val result = toIsoUtc(dateAsString, timeAsString)

        assertEquals("2026-10-14T18:00:00Z", result)

    }

    @Test
    fun formatDateLabelTest(){
        val dateAsString = "10/14/2026"
        val result = formatDateLabel(dateAsString)
        assertEquals("Wednesday, Oct 14", result)
    }

}