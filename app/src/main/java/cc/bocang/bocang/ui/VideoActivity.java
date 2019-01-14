package cc.bocang.bocang.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import cc.bocang.bocang.R;
import cc.bocang.bocang.global.Constant;

/**
 * 视频播放
 *
 * created by hardawin 2017-11-25
 */
public class VideoActivity extends BaseActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);

        initView();
    }

    private void initView() {
        Uri uri = Uri.parse(Constant.VIDEO_URL);// 网络视频
        mVideoView = (VideoView) findViewById(R.id.videoView);
        // 设置视频控制器
        mVideoView.setMediaController(new MediaController(this));
        // 设置播放完成回调
        mVideoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
        // 设置视频路径
        mVideoView.setVideoURI(uri);
        // 开始播放视频
        mVideoView.start();
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
//            Toast.makeText(VideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
