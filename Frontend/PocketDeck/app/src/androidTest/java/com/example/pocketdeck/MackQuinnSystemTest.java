package com.example.pocketdeck;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Map;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class MackQuinnSystemTest {

    private static final int SIMULATED_DELAY_MS = 700;
    private Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        context.getSharedPreferences("userPreferences", MODE_PRIVATE).edit().clear().apply();
    }

    @Test
    public void playButtonNotLoggedInOpensLoginScreen() {
        onView(withId(R.id.playButton)).perform(click());
        wait700();
        onView(withId(R.id.login_username_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.login_password_txt)).check(matches(isDisplayed()));
    }

    @Test
    public void modeButtonOpensGameModesScreen() {
        onView(withId(R.id.modeButton)).perform(click());
        wait700();
        onView(withId(R.id.pokerCard)).check(matches(isDisplayed()));
        onView(withId(R.id.blackjackCard)).check(matches(isDisplayed()));
    }

    @Test
    public void menuSettingsOpensSettingsScreen() {
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText("Settings")).perform(click());
        wait700();
        onView(withId(R.id.musicSwitch)).check(matches(isDisplayed()));
        onView(withId(R.id.volumeBar)).check(matches(isDisplayed()));
    }

    @Test
    public void menuHistoryOpensGameHistoryScreenWithNoUser() {
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText("Game History")).perform(click());
        wait700();
        onView(withText("No active user found.")).check(matches(isDisplayed()));
    }

    @Test
    public void selectingPokerSavesPokerPreference() {
        ActivityScenario.launch(GameModes.class);

        onView(withId(R.id.pokerCard)).perform(click());
        SharedPreferences prefs = context.getSharedPreferences("userPreferences", MODE_PRIVATE);
        assertEquals("Poker", prefs.getString("selectedGame", ""));
        assertEquals(1, prefs.getInt("selectedGameId", -1));
    }

    @Test
    public void selectingBlackjackSavesBlackjackPreference() {
        ActivityScenario.launch(GameModes.class);

        onView(withId(R.id.blackjackCard)).perform(click());
        SharedPreferences prefs = context.getSharedPreferences("userPreferences", MODE_PRIVATE);
        assertEquals("Blackjack", prefs.getString("selectedGame", ""));
        assertEquals(2, prefs.getInt("selectedGameId", -1));
    }

    @Test
    public void selectingFutureGamesSavesPreferences() {
        ActivityScenario.launch(GameModes.class);

        onView(withId(R.id.futureCard1)).perform(click());
        SharedPreferences prefs = context.getSharedPreferences("userPreferences", MODE_PRIVATE);
        assertEquals("garbage", prefs.getString("selectedGame", ""));
        assertEquals(3, prefs.getInt("selectedGameId", -1));
        onView(withId(R.id.futureCard2)).perform(click());
        assertEquals("solitaire", prefs.getString("selectedGame", ""));
        assertEquals(4, prefs.getInt("selectedGameId", -1));
    }

    @Test
    public void settingsLoadsSavedPreferencesAndToggleWorks() {
        context.getSharedPreferences("userPreferences", MODE_PRIVATE).edit().putBoolean("musicOn", true).putInt("volume", 25).apply();

        ActivityScenario.launch(Settings.class);
        onView(withId(R.id.musicSwitch)).check(matches(isChecked()));
        onView(withId(R.id.musicSwitch)).perform(click());
        SharedPreferences prefs = context.getSharedPreferences("userPreferences", MODE_PRIVATE);
        assertFalse(prefs.getBoolean("musicOn", true));
    }

    @Test
    public void loginEmptyFieldsStaysOnLoginScreen() {
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.login_login_button)).perform(click());
        onView(withId(R.id.login_username_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.login_password_txt)).check(matches(isDisplayed()));
    }

    @Test
    public void loginBackButtonReturnsToMainScreen() {
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.login_back_button)).perform(click());
        wait700();
        onView(withId(R.id.playButton)).check(matches(isDisplayed()));
    }

    @Test
    public void userUtilitiesStoresAndClearsUserData() throws Exception {
        UserUtilities utilities = new UserUtilities(context);

        JSONObject user = new JSONObject();
        user.put("id", 42);
        user.put("username", "mack");
        user.put("userStatus", "ACTIVE");
        utilities.applyUserObject(user);
        assertEquals(42, utilities.getSavedId());
        assertEquals("mack", utilities.getSavedUsername());
        assertEquals("ACTIVE", utilities.getSavedStatus());
        utilities.applyUsername("newmack");
        assertEquals("newmack", utilities.getSavedUsername());
        Map<String, String> map = utilities.getUserMap("abc", "123");
        assertEquals("abc", map.get("username"));
        assertEquals("123", map.get("password"));
        utilities.setSelectedGame("Poker");
        utilities.setSelectedGameID(1);
        assertEquals("Poker", utilities.getSelectedGame());
        assertEquals(1, utilities.getSelectedGameId());
        utilities.logoutUser();
        assertEquals(-1, utilities.getSavedId());
        assertEquals("ERR_INVALID_REQUEST", utilities.getSavedUsername());
    }

    @Test
    public void musicPlayerMethodsDoNotCrash() {
        MusicPlayer.startMusic(context);
        MusicPlayer.Volume(40);
        MusicPlayer.pauseMusic();
        MusicPlayer.musicPref(context);
    }

    @Test
    public void gamePlayWebSocketCallbacksUpdateStatus() {
        setupLoggedInBlackjackUser();
        ActivityScenario<GamePlayScreen> scenario = ActivityScenario.launch(GamePlayScreen.class);

        scenario.onActivity(activity -> {
            activity.onWebSocketOpen(null);
            activity.onWebSocketMessage("{\"messageType\":\"message\",\"gamePhase\":\"lobby\",\"text\":\"Player joined\"}");
            activity.onWebSocketClose(1000, "closed", false);
            activity.onWebSocketError(new Exception("fake error"));
        });

        wait700();

        onView(withId(R.id.statusText)).check(matches(withText("Disconnected")));
    }

    @Test
    public void gamePlayGameStateMessageUpdatesMainGameUi() {
        setupLoggedInBlackjackUser();
        ActivityScenario<GamePlayScreen> scenario = ActivityScenario.launch(GamePlayScreen.class);

        scenario.onActivity(activity -> {
            try {
                String message = "{" + "\"messageType\":\"game_state\"," + "\"gamePhase\":\"playing\"," + "\"status\":\"Your turn\"," + "\"centerInfo\":\"Dealer showing\"," + "\"yourSeat\":0," + "\"centerCards\":[{\"value\":\"ACE\",\"suit\":\"SPADES\"}]," + "\"centerHidden\":1," + "\"players\":[" + "{\"seat\":0,\"username\":\"mack\",\"cards\":[{\"value\":\"KING\",\"suit\":\"HEARTS\"}]}," + "{\"seat\":1,\"username\":\"bob\",\"hiddenCount\":2}" + "]," + "\"actions\":[\"hit\",\"stand\",\"double\"]" + "}";
                Method method = GamePlayScreen.class.getDeclaredMethod("gameUpdate", String.class);
                method.setAccessible(true);
                method.invoke(activity, message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        onView(withId(R.id.statusText)).check(matches(withText("Your turn")));
        onView(withId(R.id.centerText)).check(matches(withText("Dealer showing")));
        onView(withId(R.id.moveButton1)).check(matches(withText("hit")));
        onView(withId(R.id.moveButton2)).check(matches(withText("stand")));
        onView(withId(R.id.moveButton3)).check(matches(withText("double")));
        onView(withId(R.id.otherPlayersText)).check(matches(withText(containsString("bob"))));
    }

    @Test
    public void gamePlayBadJsonShowsFallbackMessage() {
        setupLoggedInBlackjackUser();
        ActivityScenario<GamePlayScreen> scenario = ActivityScenario.launch(GamePlayScreen.class);

        scenario.onActivity(activity -> {
            try {
                Method method = GamePlayScreen.class.getDeclaredMethod("gameUpdate", String.class);
                method.setAccessible(true);
                method.invoke(activity, "not json");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        onView(withId(R.id.statusText)).check(matches(withText("No game update provided")));
    }

    @Test
    public void gamePlayPrivateCardFormatterCoversRanksAndSuits() {
        setupLoggedInBlackjackUser();
        ActivityScenario<GamePlayScreen> scenario = ActivityScenario.launch(GamePlayScreen.class);

        scenario.onActivity(activity -> {
            try {
                Method method = GamePlayScreen.class.getDeclaredMethod("formatCardTextMessage", String.class, String.class
                );
                method.setAccessible(true);
                assertEquals("A♠", method.invoke(activity, "ACE", "SPADES"));
                assertEquals("K♥", method.invoke(activity, "KING", "HEARTS"));
                assertEquals("Q♦", method.invoke(activity, "QUEEN", "DIAMONDS"));
                assertEquals("J♣", method.invoke(activity, "JACK", "CLUBS"));
                assertEquals("10♠", method.invoke(activity, "TEN", "SPADES"));
                assertEquals("2♣", method.invoke(activity, "TWO", "CLUBS"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void gamePlayRenderPlayersNullShowsWaitingMessage() {
        setupLoggedInBlackjackUser();
        ActivityScenario<GamePlayScreen> scenario = ActivityScenario.launch(GamePlayScreen.class);

        scenario.onActivity(activity -> {
            try {
                Method method = GamePlayScreen.class.getDeclaredMethod("renderPlayers", JSONArray.class);
                method.setAccessible(true);
                method.invoke(activity, (Object) null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        onView(withId(R.id.otherPlayersText)).check(matches(withText("Waiting for players...")));
    }

    @Test
    public void gamePlaySetButtonsWithNoMovesShowsLeave() {
        setupLoggedInBlackjackUser();
        ActivityScenario<GamePlayScreen> scenario = ActivityScenario.launch(GamePlayScreen.class);

        scenario.onActivity(activity -> {
            try {
                Method method = GamePlayScreen.class.getDeclaredMethod("setButtons", JSONArray.class);
                method.setAccessible(true);
                method.invoke(activity, new JSONArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        onView(withId(R.id.moveButton3)).check(matches(withText("Leave")));
    }

    @Test
    public void gamePlayUpdateMoveDoesNotCrash() {
        setupLoggedInBlackjackUser();
        ActivityScenario<GamePlayScreen> scenario = ActivityScenario.launch(GamePlayScreen.class);

        scenario.onActivity(activity -> {
            try {
                Method method = GamePlayScreen.class.getDeclaredMethod("updateMove", String.class);
                method.setAccessible(true);
                method.invoke(activity, "hit");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        onView(withId(R.id.statusText)).check(matches(isDisplayed()));
    }

    @Test
    public void gameNotesEmptySaveAndClearDoNotCrash() {
        setupLoggedInBlackjackUser();
        ActivityScenario.launch(GameNotes.class);
        onView(withId(R.id.noteEdit)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.saveNoteButton)).perform(click());
        onView(withId(R.id.clearNoteButton)).perform(click());
        onView(withId(R.id.noteEdit)).check(matches(withText("")));
    }

    private void setupLoggedInBlackjackUser() {
        context.getSharedPreferences("userPreferences", MODE_PRIVATE).edit().putBoolean("isLoggedIn", true).putInt("userID", 1).putString("username", "testuser").putString("status", "ACTIVE").putString("selectedGame", "Blackjack").putInt("selectedGameId", 2).putBoolean("musicOn", false).apply();
    }

    private void wait700() {
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }
    }
}