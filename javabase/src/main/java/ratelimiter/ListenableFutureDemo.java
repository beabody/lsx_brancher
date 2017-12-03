package ratelimiter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.RateLimiter;

/**
 * http://jinnianshilongnian.iteye.com/blog/2305117
 * https://my.oschina.net/cloudcoder/blog/359598
 * 
 * 〈功能详细描述〉
 * 
 * @author lishaoxing 2016年12月1日 上午10:49:49
 * @since <版本号>
 */
public class ListenableFutureDemo {

	public static void main(String[] args) throws InterruptedException {
		//testRateLimiter();
		 testListenableFuture();
	}

	/**
	 * RateLimiter类似于JDK的信号量Semphore，他用来限制对资源并发访问的线程数
	 * 
	 * @throws InterruptedException
	 */
	public static void testRateLimiter() throws InterruptedException {
		ListeningExecutorService executorService = MoreExecutors
				.listeningDecorator(Executors.newCachedThreadPool());

		RateLimiter limiter = RateLimiter.create(1.0); // 每秒不超过4个任务被提交

		for (int i = 0; i < 10; i++) {
			double temp = limiter.acquire(); // 请求RateLimiter,
			System.out.println(temp); // 超过permits会被阻塞
			if (temp == 0) {
				final ListenableFuture<Integer> listenableFuture = executorService
						.submit(new Task("is " + i));
			} else {
				long sleepTime = (long) (temp * 1000L);
				System.out.println("sleep time==" + sleepTime);
				Thread.sleep(sleepTime);
				final ListenableFuture<Integer> listenableFuture = executorService
						.submit(new Task("is " + i));
			}

		}

	}

	public static void testListenableFuture() {
		ListeningExecutorService executorService = MoreExecutors
				.listeningDecorator(Executors.newCachedThreadPool());

		final ListenableFuture<Integer> listenableFuture = executorService
				.submit(new Task("testListenableFuture"));

		// 同步获取调用结果
		try {
			System.out.println(listenableFuture.get());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}

		// 第一种方式
		listenableFuture.addListener(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("get listenable future's result "
							+ listenableFuture.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}, executorService);

		// 第二种方式
		Futures.addCallback(listenableFuture, new FutureCallback<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				System.out
						.println("get listenable future's result with callback "
								+ result);
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}
}

class Task implements Callable<Integer> {
	String str;

	public Task(String str) {
		this.str = str;
	}

	@Override
	public Integer call() throws Exception {
		System.out.println("call execute.." + str);
		TimeUnit.SECONDS.sleep(100);
		return 7;
	}
}
