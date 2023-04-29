package com.sidgowda.pawcalc.data.settings.datasource

import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.model.toSettings
import com.sidgowda.pawcalc.data.settings.model.toSettingsEntity
import com.sidgowda.pawcalc.db.settings.SettingsDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@OptIn(FlowPreview::class)
class CachedSettingsDataSource @Inject constructor(
    private val settingsDao: SettingsDao,
    @Named("io") private val ioDispatcher: CoroutineDispatcher
) : SettingsDataSource {

    private val settingsSharedFlow = MutableSharedFlow<Settings>(replay = 1)
    private val scope = CoroutineScope(ioDispatcher)

    init {
        scope.launch {
            settingsDao.settings()
                .flatMapConcat { it.asFlow() }
                .onEach {
                    settingsSharedFlow.emit(it.toSettings())
                }.collect()
        }
    }

    override fun settings(): Flow<Settings> {
        return settingsSharedFlow.asSharedFlow()
    }

    override suspend fun updateSettings(updatedSettings: Settings) {
        settingsSharedFlow.emit(updatedSettings)
        settingsDao.update(updatedSettings.toSettingsEntity())
    }
}
