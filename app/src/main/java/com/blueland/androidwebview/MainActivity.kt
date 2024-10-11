package com.blueland.androidwebview

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.blueland.androidwebview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /** WebView에서 Android WebView로 테스트할 수 있는 기능을 제공하는 URL
     * 예)
     * 기본적인 파일 업로드 기능
     * 이미지 파일 선택(갤러리 호출 테스트에 유용)
     */
    private val webViewUrl = "https://blueimp.github.io/jQuery-File-Upload/"

    // View Binding 객체 선언
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DataBinding을 사용하여 레이아웃 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // WebView 설정
        setupWebView()
    }

    // WebView 설정 로직을 별도 함수로 분리
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(binding.webView) {
            webViewClient = WebViewClient()  // WebViewClient 설정
            settings.javaScriptEnabled = true  // JavaScript 사용 가능 설정
            loadUrl(webViewUrl)  // 웹 페이지 로드
        }
    }
}
