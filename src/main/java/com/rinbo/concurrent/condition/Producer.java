package com.rinbo.concurrent.condition;

class Producer {
    private Depot depot;
    public Producer(Depot depot) {
        this.depot = depot;
    }

    public void produce(int no,String name) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                depot.produce(no);
            }
        }, name + " produce thread").start();
    }
}

