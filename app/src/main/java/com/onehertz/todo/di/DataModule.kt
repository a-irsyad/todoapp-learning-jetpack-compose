package com.onehertz.todo.di

import android.content.Context
import androidx.room.Room
import com.onehertz.todo.data.source.local.TaskDao
import com.onehertz.todo.data.source.local.ToDoDatabase
import com.onehertz.todo.data.source.network.NetworkDataSource
import com.onehertz.todo.data.source.network.NetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseSourceModule {

    @Provides
    @Singleton
    fun provideToDoDatabase(@ApplicationContext context: Context): ToDoDatabase {
        return Room.databaseBuilder(
            context,
            ToDoDatabase::class.java,
            "ToDoDatabase.db"
        ).build()
    }

    @Provides
    fun provideLTaskDao(toDoDatabase: ToDoDatabase): TaskDao = toDoDatabase.taskDao()

}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkDataSourceModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(datasource: NetworkDataSourceImpl): NetworkDataSource
}
