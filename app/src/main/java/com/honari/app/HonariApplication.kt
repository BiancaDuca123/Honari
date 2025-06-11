package com.honari.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for Honari app.
 * Initializes Hilt dependency injection.
 */
@HiltAndroidApp
class HonariApplication : Application()
