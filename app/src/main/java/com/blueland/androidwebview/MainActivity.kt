package com.blueland.androidwebview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
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

    // 파일 선택을 위한 변수
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    // 파일 선택 결과를 처리하는 콜백
    private val fileChooserResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val uris: Array<Uri>? = when {
                data?.clipData != null -> {
                    // 여러 파일 선택한 경우
                    val count = data.clipData?.itemCount ?: 0
                    Array(count) { i -> data.clipData?.getItemAt(i)?.uri!! }
                }

                data?.data != null -> {
                    // 단일 파일 선택한 경우
                    arrayOf(data.data!!)
                }

                else -> null
            }
            filePathCallback?.onReceiveValue(uris ?: emptyArray())
        } else {
            filePathCallback?.onReceiveValue(null)
        }
    }

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

            // WebChromeClient 설정하여 파일 선택 대화 상자를 처리
            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    this@MainActivity.filePathCallback = filePathCallback

                    // 파일 선택 Intent 생성
                    // 필요에 따라 선택할 파일 유형과 다중 선택 여부를 설정합니다.

                    // 1. 모든 파일 유형을 선택 (단일 파일 선택)
//                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                        addCategory(Intent.CATEGORY_OPENABLE)
//                        type = "*/*"  // 모든 파일 유형 허용
//                    }

                    // 2. 모든 파일 유형을 선택 (여러 파일 선택)
//                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                        addCategory(Intent.CATEGORY_OPENABLE)
//                        type = "*/*"  // 모든 파일 유형 허용
//                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)  // 여러 파일 선택 허용
//                    }

                    // 3. 이미지 파일만 선택 (단일 이미지 선택)
//                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                        addCategory(Intent.CATEGORY_OPENABLE)
//                        type = "image/*"  // 이미지 파일만 선택 가능
//                    }

                    // 4. 이미지 파일만 선택 (여러 이미지 선택)
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"  // 이미지 파일만 선택 가능
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)  // 여러 이미지 선택 허용
                    }

                    // 파일 선택 결과 처리
                    fileChooserResultLauncher.launch(intent)
                    return true
                }

                // ProgressBar 업데이트
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    // newProgress는 0부터 100까지의 값을 가짐
                    binding.progressBar.progress = newProgress

                    // 로딩 중일 때는 ProgressBar를 표시, 완료되면 숨김
                    if (newProgress == 100) {
                        binding.progressBar.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }

            loadUrl(webViewUrl)  // 웹 페이지 로드
        }
    }

    // 메모리 누수 방지를 위해 액티비티가 파괴될 때 파일 선택 콜백을 해제
    override fun onDestroy() {
        super.onDestroy()
        filePathCallback = null
    }
}
