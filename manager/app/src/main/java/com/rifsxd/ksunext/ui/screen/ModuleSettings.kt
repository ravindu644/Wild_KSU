package com.rifsxd.ksunext.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.rifsxd.ksunext.ui.component.SwitchItem

@Destination<RootGraph>
@Composable
fun ModuleSettingsScreen(
    navigator: DestinationsNavigator
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        val context = LocalContext.current
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Module Cards Always Expanded Setting
                        var keepModulesExpanded by rememberSaveable {
                            mutableStateOf(
                                prefs.getBoolean("keep_modules_expanded", false)
                            )
                        }
                        SwitchItem(
                            icon = Icons.Filled.ExpandMore,
                            title = "Keep Module Cards Expanded",
                            summary = "Always keep module cards expanded instead of collapsing them",
                            checked = keepModulesExpanded
                        ) {
                            prefs.edit().putBoolean("keep_modules_expanded", it).apply()
                            keepModulesExpanded = it
                        }

                        // Banner Toggle Setting
                        var useBanner by rememberSaveable {
                            mutableStateOf(
                                prefs.getBoolean("use_banner", true)
                            )
                        }
                        SwitchItem(
                            icon = Icons.Filled.ViewCarousel,
                            title = "Enable Module Banners",
                            summary = "Show background banners for modules",
                            checked = useBanner
                        ) {
                            prefs.edit().putBoolean("use_banner", it).apply()
                            useBanner = it
                        }

                        // Hide Module Details Text Setting
                        var hideModuleDetails by rememberSaveable {
                            mutableStateOf(
                                prefs.getBoolean("hide_module_details", false)
                            )
                        }
                        SwitchItem(
                            icon = Icons.Filled.VisibilityOff,
                            title = "Hide Module Details",
                            summary = "Hide descriptive text like module size, web UI, action, and Zygisk requirements",
                            checked = hideModuleDetails
                        ) {
                            prefs.edit().putBoolean("hide_module_details", it).apply()
                            hideModuleDetails = it
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun ModuleSettingsPreview() {
    ModuleSettingsScreen(EmptyDestinationsNavigator)
}