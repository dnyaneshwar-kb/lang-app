package com.example.hsklearner.di

import android.content.Context
import androidx.room.Room
import com.example.hsklearner.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hsk_database"
        )
        .fallbackToDestructiveMigration() // For development only
        .build()
    }

    @Provides
    fun provideHSKLevelDao(database: AppDatabase): HSKLevelDao = database.hskLevelDao()

    @Provides
    fun provideUnitDao(database: AppDatabase): UnitDao = database.unitDao()

    @Provides
    fun provideLessonDao(database: AppDatabase): LessonDao = database.lessonDao()

    @Provides
    fun provideVocabularyDao(database: AppDatabase): VocabularyDao = database.vocabularyDao()

    @Provides
    fun provideProgressDao(database: AppDatabase): ProgressDao = database.progressDao()
}
