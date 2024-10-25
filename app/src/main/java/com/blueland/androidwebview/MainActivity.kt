package com.blueland.androidwebview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.blueland.androidwebview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /** WebView 테스트를 위한 GitHub html 웹 페이지 **/
    private val webViewUrl = "https://blueland99.github.io/WebViewExample/index.html"

    // View Binding 객체 선언
    private lateinit var binding: ActivityMainBinding

    // 파일 선택을 위한 변수
    private var fileChooserCallback: ((Array<Uri>?) -> Unit)? = null

    // 파일 선택 결과를 처리하는 콜백
    private val fileChooserResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uris = data?.clipData?.let { clipData ->
                (0 until clipData.itemCount).map { clipData.getItemAt(it).uri }.toTypedArray()
            } ?: data?.data?.let { arrayOf(it) }

            // MIME 타입 확인 및 결과 처리
            uris?.forEach { uri ->
                val mimeType = contentResolver.getType(uri) ?: "unknown"
                if (mimeType.startsWith("image/")) {
                    Toast.makeText(this, "이미지 파일 선택됨: $mimeType", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "일반 파일 선택됨: $mimeType", Toast.LENGTH_SHORT).show()
                }
            }

            // 콜백으로 선택된 URI 전달
            fileChooserCallback?.invoke(uris)
        } else {
            fileChooserCallback?.invoke(null)
        }
    }

    // 뒤로 가기 버튼을 두 번 눌렀을 때 종료하기 위한 변수
    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DataBinding을 사용하여 레이아웃 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // WebView 설정
        setupWebView()

        // OnBackPressedDispatcher를 통해 뒤로 가기 버튼 처리
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    // WebView에서 뒤로 갈 수 있으면 WebView에서 뒤로가기
                    binding.webView.goBack()
                } else {
                    // WebView에서 뒤로 갈 수 없으면 두 번 눌러서 종료
                    if (backPressedOnce) {
                        finish()
                    } else {
                        backPressedOnce = true
                        Toast.makeText(this@MainActivity, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

                        // 2초 안에 다시 누르지 않으면 backPressedOnce 상태 초기화
                        Handler(Looper.getMainLooper()).postDelayed({
                            backPressedOnce = false
                        }, 2000)
                    }
                }
            }
        })
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
                    fileChooserCallback = { uris -> filePathCallback?.onReceiveValue(uris) }

                    // 요청된 MIME 타입 확인
                    val acceptTypes = fileChooserParams?.acceptTypes ?: arrayOf("*/*")
                    val isImageRequest = acceptTypes.any { it.startsWith("image/") }

                    // 선택기 Intent 설정
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = if (isImageRequest) "image/*" else "*/*" // 이미지인지 파일인지 구분
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, fileChooserParams?.mode == FileChooserParams.MODE_OPEN_MULTIPLE)
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

            // JavaScript 인터페이스 추가
            addJavascriptInterface(WebBridgeInterface(), "AndroidInterface")

            loadUrl(webViewUrl)  // 웹 페이지 로드
        }
    }

    // 메모리 누수 방지를 위해 액티비티가 파괴될 때 파일 선택 콜백을 해제
    override fun onDestroy() {
        super.onDestroy()
        binding.webView.clearCache(true)
        fileChooserCallback = null
    }

    // JavaScript에서 호출될 수 있는 인터페이스 정의
    inner class WebBridgeInterface {
        @JavascriptInterface
        fun postMessage(message: String) {
            // JavaScript에서 받은 메시지를 표시
            runOnUiThread {
                Toast.makeText(this@MainActivity, "JS 메시지: $message", Toast.LENGTH_SHORT).show()

                // WebView에서 JavaScript 함수 호출하여 Android 메시지 전송
                val androidMessage = "나야, 안드로이드"
                binding.webView.evaluateJavascript("receiveMessageFromAndroid('$androidMessage')", null)
            }
        }

        @JavascriptInterface
        fun showToast(message: String) {
            // JavaScript에서 받은 메세지를 toast로 표시
            runOnUiThread {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
