package xyz.podd.piholestats.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.podd.piholestats.R
import xyz.podd.piholestats.model.Device

class DeviceViewModel(private val device: Device) : ViewModel() {
    private val _summary = MutableLiveData<PiHoleSummary>()
    val summary: LiveData<PiHoleSummary> = _summary

    fun fetchStatus() {
        val exceptionHandler = CoroutineExceptionHandler{ _, throwable ->
            throwable.printStackTrace()
            _summary.postValue(PiHoleSummary(R.drawable.ic_sentiment_very_dissatisfied_black_24dp))
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val status = device.service.getSummary()

            val drawable = when (status.enabled) {
                true -> R.drawable.ic_sentiment_very_satisfied_black_24dp
                else -> R.drawable.ic_sentiment_very_dissatisfied_black_24dp
            }

            val summary = PiHoleSummary(drawable,
                "%,d queries".format(status.queriesToday),
                "%.1f%% blocked".format(status.blockedTodayPercentage)
            )

            _summary.postValue(summary)
        }
    }
}

data class PiHoleSummary(val drawable: Int, val queries: String = "", val blocked: String = "")