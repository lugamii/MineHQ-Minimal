package net.lugami.bridge.global.deployment;

import net.md_5.bungee.api.ProxyServer;
import net.lugami.bridge.BridgeGlobal;
import net.md_5.bungee.BungeeCord;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DeploymentHandler {

    public static void createServer(String serverName, int port, List<File> plugins) {
        BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Finding file");
        File directory = new File(BridgeGlobal.getServerDeploymentDir() + File.separator + serverName);
        BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Creating file");
        directory.mkdir();

        if (sendBunkerserver(directory)) {
            try {
                BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Modifying Bridge's Config");
                PropertiesConfiguration configuration = new PropertiesConfiguration(new File(directory + File.separator + "config" + File.separator + "Bridge" + File.separator + "config.yml"));
                configuration.setProperty("serverName", serverName);
                configuration.setProperty("serverGroup", serverName);
                BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Modified Bridge's Config");
                configuration.save();
                BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Saved Bridge's Config");

                BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Modifying server.properties");
                PropertiesConfiguration conf = new PropertiesConfiguration(new File(directory + File.separator + "config" + File.separator + "server" + File.separator + "server.properties"));
                conf.setProperty("server-port", port);
                conf.setProperty("online-mode", false);
                conf.setProperty("server-name", serverName);
                BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Modified server.properties");
                conf.save();
                BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Saved server.properties");
            } catch (ConfigurationException e) {
                BridgeGlobal.sendLog(e.getMessage());
            }
            BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Sending plugins");
            sendPlugins(directory, plugins, BridgeGlobal::sendLog);
            BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Saving to BungeeCord");
            ProxyServer.getInstance().getServers().put(serverName, ProxyServer.getInstance().constructServerInfo(serverName, new InetSocketAddress("localhost", port), "", false));
            BridgeGlobal.sendLog("[Deployment] \"" + serverName + "\" Completed server deployment");
        }
    }

    public static boolean doesServerExit(String serverName) {
        return new File(BridgeGlobal.getServerDeploymentDir() + File.separator + serverName).exists();
    }

    private static File getTemplateDirectory() {
        return new File(BridgeGlobal.getServerTemplateDir());
    }

    public static void copyFolder(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;

            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            catch (Exception e) {
                try {
                    in.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    out.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static boolean sendBunkerserver(File directory) {
        File file = new File(getTemplateDirectory() + File.separator + "spigot");
        if (!file.exists()) {
            BungeeCord.getInstance().broadcast("Error whilst trying to deploy server to \"" + directory.getName() + "\" template folder not found.");
            return false;
        }
        BridgeGlobal.sendLog("[Deployment] \"" + directory.getName() + "\" Copying Files");
        copyFolder(file, directory);

        BridgeGlobal.sendLog("[Deployment] \"" + directory.getName() + "\" Copied Files");

        return true;
    }

    private static void sendPlugins(File directory, List<File> plugins, Consumer<String> consumer) {
        AtomicInteger success = new AtomicInteger();
        List<File> failed = new ArrayList<>();
        plugins.forEach(file -> {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                File outputFile = new File(directory + File.separator + "plugins" + File.separator + file.getName());
                inputStream = new FileInputStream(file);
                if (outputFile.exists() && !outputFile.delete())
                    return;
                outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0)
                    outputStream.write(buffer, 0, length);
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
        consumer.accept((success.get() == 0) ? "All plugins failed to update, check console for errors!" : (failed.isEmpty() ? ("Successfully updated all plugins (" + success.get() + " plugins)") : ("Updated " + success.get() + " plugins, the following plugins failed to update: " + failed.stream().map(File::getName).collect(Collectors.joining()))));
    }
}
