package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SantaliDatabase
import com.example.data.local.UserPreferencesDataStore
import com.example.data.model.SwipeOutcome
import com.example.data.repository.MatrimonyRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Santali Juri", appName)
    }

    @Test
    fun `room schema persists profiles, clan preferences, and chat messages`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, SantaliDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefs = UserPreferencesDataStore(context)
        val repo = MatrimonyRepository(db.santaliDao(), prefs)

        repo.ensureSeeded()

        // 1. Verify User Profile & Community Profiles table
        val currentUser = repo.currentUserFlow.first()
        assertNotNull(currentUser)
        assertEquals("Soren", currentUser?.parsiClan)

        val profiles = repo.communityProfilesFlow.first()
        assertTrue(profiles.isNotEmpty())

        // 2. Verify Clan Preferences table
        repo.updateFilters(
            excludeMyClan = false,
            preferredClans = setOf("Murmu", "Hansda"),
            minAge = 22,
            maxAge = 30,
            selectedState = "Jharkhand",
            selectedEducation = "Engineer",
            verifiedOnly = true
        )
        val storedClanPrefs = db.santaliDao().getClanPreferences()
        assertNotNull(storedClanPrefs)
        assertFalse(storedClanPrefs!!.excludeMyClan)
        assertEquals(listOf("Hansda", "Murmu"), storedClanPrefs.preferredClans)
        assertEquals(22, storedClanPrefs.minAge)
        assertEquals(30, storedClanPrefs.maxAge)
        assertEquals("Jharkhand", storedClanPrefs.selectedState)
        assertTrue(storedClanPrefs.verifiedOnly)

        // 3. Verify Chat Messages table + Match flow
        val incomingLike = profiles.first { it.id == "moni_hembram" }
        val outcome = repo.acceptProfile(incomingLike)
        assertTrue(outcome is SwipeOutcome.Matched)

        repo.sendMessage("moni_hembram", "Johar Moni!")
        val messages = repo.observeMessages("moni_hembram").first()
        assertTrue(messages.any { it.text == "Johar Moni!" && it.senderIsMe })

        db.close()
    }

    @Test
    fun `credential login and id verification persist in room`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, SantaliDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefs = UserPreferencesDataStore(context)
        val repo = MatrimonyRepository(db.santaliDao(), prefs)

        repo.ensureSeeded()

        // Wrong password fails
        val badLogin = repo.loginWithCredentials("+91 98765 43210", "wrongpass")
        assertTrue(badLogin.isFailure)
        assertFalse(repo.preferencesFlow.first().isLoggedIn)

        // Default seeded credentials succeed
        val goodLogin = repo.loginWithCredentials("+91 98765 43210", "santali123")
        assertTrue(goodLogin.isSuccess)
        assertTrue(repo.preferencesFlow.first().isLoggedIn)

        // Newly registered credentials persist and authenticate
        repo.logout()
        assertFalse(repo.preferencesFlow.first().isLoggedIn)
        repo.createOrUpdateCurrentUser(
            fullName = "Baha Tudu",
            age = 24,
            gender = "Female",
            parsiClan = "Tudu",
            location = "Dumka",
            state = "Jharkhand",
            height = "5'4\"",
            educationProfession = "B.Ed • Teacher",
            educationCategory = "Graduate",
            aboutMe = "Excited to connect.",
            interests = listOf("Folk Dance"),
            photoUri = null,
            galleryUris = emptyList(),
            markLoggedIn = true,
            accountIdentifier = "baha@santali.in",
            accountPassword = "mypassword"
        )
        repo.logout()
        assertTrue(repo.loginWithCredentials("baha@santali.in", "mypassword").isSuccess)

        // ID verification activates Blue Tick and stores record
        assertFalse(repo.currentUserFlow.first()?.isVerified ?: true)
        repo.submitIdVerification(
            documentType = "Aadhaar Card",
            frontImageUri = "content://id/front.jpg",
            backImageUri = "content://id/back.jpg",
            selfieUri = "content://id/selfie.jpg"
        )
        assertTrue(repo.currentUserFlow.first()?.isVerified == true)
        val record = repo.verificationRecordFlow.first()
        assertNotNull(record)
        assertEquals("Aadhaar Card", record?.documentType)

        // Upgrade to Premium persists selected plan & visibility boost
        assertFalse(repo.preferencesFlow.first().isPremium)
        repo.activatePremiumPlan("12_months")
        val premiumPrefs = repo.preferencesFlow.first()
        assertTrue(premiumPrefs.isPremium)
        assertEquals("12_months", premiumPrefs.premiumPlanId)
        assertTrue(premiumPrefs.boostProfileVisibility)

        db.close()
    }
}
