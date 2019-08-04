package Utility;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {

    public static String getIPAndHostName(){
        InetAddress netAddress = getInetAddress();
        String ip=getHostIp(netAddress);
        String hostName=getHostName(netAddress);
        System.out.println("host ip:" + ip);
        System.out.println("host name:" + hostName);
        // Properties properties = System.getProperties();
        // Set<String> set = properties.stringPropertyNames(); //获取java虚拟机和系统的信息。
        // for(String name : set){
        //     System.out.println(name + ":" + properties.getProperty(name));
        // }
        return ip+"-"+hostName;
    }

    public static InetAddress getInetAddress(){

        try{
            return InetAddress.getLocalHost();
        }catch(UnknownHostException e){
            System.out.println("unknown host!");
        }
        return null;

    }

    public static String getHostIp(InetAddress netAddress){
        if(null == netAddress){
            return null;
        }
        String ip = netAddress.getHostAddress(); //get the ip address
        return ip;
    }

    public static String getHostName(InetAddress netAddress){
        if(null == netAddress){
            return null;
        }
        String name = netAddress.getHostName(); //get the host address
        return name;
    }

}