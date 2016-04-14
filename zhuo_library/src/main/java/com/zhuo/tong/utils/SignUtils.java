package com.zhuo.tong.utils;
import java.text.SimpleDateFormat;  
import java.util.ArrayList;  
import java.util.Date;  
  
import android.app.Activity;  
import android.os.Bundle;  
import android.util.Log;
import android.widget.Toast;  
import android.content.Context;  
public class SignUtils {
	//获取手机中所有安装的APK签名信息  
    public static void getInstallApkSign(Context context) {  
        android.content.pm.PackageManager pm = context.getPackageManager();  
        java.util.List<android.content.pm.PackageInfo> apps = pm  
                .getInstalledPackages(android.content.pm.PackageManager.GET_SIGNATURES);  
        java.util.Iterator<android.content.pm.PackageInfo> iter = apps.iterator();         
          
        ArrayList<String> listText=new ArrayList<String>();  
          
        int i=0;  
        while (iter.hasNext()) {  
            try{  
                android.content.pm.PackageInfo info = iter.next();  
                  
                String packageName = info.packageName;  
                String versionName = info.versionName;  
                String versionCode = ""+info.versionCode;                           
                String gameName=info.applicationInfo.loadLabel(pm).toString();  
                  
                byte signature[]=info.signatures[0].toByteArray();                  
                java.security.cert.CertificateFactory certFactory = java.security.cert.CertificateFactory  
                        .getInstance("X.509");  
                java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) certFactory  
                        .generateCertificate(new java.io.ByteArrayInputStream(  
                                signature));  
                  
                String pubKey = cert.getPublicKey().toString();  
                String signNumber = cert.getSerialNumber().toString();  
                  
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
                String dateAfter=sdf.format(cert.getNotAfter());  
                String dateBefore=sdf.format(cert.getNotBefore());  
                  
                int version=cert.getVersion();            
                String type=cert.getType();  
                  
                  
                StringBuilder sb=new StringBuilder();  
                  
                sb.append("==============").append("\r\n");  
                sb.append("==============").append("\r\n");                  
                sb.append("第"+i+"个,游戏名字:【"+gameName+"】__packageName:【"+packageName+"】___versionName:"+versionName+"____versionCode:"+versionCode).append("\r\n");                 
                sb.append("parseSignature==========开始日期:"+dateBefore+"___有效结束日期:" +dateAfter).append("\r\n");  
                sb.append("parseSignature==========version:"+version+"___type:"+type).append("\r\n");             
                sb.append("parseSignature==========signName:" + cert.getSigAlgName()).append("\r\n");  
                sb.append("parseSignature==========pubKey:" + pubKey).append("\r\n");  
                sb.append("parseSignature==========signNumber:" + signNumber).append("\r\n");  
                sb.append("parseSignature==========subjectDN:" + cert.getSubjectDN().toString()).append("\r\n");  
                  
                listText.add(sb.toString()); 
                Log.e("zhuo", sb.toString());
                sb=null;  
                  
                ++i;      
            }catch (Exception e) {  
                // TODO: handle exception  
                System.out.println("EX:"+e.toString());  
            }  
              
        }  
        
      //保存信息到文件。  
        StringBuilder sbWriteText=new StringBuilder();  
        if(listText!=null&&listText.size()>0)  
        {  
            for(int j=0;j<listText.size();j++)  
            {  
                sbWriteText.append(listText.get(j));  
            }  
        }          
          
