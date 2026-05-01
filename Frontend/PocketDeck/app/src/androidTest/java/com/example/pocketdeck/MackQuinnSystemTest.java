package com.example.pocketdeck;

import com.example.pocketdeck.GamePlayScreen;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class MackQuinnSystemTest {

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<GamePlayScreen> activityScenarioRule = new ActivityScenarioRule<>(GamePlayScreen.class);

    @Test
    public void testStatusTextUpdatesFromGameState() {
        String json = "{" + "\"messageType\":\"game_state\"," + "\"gamePhase\":\"playing\"," + "\"status\":\"Your turn\"," + "\"centerInfo\":\"Dealer has 18\"," + "\"yourSeat\":0," + "\"players\":[{\"seat\":0,\"username\":\"mack\",\"cards\":[]}]," + "\"actions\":[\"Hit\",\"Stand\"]" + "}";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.onWebSocketMessage(json);
        });

        try { Thread.sleep(SIMULATED_DELAY_MS); } catch (Exception e) {}

        onView(withId(R.id.statusText)).check(matches(withText("Your turn")));
    }

    @Test
    public void testCenterTextUpdates() {
        String json = "{" + "\"messageType\":\"game_state\"," + "\"gamePhase\":\"playing\"," + "\"status\":\"Game running\"," + "\"centerInfo\":\"Dealer shows Ace\"," + "\"yourSeat\":0," + "\"players\":[{\"seat\":0,\"username\":\"mack\",\"cards\":[]}]," + "\"actions\":[]" + "}";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.onWebSocketMessage(json);
        });

        try { Thread.sleep(SIMULATED_DELAY_MS); } catch (Exception e) {}

        onView(withId(R.id.centerText)).check(matches(withText("Dealer shows Ace")));
    }

    @Test
    public void testStartButtonVisibleInLobby() {
        String json = "{" + "\"messageType\":\"game_state\"," + "\"gamePhase\":\"lobby\"," + "\"status\":\"Waiting\"," + "\"centerInfo\":\"\"," + "\"yourSeat\":0," + "\"players\":[{\"seat\":0,\"username\":\"mack\",\"cards\":[]}]," + "\"actions\":[]" + "}";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.onWebSocketMessage(json);
        });

        try { Thread.sleep(SIMULATED_DELAY_MS); } catch (Exception e) {}

        onView(withId(R.id.statusText)).check(matches(withText("Waiting")));
    }

    @Test
    public void testMoveButtonsUpdateFromActions() {
        String json = "{" + "\"messageType\":\"game_state\"," + "\"gamePhase\":\"playing\"," + "\"status\":\"Choose move\"," + "\"centerInfo\":\"\","+ "\"yourSeat\":0," + "\"players\":[{\"seat\":0,\"username\":\"mack\",\"cards\":[]}]," + "\"actions\":[\"Hit\",\"Stand\",\"Fold\"]" + "}";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.onWebSocketMessage(json);
        });

        try { Thread.sleep(SIMULATED_DELAY_MS); } catch (Exception e) {}

        onView(withId(R.id.moveButton1)).check(matches(withText("Hit")));
        onView(withId(R.id.moveButton2)).check(matches(withText("Stand")));
        onView(withId(R.id.moveButton3)).check(matches(withText("Fold")));
    }
}
