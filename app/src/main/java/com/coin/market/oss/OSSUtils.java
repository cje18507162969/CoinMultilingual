package com.coin.market.oss;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.text.MessageFormat;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import teng.wang.comment.base.FaceApplication;

public class OSSUtils {

    // 访问的endpoint地址
   public static final String OSS_ENDPOINT = Constances.EndPoint;
   public static final String OSS_ACCESS_KEY_ID = Constances.AccessKeyId;
   public static final String OSS_ACCESS_KEY_SECRET = Constances.AccessKeySecret;
   public static final String OSS_PATH = "coinsquare/feedback";
   public static final String BUCKET_NAME = Constances.BucketName;

    private static volatile OSSUtils instance;
    private static OSS mOss;

    private OSSUtils(){

    }

    public static OSSUtils getInstance() {
        if (instance == null) {
            synchronized (OSSUtils.class) {
                if (instance == null) {
                    instance = new OSSUtils();
                }
            }
        }
        return instance;
    }

    static String filelastnmae=null;//新地址的后缀部分
    public static  void put(final String ss, final String localFile, final String userId, final Callback callback) {
        File file = new File(localFile);
        if (!file.exists()) {
            callback.complete(false, "文件不存在", localFile,null);
            return;
        }
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                if (mOss == null) {
                    OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
                    ClientConfiguration conf = new ClientConfiguration();
                    conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                    conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                    conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
                    conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
                    mOss = new OSSClient(FaceApplication.newInstance(), OSS_ENDPOINT, credentialProvider, conf);
                    OSSLog.enableLog();
                }
                String endName = localFile.substring(localFile.lastIndexOf("."));
                PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, MessageFormat.format("{0}/android_{1}_{2}{3}", OSS_PATH, String.valueOf(userId), String.valueOf(System.currentTimeMillis()), endName), localFile);
                put.setCRC64(OSSRequest.CRC64Config.YES);
                mOss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        e.onNext(MessageFormat.format("http://{0}.{1}/{2}", BUCKET_NAME, OSS_ENDPOINT, request.getObjectKey()));
//                        filelastnmae =  request.getObjectKey();
                        filelastnmae=MessageFormat.format("http://{0}.{1}/{2}", BUCKET_NAME, OSS_ENDPOINT, request.getObjectKey());
                        System.out.println("line 3333333333图片上传成功getObjectKey"+request.getObjectKey());//这个就是上传后产生的图片路径地址

                    }
                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        // 请求异常
                        if (clientExcepion != null) {
                            clientExcepion.printStackTrace();
                            e.onError(new Throwable("网络异常"));
                            System.out.println("line 3333333333图片上传失败111");
                            return;
                        }
                        if (serviceException != null) {
                            serviceException.printStackTrace();
                            e.onError(new Throwable("上传失败"));
                            System.out.println("line 3333333333图片上传失败222");
                            return;
                        }
                        e.onError(new Throwable("上传失败"));
                        System.out.println("line 3333333333图片上传失败3333");
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        callback.complete(true, o, localFile,filelastnmae);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.complete(false, throwable.getMessage(), localFile,filelastnmae);
                    }
                });
    }

    public interface Callback {
        void complete(boolean success, String msg, String localFile, String filelastnmae);
    }
}
