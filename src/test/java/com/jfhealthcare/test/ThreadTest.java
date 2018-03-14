package com.jfhealthcare.test;

class Ticket2 implements Runnable{
	//定一个成员变量，用来记录当前的总票数
	private int num = 100;
	
	//定一个对象充当同步的锁对象
	//private Object lock = new Object();
	
	//售票的动作，就是多个线程同时要执行的动作。
	public void run(){
		
		//这里写死循环的目的是保证一旦有线程进来了，就可以无限的去售票
		while( num>0 ){
		//同步代码块
			//synchronized( lock ){
					System.out.println(Thread.currentThread().getName() + "........"+num);
					num--;
			//}
		}
	}
}
public class ThreadTest {
	public static void main(String[] args) {
		//创建线程要执行的任务
		Ticket2 task = new Ticket2();
		//创建线程对象
		Thread t = new Thread(task);
		/*Thread t2 = new Thread(task);
		Thread t3 = new Thread(task);
		Thread t4 = new Thread(task);*/
		//开启线程
		t.start();
		/*t2.start();
		t3.start();
		t4.start();*/
	}
}
