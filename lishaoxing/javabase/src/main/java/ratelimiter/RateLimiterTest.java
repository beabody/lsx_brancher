package ratelimiter;

import org.junit.Test;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterTest {

	/**
	 * 从测试结果来看，桶的容量貌似是n两倍，可以理解为刚刚进桶和刚刚出桶之和
	 *
	 *
	 * @throws InterruptedException
	 *
	 * @author lishaoxing 2016年12月1日 上午11:38:29
	 * @since <版本号>
	 */
	@Test
	public void test1() throws InterruptedException {
		RateLimiter limiter = RateLimiter.create(1.0); // 每秒不超过n个任务被提交
		System.out.println(limiter.acquire(5));
		Thread.sleep(10000L);
		for (int i = 0; i < 10; i++) {
			System.out.println(limiter.acquire(5));
		}
	}
}
