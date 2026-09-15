package com.banaoreel.app.di

import com.banaoreel.app.data.api.BanaoReelApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

// 10.0.2.2 is the Android emulator's alias for your host machine's localhost.
// If you're testing on a physical device instead, use your machine's actual
// LAN IP (e.g. "http://192.168.1.x:8081/") and make sure the device is on the
// same Wi-Fi and your firewall allows inbound connections on that port.
private const val BASE_URL = "http://10.0.2.2:8081/" // TODO: move to BuildConfig per environment, switch to https in prod

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()
    // Note: no KotlinJsonAdapterFactory here on purpose — every data class in
    // data/model/ is annotated with @JsonClass(generateAdapter = true), which
    // Moshi's codegen (via KSP) picks up automatically without needing
    // reflection at runtime. Faster and avoids a whole class of Moshi
    // reflection crashes on obfuscated/minified release builds.

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideBanaoReelApi(retrofit: Retrofit): BanaoReelApi =
        retrofit.create(BanaoReelApi::class.java)
}
