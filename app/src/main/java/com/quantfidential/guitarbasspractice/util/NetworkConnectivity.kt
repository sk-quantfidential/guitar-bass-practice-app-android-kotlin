package com.quantfidential.guitarbasspractice.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivity @Inject constructor(
    private val context: Context
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(hasInternet)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        // Send current state
        trySend(getCurrentConnectivityState())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun getCurrentConnectivityState(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

data class OfflineCapabilities(
    val exerciseCreation: Boolean = true,
    val exerciseStorage: Boolean = true,
    val exercisePlayback: Boolean = true,
    val userProfiles: Boolean = true,
    val fretboardVisualization: Boolean = true,
    val notationRendering: Boolean = true,
    val customization: Boolean = true,
    val aiGeneration: Boolean = false,
    val cloudSync: Boolean = false,
    val onlineFeatures: Boolean = false
)

@Singleton
class OfflineModeManager @Inject constructor(
    private val networkConnectivity: NetworkConnectivity,
    @ApplicationContext private val context: Context
) {
    
    fun getCapabilities(): Flow<OfflineCapabilities> = 
        networkConnectivity.isConnected.distinctUntilChanged()
            .map { isOnline ->
                OfflineCapabilities(
                    aiGeneration = isOnline,
                    cloudSync = isOnline,
                    onlineFeatures = isOnline
                )
            }
    
    suspend fun isFeatureAvailable(feature: OfflineFeature): Boolean {
        return when (feature) {
            OfflineFeature.EXERCISE_CREATION -> true
            OfflineFeature.EXERCISE_STORAGE -> true
            OfflineFeature.EXERCISE_PLAYBACK -> true
            OfflineFeature.USER_PROFILES -> true
            OfflineFeature.FRETBOARD_VISUALIZATION -> true
            OfflineFeature.NOTATION_RENDERING -> true
            OfflineFeature.CUSTOMIZATION -> true
            OfflineFeature.AI_GENERATION -> getCurrentConnectivityState()
            OfflineFeature.CLOUD_SYNC -> getCurrentConnectivityState()
        }
    }
    
    private fun getCurrentConnectivityState(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

enum class OfflineFeature {
    EXERCISE_CREATION,
    EXERCISE_STORAGE,
    EXERCISE_PLAYBACK,
    USER_PROFILES,
    FRETBOARD_VISUALIZATION,
    NOTATION_RENDERING,
    CUSTOMIZATION,
    AI_GENERATION,
    CLOUD_SYNC
}