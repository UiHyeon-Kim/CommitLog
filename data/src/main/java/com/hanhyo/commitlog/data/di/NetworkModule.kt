package com.hanhyo.commitlog.data.di

import com.hanhyo.commitlog.data.BuildConfig
import com.hanhyo.commitlog.data.source.remote.api.AiService
import com.hanhyo.commitlog.data.source.remote.api.GeminiAiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAiOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiApiKey(): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        require(apiKey.isNotBlank()) {
            "Gemini API 키가 설정되지 않았습니다. local.properties에 GEMINI_API_KEY를 추가하세요."
        }
        return apiKey
    }

    @Provides
    @Singleton
    fun provideAiService(
        apiKey: String,
        client: OkHttpClient
    ): AiService {
        return GeminiAiService(apiKey, client)
    }
}
