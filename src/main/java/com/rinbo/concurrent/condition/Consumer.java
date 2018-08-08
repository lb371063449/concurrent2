package com.rinbo.concurrent.condition;

class Consumer {
    private Depot depot;
    public Consumer(Depot depot) {
        this.depot = depot;
    }

    public void consume(int no,String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                depot.consume(no);
            }
        }, name + " consume thread").start();
    }
}