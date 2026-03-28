package eu.kanade.presentation.more.settings.screen.player

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.presentation.more.settings.screen.SearchableSettings
import eu.kanade.tachiyomi.ui.player.settings.SubtitlePreferences
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.domain.storage.service.StorageManager
import tachiyomi.i18n.aniyomi.AYMR
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.Locale
import java.util.MissingResourceException

object PlayerSettingsSubtitleScreen : SearchableSettings {

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = AYMR.strings.pref_player_subtitle

    @Composable
    override fun RowScope.AppBarAction() {
    }

    @Composable
    override fun getPreferences(): List<Preference> {
        val subtitlePreferences = remember { Injekt.get<SubtitlePreferences>() }
        val storageManager = remember { Injekt.get<StorageManager>() }

        var showClearFontsDialog by remember { mutableStateOf(false) }
        var fontsCount by remember { mutableIntStateOf(0) }

        val langPref = subtitlePreferences.preferredSubLanguages()
        val whitelist = subtitlePreferences.subtitleWhitelist()
        val blacklist = subtitlePreferences.subtitleBlacklist()
        val downloadCustomFonts = subtitlePreferences.downloadCustomFonts()

        if (showClearFontsDialog) {
            AlertDialog(
                onDismissRequest = { showClearFontsDialog = false },
                title = { Text(text = stringResource(AYMR.strings.pref_clear_mpv_fonts)) },
                text = {
                    Text(text = stringResource(AYMR.strings.pref_clear_mpv_fonts_confirm, fontsCount))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            launchIO {
                                storageManager.getFontsDirectory()?.listFiles()?.forEach { it.delete() }
                            }
                            showClearFontsDialog = false
                        },
                    ) {
                        Text(text = stringResource(MR.strings.action_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearFontsDialog = false }) {
                        Text(text = stringResource(MR.strings.action_cancel))
                    }
                },
            )
        }

        return listOf(
            Preference.PreferenceItem.SwitchPreference(
                preference = downloadCustomFonts,
                title = stringResource(AYMR.strings.pref_download_custom_fonts),
                subtitle = stringResource(AYMR.strings.pref_download_custom_fonts_info),
            ),
            Preference.PreferenceItem.TextPreference(
                title = stringResource(AYMR.strings.pref_clear_mpv_fonts),
                onClick = {
                    fontsCount = storageManager.getFontsDirectory()?.listFiles()?.size ?: 0
                    if (fontsCount > 0) {
                        showClearFontsDialog = true
                    }
                },
            ),
            Preference.PreferenceItem.EditTextInfoPreference(
                preference = langPref,
                dialogSubtitle = stringResource(AYMR.strings.pref_player_subtitle_lang_info),
                title = stringResource(AYMR.strings.pref_player_subtitle_lang),
                validate = { pref ->
                    val langs = pref.split(",").filter(String::isNotEmpty).map(String::trim)
                    langs.forEach {
                        try {
                            val locale = Locale(it)
                            if (locale.isO3Language == locale.language &&
                                locale.language == locale.getDisplayName(Locale.ENGLISH)
                            ) {
                                throw MissingResourceException("", "", "")
                            }
                        } catch (_: MissingResourceException) {
                            return@EditTextInfoPreference false
                        }
                    }

                    true
                },
                errorMessage = { pref ->
                    val langs = pref.split(",").filter(String::isNotEmpty).map(String::trim)
                    langs.forEach {
                        try {
                            val locale = Locale(it)
                            if (locale.isO3Language == locale.language &&
                                locale.language == locale.getDisplayName(Locale.ENGLISH)
                            ) {
                                throw MissingResourceException("", "", "")
                            }
                        } catch (_: MissingResourceException) {
                            return@EditTextInfoPreference stringResource(
                                AYMR.strings.pref_player_subtitle_invalid_lang,
                                it,
                            )
                        }
                    }
                    ""
                },
            ),
            Preference.PreferenceItem.EditTextInfoPreference(
                preference = whitelist,
                dialogSubtitle = stringResource(AYMR.strings.pref_player_subtitle_whitelist_info),
                title = stringResource(AYMR.strings.pref_player_subtitle_whitelist),
            ),
            Preference.PreferenceItem.EditTextInfoPreference(
                preference = blacklist,
                dialogSubtitle = stringResource(AYMR.strings.pref_player_subtitle_blacklist_info),
                title = stringResource(AYMR.strings.pref_player_subtitle_blacklist),
            ),
        )
    }
}
