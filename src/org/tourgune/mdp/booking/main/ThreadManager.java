package org.tourgune.mdp.booking.main;

public class ThreadManager {

	private int threadCount = 0;
	BookingCrawlerThread[] threads = null;
	
	public ThreadManager(int threadCount)
	{
		this.threadCount = threadCount;
		threads = new BookingCrawlerThread[threadCount];
		for(int i=0;i<this.threadCount;i++)
		{
			threads[i] = new BookingCrawlerThread(i);
		}
	}
	
	public BookingCrawlerThread getThread()
	{
		boolean threadSelected = false;
		BookingCrawlerThread returnThread = null;
		do
		{
			threadSelected = false;
			for(int i=0;i<threads.length && !threadSelected;i++)
			{
				if(threads[i].isAvailable() && !threads[i].isDie())
				{
					threads[i].setAvailable(false);
					returnThread = threads[i];
					threadSelected = true;
				}
			}
			if(!threadSelected)
			{
				try
				{
					Thread.sleep(10);
				}
				catch(Exception ex){}
			}
		}
		while(!threadSelected);
		
		return returnThread;
	}
	public void killThreads()
	{
		boolean allThreadsAreFree = false;
		
		while(!allThreadsAreFree)
		{
			allThreadsAreFree = true;
			for(int i = 0; i < threads.length; i++)
			{
				allThreadsAreFree &= threads[i].isAvailable();
			}
		}
		
		for(int i=0;i<threads.length;i++)
		{
			if(!threads[i].isDie()){
				threads[i].setDie(true);
				try{
					threads[i].join();
				}catch(Exception ex){}
			}
		}
	}
}
