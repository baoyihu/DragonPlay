package com.baoyihu.dragonplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.simpleframework.xml.core.PersistenceException;

import com.baoyihu.common.util.DebugLog;
import com.baoyihu.common.util.Files;
import com.baoyihu.common.util.HttpConnector;
import com.baoyihu.dragonfly.url.Media;
import com.baoyihu.dragonfly.url.MediaList;
import com.baoyihu.dragonfly.xml.SerializerService;
import com.baoyihu.dragonplayer.R.color;
import com.baoyihu.mem.MemService;
import com.baoyihu.mem.MemService.ProgressInterface;
import com.baoyihu.mem.node.Category;
import com.baoyihu.util.NetworkController;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
    public static final String CODE_TYPE = "UTF-8";
    
    private static final String TAG = "MainActivity";
    
    private static String localServerIP = null;
    
    //   private static final String uploadUrl = localServerIP + ":8080/crashlog";
    
    private ListView mediaList;
    
    private List<Media> tempPlayList = new ArrayList<Media>();
    
    private RelativeLayout relBuffer = null;
    
    private TextView waittingText = null;
    
    private UncaughtExceptionHandler defaultHandler = null;
    
    private MemService memService = null;
    
    private FileManagerAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        DebugLog.debug(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        setContentView(R.layout.main);
        
        relBuffer = (RelativeLayout)findViewById(R.id.waiting_icon);
        waittingText = (TextView)findViewById(R.id.waiting_text);
        mediaList = (ListView)findViewById(R.id.url_list);
        mediaList.setOnItemClickListener(itemClickListener);
        mediaList.setSelector(color.blue);
        request();
        memService = new MemService();
        DebugLog.debug(TAG, "onCreate()1");
        
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread thread, Throwable ex)
            {
                DebugLog.debug(TAG, "uncaughtException one:");
                String sdkVersion = "androidVersion:" + android.os.Build.VERSION.SDK + "\r\n";
                String crashString = DebugLog.getExceptinMessage(TAG, ex);
                NetworkController.postBuffer(localServerIP + ":8080/crashlog", sdkVersion + crashString);
                defaultHandler.uncaughtException(thread, ex);
            }
        });
        if (localServerIP == null)
        {
            getLocalServerIp();
        }
        
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        memService.release();
        memService = null;
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            case R.id.action_loadfromlog:
                loadUrlFromLog("/sdcard/HiMovie/CBGLog");
                break;
            
            case R.id.action_loadfromhimovie_dash:
                //getUrlFromLocalServer(localServerIP + ":8080/HMS/DASH/availableDASH.xml", true);
                getUrlFromHimovie(DragonApplication.DASH_TYPE);
                break;
            
            case R.id.action_loadfromhimovie_hls:
                //getUrlFromLocalServer(localServerIP + ":8080/HMS/HLS/availableHLS.xml", true);
                getUrlFromHimovie(DragonApplication.HLS_TYPE);
                break;
            
            case R.id.action_loadfromlocalserver_dash:
                getUrlFromLocalServer(localServerIP + ":8080/HMS/DASH/VOD/index.xml", false);
                break;
            
            case R.id.action_loadfromlocalserver_hls:
                getUrlFromLocalServer(localServerIP + ":8080/HMS/HLS/VOD/index.xml", false);
                break;
            
            case R.id.action_settings:
                break;
        }
        return true;
    }
    
    private void getLocalServerIp()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                
                String head = "http://192.168.1.";
                String ret = null;
                for (int iLoop = 50; iLoop < 100; iLoop++)
                {
                    HttpURLConnection connection = null;
                    try
                    {
                        String ipAddress = head + iLoop;
                        URL url = new URL(ipAddress + ":8080/ping.txt");
                        connection = (HttpURLConnection)(url.openConnection());
                        connection.setConnectTimeout(50);
                        connection.setRequestMethod("GET");
                        connection.setDoInput(true);
                        int code = connection.getResponseCode();
                        if (code == 200)
                        {
                            DebugLog.debug(TAG, "address:" + ipAddress + " isReachable:true");
                            ret = ipAddress;
                            break;
                        }
                        else
                        {
                            DebugLog.debug(TAG, "address:" + ipAddress + " isReachable:false");
                        }
                    }
                    catch (Exception e)
                    {
                        DebugLog.trace(TAG, e);
                    }
                    finally
                    {
                        if (connection != null)
                        {
                            connection.disconnect();
                            connection = null;
                        }
                    }
                }
                localServerIP = ret;
                waittingText.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast toast =
                            Toast.makeText(MainActivity.this, "get server url:" + localServerIP, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        }).start();
        
    }
    
    private void getUrlFromLocalServer(final String indexPath, boolean isCompletePath)
    {
        relBuffer.setVisibility(View.VISIBLE);
        waittingText.setText("开始下载,请等待!!! ");
        
        final String rootPath = isCompletePath ? "" : indexPath.substring(0, indexPath.lastIndexOf('/') + 1);
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String mediaString = HttpConnector.getData(indexPath, CODE_TYPE);
                
                if (mediaString != null)
                {
                    MediaList list;
                    try
                    {
                        list = SerializerService.fromXml(MediaList.class, mediaString);
                        List<Media> rawList = list.getList();
                        for (Media temp : rawList)
                        {
                            temp.setUrl(rootPath + temp.getUrl());
                        }
                        tempPlayList.clear();
                        tempPlayList.addAll(rawList);
                        mediaList.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                relBuffer.setVisibility(View.INVISIBLE);
                                notifyDataRefresh();
                            }
                        });
                    }
                    catch (PersistenceException e)
                    {
                        DebugLog.trace(TAG, e);
                    }
                    
                }
                
            }
        }).start();
        
    }
    
    private void getUrlFromHimovie(final String videoType)
    {
        relBuffer.setVisibility(View.VISIBLE);
        waittingText.setText("开始下载,请等待!!! ");
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (memService.logIn())
                {
                    final List<Category> totalList = memService.getAllCategoryList(videoType);
                    mediaList.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            openCategoryDialog(totalList, videoType);
                        }
                    });
                }
            }
        }).start();
    }
    
    private int openCategoryDialog(final List<Category> list, final String videoType)
    {
        Builder decodeDialog = new AlertDialog.Builder(this);
        decodeDialog.setTitle("选择类型");
        String[] decodeListStr = new String[list.size()];
        
        for (int i = 0; i < decodeListStr.length; i++)
        {
            decodeListStr[i] = list.get(i).getName();
        }
        
        decodeDialog.setSingleChoiceItems(decodeListStr, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String categoryId = list.get(which).getCategoryId();
                getCategoryVods(list, categoryId, videoType);
                dialog.dismiss();
            }
            
        });
        decodeDialog.setNegativeButton("Cancel", null);
        decodeDialog.show();
        return 0;
    }
    
    private void getCategoryVods(final List<Category> list, final String categoryId, final String videoType)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                List<Media> himovieVods = memService.downloadCategroyUrl(categoryId, new ProgressInterface()
                {
                    @Override
                    public void onPercentChange(final int percent)
                    {
                        mediaList.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                waittingText.setText("开始下载,请等待!!!\r\n " + percent + "%");
                            }
                        });
                    }
                }, videoType);
                saveUrl(himovieVods);
                tempPlayList.clear();
                tempPlayList.addAll(himovieVods);
                mediaList.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        relBuffer.setVisibility(View.INVISIBLE);
                        notifyDataRefresh();
                    }
                });
            }
        }).start();
    }
    
    private void saveUrl(List<Media> himovieVods)
    {
        MediaList sheet = new MediaList(himovieVods);
        try
        {
            String xmlString = SerializerService.toXml(sheet, "UTF-8");
            Files.writeFiles("/sdcard/url.xml", xmlString);
        }
        catch (Exception e)
        {
            DebugLog.trace(TAG, e);
        }
        
    }
    
    private void notifyDataRefresh()
    {
        List<Map<String, String>> adpterList = MediaList.toMapList(tempPlayList);
        adapter = new FileManagerAdapter(MainActivity.this, adpterList, R.layout.file_list,
            new String[] {"name", "type", "url"}, new int[] {R.id.name, R.id.type, R.id.url}, true);
        
        mediaList.setAdapter(adapter);
        DebugLog.debug(TAG, "notifyDataSetChanged in MainActivity");
        adapter.notifyDataSetChanged();
    }
    
    private void loadUrlFromLog(String logDir)
    {
        File dir = new File(logDir);
        Map<String, String> map = new TreeMap<String, String>();
        if (dir.exists())
        {
            File[] files = dir.listFiles();
            if (files != null)
            {
                for (File temp : files)
                {
                    map.putAll(getUrlFromFile(temp));
                }
            }
        }
        
        tempPlayList.clear();
        for (Entry<String, String> entry : map.entrySet())
        {
            Media media = new Media();
            media.setName(entry.getKey());
            media.setType("0");
            media.setUrl(entry.getValue());
            tempPlayList.add(media);
        }
        List<Map<String, String>> adpterList = MediaList.toMapList(tempPlayList);
        adapter = new FileManagerAdapter(MainActivity.this, adpterList, R.layout.file_list,
            new String[] {"name", "type", "url"}, new int[] {R.id.name, R.id.type, R.id.url}, true);
        mediaList.setAdapter(adapter);
        DebugLog.debug(TAG, "notifyDataSetChanged in MainActivity");
        adapter.notifyDataSetChanged();
    }
    
    private Map<String, String> getUrlFromFile(File file)
    {
        BufferedReader reader = null;
        String line = null;
        Map<String, String> map = new TreeMap<String, String>();
        String pre = file.getName();
        int index = 1;
        try
        {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null)
            {
                if (line.contains("setDataSource()"))
                {
                    int begin = line.indexOf("http:");
                    if (begin > 0)
                    {
                        String url = line.substring(begin);
                        if (url.contains(" ") || url.contains(","))
                        {
                            //Trim to
                        }
                        
                        map.put(pre + "_" + index++, url);
                    }
                }
            }
        }
        catch (IOException e)
        {
            DebugLog.trace(TAG, e);
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    DebugLog.trace(TAG, e);
                }
            }
        }
        return map;
    }
    
    private void request()
    {
        if (VERSION.SDK_INT >= 23
            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);//ActivityCompat,MainActivity.this,
            return;
        }
        else
        {
            loadData();
        }
    }
    
    private void loadData()
    {
        tempPlayList = MediaList.parseFile(MediaList.FILEPATH);
        
        if (tempPlayList.isEmpty())
        {
            Toast toast = Toast.makeText(this, "no url.xml", Toast.LENGTH_LONG);
            toast.show();
        }
        
        List<Map<String, String>> adpterList = MediaList.toMapList(tempPlayList);
        adapter = new FileManagerAdapter(MainActivity.this, adpterList, R.layout.file_list,
            new String[] {"name", "type", "url"}, new int[] {R.id.name, R.id.type, R.id.url}, true);
        mediaList.setAdapter(adapter);
        DebugLog.debug(TAG, "notifyDataSetChanged in MainActivity");
        adapter.notifyDataSetChanged();
        
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 123:
                DebugLog.debug(TAG, "we geti it" + grantResults[0]);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    loadData();
                }
                break;
        }
        
    }
    
    private OnItemClickListener itemClickListener = new OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
        {
            startToPlay(arg2);
        }
    };
    
    private void startToPlay(int index)
    {
        Intent intent = new Intent();
        intent.putExtra("media", tempPlayList.get(index));
        intent.setClass(MainActivity.this, PlayerActivity.class);
        startActivity(intent);
    }
}
