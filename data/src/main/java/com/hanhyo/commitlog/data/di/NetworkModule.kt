package com.hanhyo.commitlog.data.di

import com.hanhyo.commitlog.data.BuildConfig
import com.hanhyo.commitlog.data.source.remote.AiService
import com.hanhyo.commitlog.data.source.remote.GeminiAiService
import com.hanhyo.commitlog.data.source.remote.OpenAiService
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

    /**
     * AI 서비스 제공
     *
     * 현재: Gemini (기본)
     * OpenAI로 전환하려면 아래 한 줄만 변경:
     *   GeminiAiService(...) → OpenAiService(BuildConfig.OPENAI_API_KEY, client)
     */
    @Provides
    @Singleton
    fun provideAiService(client: OkHttpClient): AiService {
        return GeminiAiService(
            apiKey = BuildConfig.GEMINI_API_KEY,
            client = client,
        )
        // OpenAI로 변경 시:
//         return OpenAiService(
//             apiKey = BuildConfig.OPENAI_API_KEY,
//             client = client,
//         )
    }
}
