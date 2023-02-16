package team.jsv.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import team.jsv.data.repositoryImpl.ImageRepositoryImpl
import team.jsv.domain.repository.ImageRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindImageRepository(repository: ImageRepositoryImpl) : ImageRepository

}