/*****************************************************************
 * Code From: https://github.com/vecio/MediaCodecDemo
 * 
 * 
 * 				Modifed by: Shawn -> shinykongcn@gmail.com
 * 				Reference :
 * 										2013-11-15
 *****************************************************************/

package com.baoyihu.dragonplayer;

import java.util.List;
import java.util.Locale;

import com.baoyihu.common.util.DebugLog;
import com.baoyihu.dragonfly.constant.ErrorCode;
import com.baoyihu.dragonfly.constant.InfoCode;
import com.baoyihu.dragonfly.controller.DragonPlayerInterface;
import com.baoyihu.dragonfly.controller.DragonPlayerInterface.OnErrorListener;
import com.baoyihu.dragonfly.controller.DragonPlayerInterface.OnInfoListener;
import com.baoyihu.dragonfly.controller.PlayerController;
import com.baoyihu.dragonfly.controller.PlayerMonitor;
import com.baoyihu.dragonfly.controller.PlayerStatus;
import com.baoyihu.dragonfly.url.Media;
import com.baoyihu.dragonplayer.view.FloatingWindow;
import com.baoyihu.dragonplayer.view.FloatingWindow.UpdateTextInterface;
import com.baoyihu.dragonplayer.view.SelectDialog;
import com.baoyihu.util.SystemInfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends Activity
    implements SurfaceHolder.Callback, PlayerMonitor, OnErrorListener, OnInfoListener, OnClickListener
{
    private final String TAG = "PlayerActivity";
    
    private static final int PLAYER_SWITCH_BITRATE = 11901;
    
    private static final int PLAYER_PLAYING_UPDATE = 11902;
    
    private static final int PLAYER_START_TIME_UPDATE = 11903;
    
    private SurfaceHolder holder;
    
    private Media media = null;
    
    private DragonPlayerInterface playerController = null;
    
    private SurfaceView mSurfaceView;
    
    private RelativeLayout mainLayout;
    
    private TextView currentTimeText;
    
    private TextView durationText;
    
    /** buffer */
    private RelativeLayout relBuffer;
    
    private RelativeLayout controlView;
    
    private TextView textBufferPercent;
    
    private SeekBar mSeekBarMoive;
    
    private ImageButton playPauseButton;
    
    private boolean inited;
    
    private long timeCreate = 0;
    
    private FloatingWindow cpuWindow = null;
    
    private static final int BOOT_UP_BUFFER = 200;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DebugLog.debug(TAG, "onCreate:");
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.play);
        
        mainLayout = (RelativeLayout)findViewById(R.id.playerlayout);
        relBuffer = (RelativeLayout)findViewById(R.id.buffer_rl);
        mSeekBarMoive = (SeekBar)findViewById(R.id.playerView_seekBar);
        mSeekBarMoive.setOnSeekBarChangeListener(movieSeekBarChangeListener);
        
        textBufferPercent = (TextView)findViewById(R.id.media_process_buffer);
        playPauseButton = (ImageButton)findViewById(R.id.play_pause);
        playPauseButton.setImageResource(R.drawable.vod_play_pause);
        playPauseButton.setOnClickListener(viewClickListener);
        
        controlView = (RelativeLayout)findViewById(R.id.player_controler);
        media = (Media)getIntent().getParcelableExtra("media");
        
        initView();
        holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        timeCreate = System.currentTimeMillis();
        playerController = new PlayerController(media);
        playerController.setSurfaceView(mSurfaceView);
        playerController.setMonitor(this);
        playerController.setOnErrorListener(this);
        playerController.setOnInfoListener(this);
        playerController.setStartBufferTimeMill(BOOT_UP_BUFFER);
        playerController.setMaxBufferTimeMill(200 * 1000);
        mainLayout.setOnClickListener(this);
        showAppInfoWindow();
    }
    
    private void showAppInfoWindow()
    {
        if (cpuWindow == null)
        {
            cpuWindow = new FloatingWindow(mSurfaceView, new Rect(0, 0, 1000, 80), new UpdateTextInterface()
            {
                int color = Color.GREEN;
                
                @Override
                public String getText()
                {
                    String cpuString = "CPU:" + (int)SystemInfo.getProcessCpuRate() + "%  ";
                    String memInfo = "RAM:" + getAppPureMemoryCost();
                    String bufferString = "Audio:" + playerController.getBufferedSize(1) / 1024 + "K  " + " Video:"
                        + playerController.getBufferedSize(2) / 1024 + "K  ";
                    String ret = cpuString + memInfo + bufferString;
                    return ret;
                }
                
                @Override
                public int getColorText()
                {
                    return color;
                }
            });
            cpuWindow.start();
        }
        else
        {
            cpuWindow.dismiss();
            cpuWindow = null;
        }
    }
    
    private void testIso()
    {
        
    }
    
    private void setAllViewEnabled(boolean isEnable)
    {
        // tv unsupport seek,set progressbar seek disable always        
        mSeekBarMoive.setEnabled(isEnable);
        mSeekBarMoive.setFocusable(isEnable);
        mSeekBarMoive.setClickable(isEnable);
        playPauseButton.setEnabled(isEnable);
    }
    
    private OnClickListener viewClickListener = new OnClickListener()
    {
        
        @Override
        public void onClick(View arg0)
        {
            switch (arg0.getId())
            {
                case R.id.play_pause:
                    onPlayButtonClick();
                    break;
            }
            
        }
    };
    
    /** play progress */
    private int seekProgress;
    
    private OnSeekBarChangeListener movieSeekBarChangeListener = new OnSeekBarChangeListener()
    {
        @Override
        public void onStartTrackingTouch(SeekBar arg0)
        {
            
        }
        
        @Override
        public void onProgressChanged(SeekBar seekBar1, int progress, boolean fromUser)
        {
            if (fromUser)
            {
                DebugLog.debug(TAG, "onProgressChanged:" + progress);
                seekProgress = progress;
            }
            
        }
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            DebugLog.debug(TAG, "onStopTrackingTouch");
            int max = seekBar.getMax();
            long position = seekProgress * playerController.getDuration() / max;
            playerController.seekTo(position);
        }
    };
    
    public void onPlayButtonClick()
    {
        DebugLog.info(TAG, "onPlayButtonClick()");
        if (playerController != null)
        {
            int status = playerController.getStatus();
            if (status == PlayerStatus.STATUS_PAUSE)
            {
                playerController.resume();
                Toast.makeText(PlayerActivity.this, "ResumePlayer", Toast.LENGTH_LONG).show();
                playPauseButton.setImageResource(R.drawable.vod_play_pause);
            }
            else
            {
                playerController.pause();
                Toast.makeText(PlayerActivity.this, "PausePlayer", Toast.LENGTH_LONG).show();
                playPauseButton.setImageResource(R.drawable.vod_play_play);
            }
            setAllViewEnabled(true);
        }
    }
    
    private void initView()
    {
        mSurfaceView = new SurfaceView(this);
        RelativeLayout.LayoutParams surfaceLayout =
            new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        
        mSurfaceView.setLayoutParams(surfaceLayout);
        surfaceLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        surfaceLayout.addRule(RelativeLayout.CENTER_VERTICAL);
        mainLayout.addView(mSurfaceView, 0);
        
        currentTimeText = (TextView)findViewById(R.id.tv_beginTime);
        durationText = (TextView)findViewById(R.id.tv_endtime);
        
        relBuffer.setVisibility(View.GONE);
        textBufferPercent.setVisibility(View.GONE);
        setAllViewEnabled(true);
    }
    
    @Override
    protected void onResume()
    {
        DebugLog.info(TAG, "onResume()");
        //  String dd = null;
        //    DebugLog.info(TAG, "onResume" + dd.length());
        super.onResume();
    }
    
    @Override
    protected void onStop()
    {
        DebugLog.info(TAG, "onStop()");
        super.onStop();
        if (playerController != null)
        {
            playerController.pause();
        }
    }
    
    @Override
    protected void onDestroy()
    {
        DebugLog.info(TAG, "onDestroy()");
        super.onDestroy();
        if (cpuWindow != null)
        {
            cpuWindow.dismiss();
            cpuWindow = null;
        }
        
        handler.removeMessages(PLAYER_PLAYING_UPDATE);
        if (playerController != null)
        {
            playerController.release();
            playerController = null;
        }
        
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        DebugLog.info(TAG, "surfaceCreated");
        if (!inited)
        {
            inited = true;
            init();
        }
        else
        {
            if (playerController != null)
            {
                playerController.resume();
            }
            playerController.setSurfaceView(mSurfaceView);
        }
    }
    
    private void init()
    {
        DebugLog.info(TAG, "init()");
        if (playerController != null)
        {
            playerController.prepare();
        }
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        DebugLog.info(TAG, "surfaceChanged");
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        DebugLog.info(TAG, "surfaceDestroyed");
        if (playerController != null)
        {
            playerController.pause();
        }
    }
    
    @Override
    public void onEnd()
    {
        DebugLog.info(TAG, "onEnd()");
    }
    
    @Override
    public void onFrameSizeChange(Rect rect)
    {
        DebugLog.info(TAG, "onFrameSizeChange()");
    }
    
    @Override
    public void onPositionChange(long positionMilli)
    {
        DebugLog.info(TAG, "onPositionChange:" + positionMilli);
    }
    
    @Override
    public boolean onInfo(InfoCode info, int what, int extra, Object obj)
    {
        DebugLog.info(TAG, "onInfo:" + info);
        return false;
    }
    
    @Override
    public boolean onError(ErrorCode error, Object obj)
    {
        String errStr = error.getMeans();
        
        if (!isFinishing())
        {
            ConfirmDialog.popUp(this, "Play Error:" + errStr, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    PlayerActivity.this.finish();
                }
            });
        }
        
        return false;
    }
    
    @Override
    public void onBuffering(int percent)
    {
        DebugLog.info(TAG, "onBuffering:" + percent + "%");
    }
    
    @Override
    public void onPrepare()
    {
        DebugLog.info(TAG, "onPrepare:");
        if (playerController != null)
        {
            playerController.start();
        }
        
    }
    
    @Override
    public void onPlaying()
    {
        DebugLog.info(TAG, "onPlaying:");
        if (timeCreate > 0)
        {
            handler.sendEmptyMessage(PLAYER_START_TIME_UPDATE);
            handler.sendEmptyMessage(PLAYER_PLAYING_UPDATE);
        }
    }
    
    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case PLAYER_SWITCH_BITRATE:
                    playerController.switchBitrate(Integer.valueOf((String)msg.obj));
                    break;
                
                case PLAYER_PLAYING_UPDATE:
                    updateSeekbar();
                    handler.removeMessages(PLAYER_PLAYING_UPDATE);
                    handler.sendEmptyMessageDelayed(PLAYER_PLAYING_UPDATE, 40);
                    break;
                
                case PLAYER_START_TIME_UPDATE:
                    Toast.makeText(PlayerActivity.this,
                        "onPlay Cost:" + (System.currentTimeMillis() - timeCreate),
                        Toast.LENGTH_LONG).show();
                    timeCreate = 0;
                    break;
            }
        }
        
    };
    
    /** the duration of video */
    private long miDuration = 0;
    
    private void updateSeekbar()
    {
        if (playerController == null)
        {
            return;
        }
        
        if (miDuration <= 0)
        {
            miDuration = playerController.getDuration();
        }
        
        long currentTime = playerController.getCurrentPosition();
        int currentProgress = (int)(((float)currentTime / miDuration) * 1000 + 0.5);
        
        if (miDuration == 0)
        {// it must be TV 
            currentProgress = 1000;
        }
        mSeekBarMoive.setProgress(currentProgress);
        // update current play time ,when video is vod
        currentTimeText.setText(parseSec(currentTime));
        durationText.setText(parseSec(miDuration));
        
        //  updateFaceShow(currentTime);
    }
    
    private String parseSec(long timeMs)
    {
        long totalSeconds = timeMs % 1000 >= 500 ? (timeMs / 1000) + 1 : timeMs / 1000;
        
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    private void switchBitrateMenu()
    {
        List<Integer> videoBandWidthList = playerController.getBitrateList();
        SelectDialog dialog = new SelectDialog(this);
        dialog.setTitle("SwitchBitrate").setIntList(videoBandWidthList);
        String selectedValue = String.valueOf(playerController.getCurrentBitrate());
        dialog.setSelectValue(selectedValue).setNegativeButton("Cancel", null);
        dialog.setHandler(handler, PLAYER_SWITCH_BITRATE);
        dialog.show();
    }
    
    @Override
    public void onClick(View v)
    {
        DebugLog.info(TAG, "onClick:");
        if (controlView.getVisibility() == View.VISIBLE)
        {
            DebugLog.info(TAG, "setGone:");
            controlView.setVisibility(View.GONE);
        }
        else
        {
            DebugLog.info(TAG, "setVisible:");
            controlView.setVisibility(View.VISIBLE);
        }
    }
    
    private String getAppPureMemoryCost()
    {
        ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        int pid = android.os.Process.myPid();
        Debug.MemoryInfo memoryInfo = mActivityManager.getProcessMemoryInfo(new int[] {pid})[0];
        int clean = memoryInfo.getTotalPrivateClean() / 1024;
        int dirty = memoryInfo.getTotalPrivateDirty() / 1024;
        int total = clean + dirty;
        DebugLog.info(TAG, "clean:" + total + " dirty:" + dirty);
        MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(outInfo);
        long freeMemory = outInfo.availMem / (1024 * 1024);
        String ret = total + "M/" + freeMemory + "M  ";
        return ret;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.play_switchhint:
                showAppInfoWindow();
                break;
            
        }
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        boolean ret = false;
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                //Toast.makeText(PlayerActivity.this, "onKeyDown :" + keyCode + "拦截中间键按下", Toast.LENGTH_LONG).show();
                //  onCenterKeyClick();                
                onCenterKeyClick();
                ret = true;
                break;
            
            case KeyEvent.KEYCODE_DPAD_UP:
                //Toast.makeText(PlayerActivity.this, "onKeyDown :" + keyCode + "拦截上键按下", Toast.LENGTH_LONG).show();                
                break;
            
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //Toast.makeText(PlayerActivity.this, "onKeyDown :" + keyCode + "拦截下键按下", Toast.LENGTH_LONG).show();
                break;
            
            case KeyEvent.KEYCODE_DPAD_LEFT:
                onSeekPercent(-1);
                ret = true;
                //Toast.makeText(PlayerActivity.this, "onKeyDown :" + keyCode + "拦截左键按下", Toast.LENGTH_LONG).show();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                onSeekPercent(1);
                ret = true;
                //Toast.makeText(PlayerActivity.this, "onKeyDown :" + keyCode + "拦截右键按下", Toast.LENGTH_LONG).show();                
                break;
            
            case KeyEvent.KEYCODE_BACK:
                if (controlView.getVisibility() == View.VISIBLE)
                {
                    DebugLog.info(TAG, "setGone:");
                    controlView.setVisibility(View.GONE);
                    ret = true;
                }
                else
                {
                    this.finish();
                    ret = true;
                }
                break;
            
            default:
                //Toast.makeText(PlayerActivity.this, "onKeyDown :" + keyCode + "未处理的按键事件", Toast.LENGTH_LONG).show();
                break;
        }
        return ret;
    }
    
    private void onCenterKeyClick()
    {
        if (controlView.getVisibility() != View.VISIBLE)
        {
            DebugLog.info(TAG, "setGone:");
            controlView.setVisibility(View.VISIBLE);
        }
        else
        {
            onPlayButtonClick();
        }
    }
    
    private void onSeekPercent(int percent)
    {
        long duration = playerController.getDuration();
        long dest = playerController.getCurrentPosition() + percent * duration / 100;
        if (dest <= 0)
        {
            dest = 0;
        }
        else if (dest >= duration - 10 * 1000)
        {
            dest = duration - 10 * 1000;
        }
        playerController.seekTo(dest);
        
    }
}