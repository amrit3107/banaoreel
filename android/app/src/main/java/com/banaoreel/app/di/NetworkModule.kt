package com.banaoreel.app.di

import com.banaoreel.app.BuildConfig
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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // Never log request/response bodies (including auth tokens) in
            // release builds -- verbose logging is a debug-only convenience.
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
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
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideBanaoReelApi(retrofit: Retrofit): BanaoReelApi =
        retrofit.create(BanaoReelApi::class.java)
}
