package eu.baltrad.dex.net.util;

import static org.easymock.EasyMock.expect;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FramePublisherTest extends EasyMockSupport {
  
  private static final int defaultQueueSize = 1;
  private static final int defaultPoolSize = 2;
  private static final int maxPoolSize = 8;
  private FramePublisher framePublisher;
  private ThreadPoolExecutor mockedExecutor;
  private BlockingQueue<Runnable> mockedQueue;
  
  
  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    framePublisher = new FramePublisher(defaultQueueSize, defaultPoolSize, maxPoolSize);
    mockedExecutor = createMock(ThreadPoolExecutor.class);
    framePublisher.setThreadPoolExecutor(mockedExecutor);
    mockedQueue = createMock(BlockingQueue.class);
  }
  
  @After
  public void tearDown() {
    
  }

  @Test
  public void testOneTask() throws InterruptedException {
    
    Runnable task = createMock(Runnable.class);
    
    expect(mockedExecutor.getCorePoolSize()).andReturn(defaultPoolSize);
    expect(mockedExecutor.getQueue()).andReturn(mockedQueue);
    expect(mockedQueue.size()).andReturn(0);
    expect(mockedExecutor.getActiveCount()).andReturn(0);
    mockedExecutor.execute(task);

    replayAll();
    
    framePublisher.addTask(task);

    verifyAll();
  }
  
  @Test
  public void testTwoActiveTasks() throws InterruptedException {
    
    Runnable task = createMock(Runnable.class);
    
    expect(mockedExecutor.getCorePoolSize()).andReturn(defaultPoolSize);
    expect(mockedExecutor.getQueue()).andReturn(mockedQueue);
    expect(mockedQueue.size()).andReturn(0);
    expect(mockedExecutor.getActiveCount()).andReturn(1); // simulate one already running
    mockedExecutor.execute(task);

    replayAll();
    
    framePublisher.addTask(task);

    verifyAll();
  }
  
  @Test
  public void testIncreasePoolSize_defaultPoolSize() throws InterruptedException {
    int poolSize = defaultPoolSize;
    doChangingPoolSizeTest(poolSize, 1, 1, poolSize * 2); // a doubling of the pool size is expected    
  }
  
  @Test
  public void testIncreasePoolSize_poolSize4() throws InterruptedException {
    int poolSize = 4;
    doChangingPoolSizeTest(poolSize, 2, 2, poolSize * 2); // a doubling of the pool size is expected    
  }
  
  @Test
  public void testIncreasePoolSize_poolSize5() throws InterruptedException {
    int poolSize = 5;
    doChangingPoolSizeTest(poolSize, 3, 1, maxPoolSize); // since max pool size is smaller than pool size double, the max size limits    
  }
  
  @Test
  public void testNotIncreasingPoolSize_defaultPoolSize() throws InterruptedException {
    int poolSize = defaultPoolSize;
    // since activeThreads is larger than noOfQueued, no increase will occur
    doChangingPoolSizeTest(poolSize, 4, 5, poolSize);  
  }
  
  @Test
  public void testNotIncreasingPoolSize_maxSizeReached() throws InterruptedException {
    int poolSize = maxPoolSize;
    // since max pool size is reached, no increase will occur
    doChangingPoolSizeTest(poolSize, 10, 10, poolSize);  
  }
  
  @Test
  public void testDecreasePoolSize_poolSize8() throws InterruptedException {
    int poolSize = 8;
    // when no of queued is 0, the pool size is decreased by 50%
    doChangingPoolSizeTest(poolSize, 0, 2, poolSize / 2);   
  }
  
  @Test
  public void testDecreasePoolSize_poolSize4() throws InterruptedException {
    int poolSize = 4;
    // when no of queued is 0, the pool size is decreased by 50%
    doChangingPoolSizeTest(poolSize, 0, 0, poolSize / 2);
  }
  
  @Test
  public void testDecreasePoolSize_poolSize5() throws InterruptedException {
    int poolSize = 5;
    // when no of queued is 0, the pool size is decreased by 50%
    doChangingPoolSizeTest(poolSize, 0, 1, Math.floorDiv(poolSize, 2));  
  }
  
  @Test
  public void testNotDecreasingPoolSize_minSizeReached() throws InterruptedException {
    int poolSize = defaultPoolSize;
    // since min pool size is reached, no decrease will occur
    doChangingPoolSizeTest(poolSize, 0, 0, poolSize);  
  }
  
  private void doChangingPoolSizeTest(int currentPoolSize, int noOfQueued, int activeThreads, int newPoolSize) {
    Runnable task = createMock(Runnable.class);
    
    expect(mockedExecutor.getCorePoolSize()).andReturn(currentPoolSize);
    expect(mockedExecutor.getQueue()).andReturn(mockedQueue);
    expect(mockedQueue.size()).andReturn(noOfQueued);
    expect(mockedExecutor.getActiveCount()).andReturn(activeThreads).anyTimes(); // simulate one already running
    
    if (newPoolSize != currentPoolSize) {
      mockedExecutor.setCorePoolSize(newPoolSize);      
    }
    
    mockedExecutor.execute(task);

    replayAll();
    
    framePublisher.addTask(task);

    verifyAll();
  }


}