        String ss=sbWriteText.toString();  
        writePackageInfoToSdcard(ss);  
        sbWriteText=null;       
    }
    
    public static boolean writePackageInfoToSdcard(String text)  
    {  
        boolean isOk=false;  
        if(text==null)  
        {  
            return isOk;  
        }  
        String fileName="App_Infor_"+getCurrentData("yyyy-MM-ddHHmmss")+".txt";  
        try {  
            isOk=writeLiveToSdcard(fileName, text.getBytes("UTF-8"));  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        fileName=null;  
        return isOk;  
    } 
    
    private static String getCurrentData(String timeType){        
        SimpleDateFormat sdf=new SimpleDateFormat(timeType);  
        return sdf.format(new Date());  
    }  
      
      
    private static boolean writeLiveToSdcard(String fileName,byte files[])  
    {  
        try {  
            String sdcardState=android.os.Environment.getExternalStorageState();      
            if (sdcardState.equals(  
                    android.os.Environment.MEDIA_MOUNTED))  
            {  
                String sdcardAssFilePath=android.os.Environment.getExternalStorageDirectory().getPath()+"/age_install_app/"+fileName;  
                java.io.File file=new java.io.File(sdcardAssFilePath);  
                java.io.File parent = file.getParentFile();  
                if(!parent.exists()) {  
                    file.getParentFile().mkdirs();  
                }  
                java.io.FileOutputStream fos=new java.io.FileOutputStream(file);              
                fos.write(files);  
                fos.flush();  
                fos.close();  
                fos = null;  
                file=null;  
                System.out.println("存储APP信息文件成功");  
                return true;  
            }  
            else if(sdcardState.equals(android.os.Environment.MEDIA_REMOVED))  
            {  
                  
                System.out.println("存储APP信息 ,不存在sdcard卡,写入失败");  
            }  
            else  
            {     
                System.out.println("存储APP信息 ,当前SDcard状态为:《"+sdcardState+"》写入失败");  
            }             
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return false;         
    }  
    
   
    /**
     * 获取APK签名信息,byte数组，前提是该应用已安装，不过这个方法简直是扯渎子的方法，请忽略！
     * 因为他在已安装的apk里面遍历，而获取的包名的是从传入的context获取的，我想问如果这个程序执行了，
     * 难道这个apk还没安装？太多余了。
     * @param context
     * @return
     */
    public static byte[] getApkSign(Context context) {  
        android.content.pm.PackageManager pm = context.getPackageManager();  
        java.util.List<android.content.pm.PackageInfo> apps = pm  
                .getInstalledPackages(android.content.pm.PackageManager.GET_SIGNATURES);  
        java.util.Iterator<android.content.pm.PackageInfo> iter = apps.iterator();         
          
        while (iter.hasNext()) {  
            android.content.pm.PackageInfo info = iter.next();  
            String packageName = info.packageName;  
            //System.out.println("packageName:"+packageName+"___I:"+i);          
            //按包名 取签名  
            if (packageName.equals(context.getPackageName())) {  
                return info.signatures[0].toByteArray();  
  
            }               
        }  
        return null;  
    } 
    
    /** 
    * 得到任意apk签名信息  
    * 如果当前运行的软件要得到自身的公钥信息，只需要得到apk位置就可以了（每个软件（非内置）安装后，都会在/data/system里保存一份apk文件） 
    * String path = context.getApplicationInfo().publicSourceDir 
    *  
    * 此方法为依赖反射隐藏API的方法 
    *  
    * 安卓系统的分裂版本过多，并且不同厂商进行的修改很多，依赖反射隐藏API的方法并不能保证兼容性和通用性，因此推荐使用JAVA自带API进行获取 
    *                       
    * @param apkPath 
    * @return 
    * @throws Exception 
    */  
   public  static byte[] getApkSignature(Context context,String apkPath) throws Exception {  
       Class<?> clazz = Class.forName("android.content.pm.PackageParser");  
       java.lang.reflect.Method parsePackageMethod = clazz.getMethod("parsePackage", java.io.File.class, String.class, android.util.DisplayMetrics.class, int.class);  
 
       Object packageParser = clazz.getConstructor(String.class).newInstance("");  
       Object packag = parsePackageMethod.invoke(packageParser, new java.io.File(apkPath), null,context.getResources().getDisplayMetrics(), 0x0004);  
 
       java.lang.reflect.Method collectCertificatesMethod = clazz.getMethod("collectCertificates", Class.forName("android.content.pm.PackageParser$Package"), int.class);  
       collectCertificatesMethod.invoke(packageParser, packag, android.content.pm.PackageManager.GET_SIGNATURES);  
       android.content.pm.Signature mSignatures[] = (android.content.pm.Signature[]) packag.getClass().getField("mSignatures").get(packag);  
 
       android.content.pm.Signature apkSignature = mSignatures.length > 0 ? mSignatures[0] : null;  
       if(apkSignature!=null)  
       {  
           return apkSignature.toByteArray();  
       }  
       else  
       {  
           return null;      
       }         
   }  
   
 
   /**
    * 根据签名byte[]签名信息，获取APK公钥  
 * @param signature
 * @return
 */
   public static String getPublicKey(byte[] signature) {  
	   try {  
		   java.security.cert.CertificateFactory certFactory = java.security.cert.CertificateFactory  
				   .getInstance("X.509");  
		   java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) certFactory  
				   .generateCertificate(new java.io.ByteArrayInputStream(signature));  

		   String publickey = cert.getPublicKey().toString();  
		   publickey = publickey.substring(publickey.indexOf("modulus: ") + 9,  
				   publickey.indexOf("\n", publickey.indexOf("modulus:")));  


		   return publickey;  
	   } catch (java.security.cert.CertificateException e) {  
		   e.printStackTrace();  
	   }  
	   return null;  
   }  
   public static void getSingInfo(Context context) {  
	   try {  
		   android.content.pm.PackageInfo packageInfo = context  
				   .getPackageManager().getPackageInfo(  
						   context.getPackageName(),  
						   android.content.pm.PackageManager.GET_SIGNATURES);  
		   android.content.pm.Signature[] signs = packageInfo.signatures;  
		   android.content.pm.Signature sign = signs[0];  
		   parseSignature(sign.toByteArray());  
	   } catch (Exception e) {  
		   e.printStackTrace();  
	   }  
   } 
   
   private static void parseSignature(byte[] signature) {  
       try {  
           java.security.cert.CertificateFactory certFactory = java.security.cert.CertificateFactory  
                   .getInstance("X.509");  
           java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) certFactory  
                   .generateCertificate(new java.io.ByteArrayInputStream(  
                           signature));  
           String pubKey = cert.getPublicKey().toString();  
           String signNumber = cert.getSerialNumber().toString();  
             
           SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
           String dateAfter=sdf.format(cert.getNotAfter());  
           String dateBefore=sdf.format(cert.getNotBefore());  
             
             
             
           int version=cert.getVersion();            
           String type=cert.getType();  
             
             
             
           System.out.println("parseSignature==========开始日期:"+dateBefore+"___有效结束日期:" +dateAfter);  
           System.out.println("parseSignature==========version:"+version+"___type:"+type);           
           System.out.println("parseSignature==========signName:" + cert.getSigAlgName());  
           System.out.println("parseSignature==========pubKey:" + pubKey);  
           System.out.println("parseSignature==========signNumber:" + signNumber);  
           System.out.println("parseSignature==========subjectDN:" + cert.getSubjectDN().toString());  
 
       } catch (java.security.cert.CertificateException e) {  
           e.printStackTrace();  
       }  
   }  

}
