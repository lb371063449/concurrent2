package com.rinbo.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WordCountTopology {
	
	/**
     *
     * Spout是Stream的消息产生源，Spout组件的实现可以通过继承BaseRichSpout类或者其他*Spout类来完成，也可以通过实现IRichSpout接口来实现
	 */
	public static class RandomSentenceSpout extends BaseRichSpout {
		
		private static final Logger LOGGER = LoggerFactory.getLogger(RandomSentenceSpout.class);

		private SpoutOutputCollector collector;
		private Random random;
		
		/**
         *
		 * open方法，是对spout进行初始化的，比如说创建一个线程池，或者创建一个数据库连接池，或者构造一个httpclient
		 */
		@Override
		@SuppressWarnings("rawtypes")
		public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
			// 在open方法初始化的时候会传入SpoutOutputCollector，用来发射数据出去的
			this.collector = collector;
			// 构造一个随机数生产对象
			this.random = new Random();
		}
		
		/**
		 * 
		 * spout类最终会运行在某个worker进程的某个executor线程内部的某个task中
		 * ask会负责去不断的无限循环调用nextTuple()方法，无限循环调用，可以不断发射最新的数据出去，形成一个数据流
		 */
		@Override
		public void nextTuple() {
			Utils.sleep(100); 
			String[] sentences = new String[]{"the cow jumped over the moon", "an apple a day keeps the doctor away",
					"four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature"};
			String sentence = sentences[random.nextInt(sentences.length)];
			LOGGER.info("【发射句子】sentence=" + sentence);  
			// values可以认为就是构建一个tuple
			// tuple是最小的数据单位，无限个tuple组成的流就是一个stream
			collector.emit(new Values(sentence)); 
		}

		/**
         *
		 *这个方法定义发射出去的每个tuple中的每个field的名称是什么
		 */
		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("sentence"));   
		}
		
	}

	/**
	 *
	 * Bolt类接收由Spout或者其他上游Bolt类发来的Tuple，并对其进行处理。Bolt组件的实现可以通过继承BasicRichBolt类或者IRichBolt接口来完成
	 * 每个bolt代码同样是发送到worker某个executor的task里面去运行
	 */
	public static class SplitSentence extends BaseRichBolt {

	    //OutputCollector是Bolt的这个tuple的发射器
		private OutputCollector collector;
		
		/**
         *
		 * 对于bolt来说，第一个执行方法就是prepare方法
		 */
		@Override
		public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
			this.collector = collector;
		}
		
		/**
		 * 
		 * 每次接收到一条数据后，就会交给这个executor方法来执行
		 */
        @Override
		public void execute(Tuple tuple) {
			String sentence = tuple.getStringByField("sentence");
			String[] words = sentence.split(" ");
			for(String word : words) {
				collector.emit(new Values(word)); 
			}
		}

		/**
		 * 定义发射出去的tuple，每个field的名称
		 */
        @Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));   
		}
		
	}
	
	public static class WordCount extends BaseRichBolt {
		
		private static final Logger LOGGER = LoggerFactory.getLogger(WordCount.class);

		private OutputCollector collector;
		private Map<String, Long> wordCounts = new HashMap<String, Long>();

        @Override
		public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
			this.collector = collector;
		}

        @Override
		public void execute(Tuple tuple) {
			String word = tuple.getStringByField("word");
			
			Long count = wordCounts.get(word);
			if(count == null) {
				count = 0L;
			}
			count++;
			
			wordCounts.put(word, count);
			
			LOGGER.info("【单词计数】" + word + "出现的次数是" + count);  
			
			collector.emit(new Values(word, count));
		}

        @Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));    
		}
		
	}
	
	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();
	
		// 第一个参数给这个spout设置一个名字
		// 第二个参数创建一个spout的对象
		// 第三个参数设置spout的executor有几个
		builder.setSpout("RandomSentence", new RandomSentenceSpout(), 2);
		builder.setBolt("SplitSentence", new SplitSentence(), 5)
                .setNumTasks(10)//默认task的数量=executor数量
				.shuffleGrouping("RandomSentence");
		// fieldsGrouping：相同的单词，从SplitSentence发射出来时，一定会进入到下游的指定的同一个task中
		// 只有这样子才能准确的统计出每个单词的数量
		// 比如你有个单词hello，下游task1接收到3个hello，task2接收到2个hello，共5个hello，全都进入一个task
		builder.setBolt("WordCount", new WordCount(),
                10)
				.setNumTasks(20)
				.fieldsGrouping("SplitSentence", new Fields("word"));  
		
		Config config = new Config();
	
		// 说明是在命令行执行，打算提交到storm集群上去
		if(args != null && args.length > 0) {
			config.setNumWorkers(3);  
			try {
				StormSubmitter.submitTopology(args[0], config, builder.createTopology());  
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			config.setMaxTaskParallelism(20);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("WordCountTopology", config, builder.createTopology());
			Utils.sleep(60000);
			cluster.shutdown();
		}
	}
	
}
