package com.kevinzamora.temporis_androidapp.ui.accessibility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccessibilityViewModel : ViewModel() {
    private val _isHighContrast = MutableLiveData<Boolean>()
    val isHighContrast: LiveData<Boolean> = _isHighContrast

    private val _isBoldText = MutableLiveData<Boolean>()
    val isBoldText: LiveData<Boolean> = _isBoldText
}