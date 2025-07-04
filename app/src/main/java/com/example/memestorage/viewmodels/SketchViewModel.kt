package com.example.memestorage.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SketchViewModel : ViewModel() {
    private val _isLock = MutableLiveData(false)
    val isLock: LiveData<Boolean> get() = _isLock

    private val _isFlash = MutableLiveData(false)
    val isFlash: LiveData<Boolean> get() = _isFlash

    private val _isExtended = MutableLiveData(false)
    val isExtended: LiveData<Boolean> get() = _isExtended

    private val _isOpenOpacity = MutableLiveData(false)
    val isOpenOpacity: LiveData<Boolean> get() = _isOpenOpacity

    private val _isConvert = MutableLiveData(false)
    val isConvert: LiveData<Boolean> get() = _isConvert

    private val _isFlip = MutableLiveData(false)
    val isFlip: LiveData<Boolean> get() = _isFlip

    private val _isRecommend = MutableLiveData(false)
    val isRecommend: LiveData<Boolean> get() = _isRecommend

    fun toggleRecommend() {
        _isRecommend.value = (_isRecommend.value ?: false).not()
    }

    fun toggleFlash() {
        _isFlash.value = (_isFlash.value ?: false).not()
    }

    fun toggleLock() {
        _isLock.value = (_isLock.value ?: false).not()
    }

    fun toggleExtend() {
        _isExtended.value = (_isExtended.value ?: false).not()
    }

    fun toggleOpacity() {
        _isOpenOpacity.value = (_isOpenOpacity.value ?: true).not()
    }

    fun toggleConvert(value: Boolean? = null) {
        if (value != null) {
            _isConvert.value = value
        } else {
            _isConvert.value = (_isConvert.value ?: false).not()
        }
    }
    fun toggleFlip(value: Boolean? = null) {
        if (value != null) {
            _isFlip.value = value
        } else {
            _isFlip.value = (_isFlip.value ?: false).not()
        }
    }
}