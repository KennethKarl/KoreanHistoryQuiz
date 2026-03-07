package com.historyquiz.app.core.error

class AppException(val appError: AppError) : Exception(appError.message, appError.cause)
