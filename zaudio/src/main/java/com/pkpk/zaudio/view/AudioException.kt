package com.pkpk.zaudio.view

class AudioException(private val msg: String? = null, private val throwable: Throwable? = null) : Exception(msg, throwable)