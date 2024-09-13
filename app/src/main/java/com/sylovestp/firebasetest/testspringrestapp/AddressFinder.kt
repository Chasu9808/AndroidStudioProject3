package com.sylovestp.firebasetest.testspringrestapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.busanit501.androidlabtest501.R
import java.util.function.Consumer

class AddressFinder: Activity() {
    companion object {
        private const val ACTION = "com.example.addressfinder.FINDER"

        private const val JS_BRIDGE = "address_finder"
        private const val DOMAIN = "address.finder.net"
        private const val PATH = "assets"

        const val ADDRESS = "address"
        const val ZIPCODE = "zipcode"

        private var launcher: ActivityResultLauncher<Bundle>? = null
        val contract: ActivityResultContract<Bundle, Bundle>
            get() = object: ActivityResultContract<Bundle, Bundle>(){
            override fun createIntent(context: Context, input: Bundle): Intent = Intent(ACTION)
            override fun parseResult(resultCode: Int, intent: Intent?): Bundle =
                when (resultCode) {
                    RESULT_CANCELED -> Bundle.EMPTY
                    else -> intent?.extras ?: Bundle.EMPTY
                }
        }

        private var action: ((Bundle) -> Unit)? = null


        @JvmStatic
        fun open(onComplete: Consumer<Bundle>) {
            callee(onComplete::accept)

            openInternal()
        }


        fun open(onComplete:(Bundle) -> Unit){
            callee(onComplete)

            openInternal()
        }

        private fun openInternal() {
            launcher?.launch(Bundle())
        }

        @JvmStatic
        fun register(fragment: Fragment){
            launcher = fragment.registerForActivityResult(contract) { b -> action?.invoke(b) }
        }

        @JvmStatic
        fun register(activity: ComponentActivity){
            launcher = activity.registerForActivityResult(contract) { b -> action?.invoke(b)}
        }

        @JvmStatic
        fun unregister(){
            action = null
            launcher = null
        }

        private fun callee(func: (Bundle) -> Unit){ action = func }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_address_finder)

        val webView = findViewById<WebView>(R.id.webView).apply {
            with(settings){
                javaScriptEnabled = true

                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
                allowFileAccess = false
                allowContentAccess = false
            }

            addJavascriptInterface(JavascriptInterface(this@AddressFinder), JS_BRIDGE)


            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/${PATH}/", WebViewAssetLoader.AssetsPathHandler(this@AddressFinder))
                .setDomain(DOMAIN)
                .build()

            webViewClient = FileWebViewClient(assetLoader)
        }

        webView.loadUrl("https://${DOMAIN}/${PATH}/html/address.html")
    }

    private class FileWebViewClient(private val assetLoader: WebViewAssetLoader): WebViewClientCompat() {
        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? =
            assetLoader.shouldInterceptRequest(request!!.url)

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? =
            assetLoader.shouldInterceptRequest(Uri.parse(url))
    }

    private class JavascriptInterface(private val activity: Activity) {

        @android.webkit.JavascriptInterface
        fun result(address: String, zipCode: String){
            val intent = Intent().apply {
                putExtra(ADDRESS, address)
                putExtra(ZIPCODE, zipCode)
            }

            with(activity){
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}