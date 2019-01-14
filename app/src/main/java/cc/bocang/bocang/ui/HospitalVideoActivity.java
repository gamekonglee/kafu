package cc.bocang.bocang.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import cc.bocang.bocang.R;

/**
 * 医院场景视频
 *
 * created by hardawin 2017-12-15
 */
public class HospitalVideoActivity extends Activity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hospital_video);
        initView();
    }

    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        // 设置视频控制器
        mVideoView.setMediaController(new MediaController(this));
        // 设置播放完成回调
        mVideoView.setOnCompletionListener(new MyPlayerListener());
        // 设置视频路径
        mVideoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.movie01));// 视频地址
        mVideoView.requestFocus();
        // 开始播放视频
        mVideoView.start();
    }

    class MyPlayerListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
//            Toast.makeText(HospitalVideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
