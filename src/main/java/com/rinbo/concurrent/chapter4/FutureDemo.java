package com.rinbo.concurrent.chapter4;

public class FutureDemo {
    interface Data {
        String getResult();
    }
    //代理数据
    static class FutureData implements Data {
        private Data realData;
        private boolean isReady;

        public synchronized void setRealData(Data realData) {
            if (isReady) {
                return;
            }
            this.realData = realData;
            isReady = true;
            //通知阻塞在获取数据上的线程，数据准备就绪
            notifyAll();
        }
        public synchronized String getResult() {
            //如果数据没有准备好，则等待
            if (!isReady) {
                try {
                    //如果真实的数据没有准备好，当前线程
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return realData.getResult();
        }
    }
    //真实数据
    static class RealData implements Data {
        private final String result;
        public RealData(String para) {
            StringBuffer sb = new StringBuffer();
            for (int i=0;i<10;i++) {
                sb.append(para);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result = sb.toString();
        }

        public String getResult() {
            return result;
        }
    }
    //客户端，用于请求数据
    static class Client {
        public Data requestData(String para) {
            FutureData futureData = new FutureData();
            //通过线程从后台获取真实数据
            new Thread() {
                public void run() {
                    RealData realData = new RealData(para);
                    //将真实数据设置带代理数据中
                    futureData.setRealData(realData);
                }
            }.start();
            //立即返回代理数据
            return futureData;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        Data data = client.requestData("name");
        System.out.println("请求完毕");
        Thread.sleep(1000);
        System.out.println("真实数据：" + data.getResult());
    }
}
