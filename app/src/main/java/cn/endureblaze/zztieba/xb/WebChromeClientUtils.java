package cn.endureblaze.zztieba.xb;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebChromeClientUtils {
    //处理文件上传的请求吗
    public static final int FILECHOOSER_RESULTCODE = 8083;
    private static ValueCallback<Uri> mUploadMessage;
    private static ValueCallback<Uri[]> mUploadMessageAboveL;

	private static final int RESULT_OK=200;

    //支持文件上传的WebChromeClient
    public static WebChromeClient getFileChromeClient(final Activity activity) {
        return new WebChromeClient() {
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, null, null);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, null);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, 
                                        String capture) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                String type = TextUtils.isEmpty(acceptType) ? "*/*" : acceptType;
                i.setType(type);
                activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            //Android 5.0+
            @Override
            @SuppressLint("NewApi")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                if (mUploadMessageAboveL != null) {
                    mUploadMessageAboveL.onReceiveValue(null);
                }
                mUploadMessageAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
					&& fileChooserParams.getAcceptTypes().length > 0) {
                    i.setType(fileChooserParams.getAcceptTypes()[0]);
                } else {
                    i.setType("image/*");
                }
                activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), 
												FILECHOOSER_RESULTCODE);
                return true;
            }
        };
    }

    //上面的方法调用后,记得回调该方法
    public static void onActivityResult(Activity activity, int requestCode,
                                        int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mUploadMessage != null) {//5.0以下
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                if (null != result) {
                    mUploadMessage.onReceiveValue(result);
                } else {
                    mUploadMessage.onReceiveValue(Uri.EMPTY);
                }
                mUploadMessage = null;

            } else if (mUploadMessageAboveL != null) {//5.0+
                Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                if (result != null) {
//                    mUploadMessageAboveL.onReceiveValue(new Uri[]{result});
                    onActivityResultAboveL(data);
                } else {
                    mUploadMessageAboveL.onReceiveValue(new Uri[]{});
                }
                mUploadMessageAboveL = null;
            }
        }
    }

    //Android 5.0+
    private static void onActivityResultAboveL(Intent data) {
        Uri[] results = null;
        if (data == null) {
        } else {
            String dataString = data.getDataString();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }
            if (dataString != null)
                results = new Uri[]{Uri.parse(dataString)};
        }
        mUploadMessageAboveL.onReceiveValue(results);
        mUploadMessageAboveL = null;
    }
}

