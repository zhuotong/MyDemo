package com.zhuo.tong.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 项目名称：My Application
 * 类描述：
 * 创建人：苏格
 * 创建时间：2016/4/16 10:37
 * 修改人：苏格
 * 修改时间：2016/4/16 10:37
 * 修改备注：
 */
public class MyRootTool {

    /**
     * 获取已经得到root权限的进程，在其他地方自己处理输入命令等
     * @return null为获取root失败
     */
    public static void getRootProcess(final MyHolder holder){
        MyThreadPool.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MyTestLog.info("getRootProcess");
                    //以前一直以为执行这行代码跳出权限选择时是阻塞的，不过经测试并不阻塞；只有执行读数据流的操作才阻塞，意思就是如果用户还没有拒绝root权限或读不到数据，那么会等待，
                    //而如果拒绝root会返回-1，后面其他代码还会执行；而写数据流不阻塞，在root管理程序还没回复或者是还没给root的过程中应该是写入缓冲区了，而给了root后会自动执行缓存中
                    //的命令还是由下一条命令写入时一起执行不得而知。
                    //因为写数据流不阻塞，所以如果拒绝root那么su后第一个读数据流后的写数据流都会IOException
                    //经过测试Thread.sleep(1000);那么默认设置为拒绝root的写数据流会io异常，读数据流返回-1;
                    //等待用户设置root的时候如果能在1秒内设置拒绝，那么同上。
                    //所以得出的结论：最好还是在关键代码前加上写读echo $?来判断是否已经设置好了root，因为即使是设置了默认root，也和root管理程序交互有时间差，所以用读阻塞一下刚好。
                    holder.process = Runtime.getRuntime().exec("su");
                    OutputStream outputStream = holder.process.getOutputStream();
                    InputStream inputStream = holder.process.getInputStream();
                    outputStream.write("echo $?\n".getBytes());
                    /*int read = -1;
                    while ((read=inputStream.read())!=-1)
                        MyTestLog.info("read:"+read);*/
                    String read = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                    if(!TextUtils.isEmpty(read)){
                        holder.wait = Integer.parseInt(read);
                        MyTestLog.info("echo $?:"+holder.wait);
                        if(holder.wait == 0)
                            holder.success();
                        else
                            holder.fail();
                    }else {
                        holder.fail();
                    }

                } catch (IOException e) {//出现这个错误是找不到su文件或拒绝root后写入数据流
                    MyTestLog.info("getRootProcess IOException");
                    holder.fail();
                    e.printStackTrace();
                    if(holder.process != null)
                        holder.process.destroy();
                }
            }
        });
    }

    /**
     * 直接执行命令,非root
     * @param exec
     * @return
     */
    public static void exec(final String exec, final MyHolder holder) {
        MyThreadPool.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    holder.process = Runtime.getRuntime().exec(exec);
                    int wait = holder.process.waitFor();
                    MyTestLog.info("waitFor:" + wait);
                    holder.result = wait == 0;
                    if (holder.result){
                        holder.success();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (holder.process != null) {
                        holder.process.destroy();
                    }
                }
                holder.fail();
            }
        });
    }

    /**
     * 开启一个sh执行命令
     * @param exec
     * @return
     */
    public static void execSh(final String exec, final MyHolder holder) {
        MyThreadPool.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    holder.process = Runtime.getRuntime().exec("sh");
                    OutputStream outputStream = holder.process.getOutputStream();
                    outputStream.write(exec.getBytes());
                    outputStream.write("\n".getBytes());
                    outputStream.write("exit\n".getBytes());//必须\n
                    holder.wait = holder.process.waitFor();
                    MyTestLog.info("waitFor:" + holder.wait);
                    holder.result = holder.wait == 0;
                    if(holder.result){
                        holder.success();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    if (holder.process != null) {
                        holder.process.destroy();
                    }
                }
                holder.fail();
            }
        });
    }


    /**
     * 以root执行传入的命令
     * @param exec
     * @return
     */
    public static void execRoot(final String exec, final MyHolder holder){
        MyThreadPool.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    holder.process = Runtime.getRuntime().exec("su");
                    OutputStream outputStream = holder.process.getOutputStream();
                    outputStream.write(exec.getBytes());
                    outputStream.write("\n".getBytes());
                    outputStream.write("exit\n".getBytes());//必须\n
                    holder.wait = holder.process.waitFor();
                    MyTestLog.info("waitFor:" + holder.wait);
                    holder.result = holder.wait == 0;
                    if(holder.result){
                        holder.success();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    if (holder.process != null) {
                        holder.process.destroy();
                    }
                }
                holder.fail();
            }
        });
    }

    /**
     * 检测是否能获取到root
     * @return
     */
    public static void checkRoot(){
        final MyHolder holder = new MyHolder();
        MyThreadPool.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                holder.result = checkRootSimple();
                MyTestLog.info("root:"+holder.result);
            }
        });
    }

    public static boolean checkRootSimple(){
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            OutputStream outputStream = process.getOutputStream();
            outputStream.write("exit\n".getBytes());//必须加\n
            int wait = process.waitFor();
            MyTestLog.info("waitFor:" + wait);
            return wait == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    public static boolean checkRootComplex() {
        BufferedReader in = null;
        OutputStream out = null;
        BufferedReader error = null;
        int rootResult = 1;
        String resultError = null;

        try {
            //事实证明靠waitFor()返回值是不对的，因为这个只是最后一条命令执行后的返回值。
            //上面的解释，是除了exit之前的那行命令的返回值。注意一定要有exit不然线程阻塞在这里。
            //还有注意\n，多条命令加\n才能分开执行。另外"exit 1"这样那么waitFor()返回值就是1
            //我明白了,如果是su或者sh,cmd这种能开启一个命令终端的命令,执行后才能后续输入执行命令,其他的命令都是一次性的,输入也没用
            //测试如果没有权限,或者这里拒绝权限,下面的write就不执行了(应该是开启的子线程结束了,输入的命名也没人执行)
            //晕，不是不执行了，而是报异常了没看到，java.io.IOException: write failed: EPIPE (Broken pipe)
            //说明su执行出错，后面的写入就不正确了。
            Process process = Runtime.getRuntime().exec("su");
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            out = process.getOutputStream();
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String result = null;
            Log.e("zhuo", "1");
            out.write("echo $?\n".getBytes());
            out.flush();
            Log.e("zhuo", "2");
            result = in.readLine();
            Log.e("zhuo", "3");
            if (!TextUtils.isEmpty(result)) {
                Log.e("zhuo", "4+" + result);
                rootResult = Integer.parseInt(result);
                out.write("exit\n".getBytes());
                out.flush();
                process.waitFor();//不写这行直接结束会有异常，虽然是info级别的好像是这个子线程抛的异常，也不影响程序结果，但是不是好习惯
                process.destroy();
//				Log.e("zhuo", "4.5+"+result);
                return rootResult == 0;
            }
            Log.e("zhuo", "5");
            resultError = error.readLine();
            Log.e("zhuo", "6");
            if (!TextUtils.isEmpty(resultError)) {
                Log.e("zhuo", "7+" + resultError);
                process.waitFor();
                process.destroy();
                return false;
            }
            Log.e("zhuo", "8");
            out.write("exit\n".getBytes());
            out.flush();
            Log.e("zhuo", "9");
            return process.waitFor() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (error != null) {
                try {
                    error.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return false;
    }

    public static class MyHolder{
        public boolean result = false;
        public int wait = -1;
        public Process process = null;

        public void success(){};
        public void fail(){};
    }
}