# AndroidWebView

이 프로젝트는 Android WebView를 사용하여 웹 콘텐츠를 앱 내에서 표시하는 방법을 보여주는 예제입니다. WebView는 네이티브 애플리케이션 내에서 웹 페이지를 렌더링하고, JavaScript와 상호작용하며, 앱 내에서 웹 기반 기능을 쉽게 사용할 수 있도록 도와줍니다.

## 기술 스택

- **언어**: Kotlin
- **UI 프레임워크**: Android XML 기반 UI
- **웹 콘텐츠**: WebView

## 주요 기능

- **웹 페이지 로드**: WebView를 사용하여 외부 웹 페이지 또는 앱 내의 로컬 HTML 파일을 로드합니다.
- **JavaScript 활성화**: WebView에서 JavaScript를 활성화하여 웹 콘텐츠와 상호작용할 수 있습니다.
- **클라이언트 설정**: WebViewClient를 통해 URL 변경이나 로딩 상태를 관리하고, 웹 페이지의 탐색 동작을 제어합니다.
- **파일 업로드 지원**: 파일 업로드 기능을 지원하여 사용자가 파일 선택을 통해 웹 페이지와 상호작용할 수 있습니다.

## WebView 구현 방식

- **WebView 초기화**: XML 레이아웃에 WebView를 배치하고, loadUrl() 메서드를 사용해 웹 페이지를 로드합니다.
- **JavaScript 활성화**: WebView 설정에서 JavaScript를 활성화하여 더 복잡한 웹 기반 인터랙션을 허용합니다.
- **WebViewClient 설정**: WebViewClient를 설정하여 브라우저가 아닌 앱 내에서 모든 URL을 처리하며, 외부 링크나 특정 동작을 제어할 수 있습니다.