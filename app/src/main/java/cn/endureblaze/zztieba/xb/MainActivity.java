package cn.endureblaze.zztieba.xb;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.webkit.WebChromeClient;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.Toast;
import android.os.Build;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends Activity {

	private WebView zz;

	private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Toast.makeText(this, "本工具十分简陋，仅供百度内部调整特殊时期查看2017年以前的贴使用，制作by汝南京", 5000).show();
		zz = findViewById(R.id.zz);
		progressBar = findViewById(R.id.pro);
		WebSettings settings = zz.getSettings();
        settings.setUserAgentString("Mozilla/5.0 (Symbian/3; Series60/5.2 NokiaN8-00/012.002; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) AppleWebKit/533.4 (KHTML, like Gecko) NokiaBrowser/7.3.0 Mobile Safari/533.4 3gpp-gba");//添加UA,  “app/XXX”：是与h5商量好的标识，h5确认UA为app/XXX就认为该请求的终端为App
        settings.setJavaScriptEnabled(true);
		settings.setUseWideViewPort(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		settings.setBlockNetworkImage(false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		settings.setDomStorageEnabled(true);
		settings.setUseWideViewPort(false);
		settings.setLoadsImagesAutomatically(true);
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		settings.setLoadWithOverviewMode(true);
        //设置参数
        settings.setBuiltInZoomControls(true);
        settings.setAppCacheEnabled(true);// 设置缓存

        zz.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {

					return false;
				}
			});
		zz.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					progressBar.setVisibility(View.VISIBLE);
					//显示进度条
					progressBar.setProgress(newProgress);
					if (newProgress == 100) {
						//加载完毕隐藏进度条
						progressBar.setVisibility(View.GONE);
					}
					super.onProgressChanged(view, newProgress);
				}
			});
        zz.loadUrl("https://tieba.baidu.com/f?ie=utf-8&kw=%E6%98%9F%E4%B9%8B%E5%8D%A1%E6%AF%94&fr=search");
		zz.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					final WebView.HitTestResult hitTestResult = zz.getHitTestResult();
					// 如果是图片类型或者是带有图片链接的类型
					if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
						// 弹出保存图片的对话框
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setTitle("提示");
						builder.setMessage("到浏览器查看并且保存图片？");
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									String picUrl = hitTestResult.getExtra();//获取图片链接
									Intent web = new Intent();        
									web.setAction("android.intent.action.VIEW");    
									Uri content_url = Uri.parse(picUrl);   
									web.setData(content_url);  
									startActivity(web);  
								}
							});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								// 自动dismiss
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
								}
							});
						AlertDialog dialog = builder.create();
						dialog.show();
						return true;
					}
					return false;//保持长按可以复制文字
				}
			});
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == WebChromeClientUtils.FILECHOOSER_RESULTCODE) {
			WebChromeClientUtils.onActivityResult(this, requestCode, resultCode, data);
		}
	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && zz.canGoBack()) {
			zz.goBack();// 返回前一个页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
