package com.example.o2assignment

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import androidx.navigation.toRoute
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.o2assignment.home.HOME_ROUTE
import com.example.o2assignment.scratch.ScratchNav
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: NavController

    @Before
    fun setup() {
        composeTestRule.activityRule.scenario.onActivity { activity ->
            // Ak si pridáš property do MainActivity
            navController = activity.navController
        }
    }

    @Test
    fun navHost_verifyStartDestination() {
        composeTestRule.runOnIdle {
            assertEquals(HOME_ROUTE, navController.currentDestination?.route)
        }
    }

    @Test
    fun navHost_clickAddCard_navigatesToScratchScreen() {
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.btn_add_cntDesc)
        ).performClick()

        composeTestRule.onNodeWithStringId(R.string.btn_scratch).performClick()

        composeTestRule.runOnIdle {
            val current = navController.currentBackStackEntry?.toRoute<ScratchNav>()
            assertEquals(ScratchNav(1), current)
        }
    }

    @Test
    fun navHost_scratchAndThenToActivateScreen() {
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.btn_add_cntDesc)
        ).performClick()

        // Step 1: Click button to navigate to Scratch screen
        composeTestRule.onNodeWithStringId(R.string.btn_scratch).performClick()

        // Step 2: Verify navigation to Scratch screen
        composeTestRule.runOnIdle {
            val current = navController.currentBackStackEntry?.toRoute<ScratchNav>()
            assertEquals(ScratchNav(1), current)
        }

        /*// Step 3: Click the "Scratch" button on Scratch screen
        composeTestRule.onNodeWithStringId(R.string.btn_scratch_card).performClick()

        // Step 4: Wait for "Activate" button to appear (after async work)
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule
                .onAllNodesWithText(composeTestRule.activity.getString(R.string.btn_activate))
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Step 5: Click "Activate" button
        composeTestRule.onNodeWithStringId(R.string.btn_activate).performClick()

        // Step 6: Verify navigation to Activate screen
        composeTestRule.runOnIdle {
            val current = navController.currentBackStackEntry?.toRoute<ActivateNav>()
            assertEquals(ActivateNav(1), current)
        }*/
    }


}

fun <A : AppCompatActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithStringId(
    @StringRes id: Int
): SemanticsNodeInteraction = onNodeWithText(activity.getString(id))
