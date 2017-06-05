package org.red.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 全局配置类
 * Created by JRed on 2017/6/4.
 */
public class Config implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private Map<String,Long> configFileModifyDate = new HashMap<String, Long>();

    private static Map<String,String> SYSTEM_CONFIG = new ConcurrentHashMap<String, String>();

    private static String DEFAULT_CHARSET="UTF-8";

    private int checkDely = 5*1000;  //配置文件自动检查间隔

    private int beginDely = 60*1000; //1分钟后运行配置文件自动检查功能

    private boolean isCheck = true;


    @Override
    public void run() {

        try{
            Thread.sleep(beginDely);
            LOGGER.info("启动配置文件检查线程，当前检查频率:"+checkDely+"ms");
        }catch (InterruptedException e){
            LOGGER.error(e.getMessage(),e);
        }

        while (isCheck){
            this.loadAllConfigFiles();
        }

    }

    private void loadAllConfigFiles(){
        File rootDir = new File(getResource("/conf"));
        rootDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                String fullPath = pathname.getAbsolutePath();
                if(name.matches("^config.*\\.properties$")){
                    Long value = configFileModifyDate.get(fullPath);
                    if(value == null || value.longValue()!=pathname.lastModified()){
                        LOGGER.info("加载配置文件:"+pathname);
                        loadPropertiesFiles(pathname);
                        configFileModifyDate.put(fullPath,pathname.lastModified());
                    }
                }
                return false;
            }
        });

    }


    private void loadPropertiesFiles(File configFile){
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(configFile));
            for(String key : properties.stringPropertyNames()){
                String value = properties.getProperty(key);
                Config.SYSTEM_CONFIG.put(key,value);
                LOGGER.info("load property:"+key+"->"+value);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }


    }

    private String getResource(String path){
       URL uri =  this.getClass().getResource(path);
        return uri.getPath();
    }

    public static String getProperty(String key){
        if(key!=null&&!"".equals(key.trim())){
            return SYSTEM_CONFIG.get(key);
        }else{
            return null;
        }
    }

    private void init(){
        LOGGER.info("启动系统配置文件监听线程.....");
        new Thread(this,"System-Config-check-thread").start();
    }

}
