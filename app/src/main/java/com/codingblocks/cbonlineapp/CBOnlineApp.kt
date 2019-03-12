package com.codingblocks.cbonlineapp

import android.app.Application
import android.content.Context
import com.codingblocks.onlineapi.CustomResponseInterceptor
import com.devbrackets.android.exomedia.ExoMedia
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.TransferListener
import com.onesignal.OSNotification
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import com.squareup.picasso.Picasso
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import okhttp3.OkHttpClient

class CBOnlineApp : Application() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        Picasso.setSingletonInstance(Picasso.Builder(this).build())
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Cabin-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationReceivedHandler(NotificationReceivedHandler())
                .setNotificationOpenedHandler(NotificationOpenedHandler())
                .init()

        configureExoMedia()

    }

    companion object {
        lateinit var mInstance: CBOnlineApp
        fun getContext(): Context? {
            return mInstance.applicationContext
        }
    }

    private fun configureExoMedia() {
        // Registers the media sources to use the OkHttp client instead of the standard Apache one
        // Note: the OkHttpDataSourceFactory can be found in the ExoPlayer extension library `extension-okhttp`
        ExoMedia.setDataSourceFactoryProvider(object : ExoMedia.DataSourceFactoryProvider {
            private var instance: DataSource.Factory? = null

            override fun provide(userAgent: String, listener: TransferListener?): DataSource.Factory {
                if (instance == null) {
                    // Updates the network data source to use the OKHttp implementation
                    val interceptor = CustomResponseInterceptor()
                    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

                    val upstreamFactory = OkHttpDataSourceFactory(client, userAgent, listener)
                    instance = upstreamFactory
                    // Adds a cache around the upstreamFactory
//                    val cache = SimpleCache(File(cacheDir, "ExoMediaCache"), LeastRecentlyUsedCacheEvictor((50 * 1024 * 1024).toLong()))
//                    instance = CacheDataSourceFactory(cache, upstreamFactory, CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                }

                return instance!!
            }
        })
    }

    class NotificationReceivedHandler : OneSignal.NotificationReceivedHandler {
        override fun notificationReceived(notification: OSNotification?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    class NotificationOpenedHandler : OneSignal.NotificationOpenedHandler {
        override fun notificationOpened(result: OSNotificationOpenResult?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}