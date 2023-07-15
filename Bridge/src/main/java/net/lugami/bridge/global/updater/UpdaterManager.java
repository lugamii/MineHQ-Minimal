package net.lugami.bridge.global.updater;

import net.lugami.bridge.BridgeGlobal;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UpdaterManager {


    public String getPluginUpdateDir() {
        String dir;
        if(BridgeGlobal.getPluginUpdateDir() == null || BridgeGlobal.getPluginUpdateDir().isEmpty()) {
            dir = "%user_home%/Bridge/updater";
        }else {
            dir = BridgeGlobal.getPluginUpdateDir();
        }
        return dir.replace("%user_home%", System.getProperty("user.home"));
    }

    public File getPluginDirFile() {
        File f = new File(getPluginUpdateDir());

        if(!f.exists() || !f.isDirectory()) {
            boolean b = f.mkdir();
            if(!b) return null;
        }

        return f;
    }

    public List<File> getFilesForGroup(String group) {
        return getFilesForGroup(Collections.singletonList(group));
    }

    public List<File> getFilesForGroup(List<String> group) {
        List<File> files = new ArrayList<>();

        if(group.contains("ALL")) {

            for (File file : getPluginDirFile().listFiles()) {
                if(file.isFile()) continue;
                for (File listFile : file.listFiles()) {
                    if(listFile.isFile()) {
                        files.add(listFile);
                    }
                }
            }

            return files;
        }


        /*/ Global Files /*/

        File folder = new File(getPluginUpdateDir() + "/Global");
        if(!folder.exists() || !folder.isDirectory()) return null;


        for (File file : folder.listFiles()) {
            if(file.isFile()) {
                files.add(file);
            }
        }

        /*/ Group Files /*/

        for (String s : group) {
            folder = new File(getPluginUpdateDir() + "/" + s);
            if(!folder.exists() || !folder.isDirectory()) continue;


            for (File file : folder.listFiles()) {
                if(file.isFile()) {
                    files.add(file);
                }
            }
        }

        return files;
    }

    public String getRunningDirectory() {
        try {
            return BridgeGlobal.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replace("/Bridge.jar", "");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UpdateStatus getStatus(File file) {
        String runningDir = getRunningDirectory();
        if(runningDir == null) {
            return UpdateStatus.ERROR;
        }

        File selectedFile = new File(runningDir + "/" + file.getName());
        if(!selectedFile.exists()) {
            return UpdateStatus.NOT_INSTALLED;
        }else {
            if(selectedFile.length() != file.length()) {
                return UpdateStatus.NEW_UPDATE;
            }else {
                return UpdateStatus.LATEST;
            }
        }
    }

    public void updatePlugins(List<File> plugins, Consumer<String> consumer) {

        AtomicInteger success = new AtomicInteger();
        List<File> failed = new ArrayList<>();

        plugins.forEach(file -> {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                File outputFile = new File(getRunningDirectory() + "/" + file.getName());
                inputStream = new FileInputStream(file);
                if(outputFile.exists()) if(!outputFile.delete()) return;

                outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0 ,length);
                }
                success.getAndIncrement();
            } catch (IOException e) {
                e.printStackTrace();
                failed.add(file);
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        consumer.accept(success.get() == 0 ? "All plugins failed to update, check console for errors!" : (failed.isEmpty() ? "Successfully updated all plugins (" + success.get() + " plugins)" : "Updated " + success.get() + " plugins, the following plugins failed to update: " + failed.stream().map(File::getName).collect(Collectors.joining())));

    }


}