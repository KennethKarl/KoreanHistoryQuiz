package com.historyquiz.app.core.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Firebase ID Token을 HTTP 요청 헤더에 자동으로 주입하는 인터셉터
 *
 * - 토큰이 만료되었으면 Firebase SDK가 자동으로 갱신한다.
 * - 비로그인 상태(currentUser == null)이면 Authorization 헤더를 추가하지 않는다.
 * - 토큰 갱신 실패 시 헤더 없이 요청을 계속 보낸다 (API가 401로 응답하면 상위에서 처리).
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getIdToken()
        val request = if (token != null) {
            chain.request()
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }

    /**
     * Firebase ID Token을 동기적으로 가져온다.
     * 네트워크 스레드에서 호출되므로 runBlocking 사용.
     */
    private fun getIdToken(): String? = runBlocking {
        try {
            FirebaseAuth.getInstance().currentUser
                ?.getIdToken(/* forceRefresh = */ false)
                ?.await()
                ?.token
        } catch (e: Exception) {
            // 토큰 취득 실패 시 null 반환 → 헤더 없이 진행
            null
        }
    }
}
