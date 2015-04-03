package com.light3moon.concurrenttest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.RadioGroup;

public class MainActivity extends Activity implements View.OnClickListener, 
	RadioGroup.OnCheckedChangeListener {
	
	private final static int THREAD_SIZE = 1000;
	private final static int LOOP_COUNT = 1000;
	
	private RadioGroup mRadioGroup;
	private Button mBtnStart;
	private TextView mTvResult;
	
	private int mCount = 0;
	private volatile int mCountVolatile = 0;
	private AtomicInteger mCountAtomic = new AtomicInteger();
	private Object mCountLock = new Object();
	
	private int mConcurrentMode = R.id.op_noting;
	private int mExecutorCount = 0;
	private ExecutorService mRunService;
	
	private long mCostTime = 0;
	private Handler mUIHandler = new H();
	
	// not any thread concurrent detail
	class Counter implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < LOOP_COUNT; i++) {
				mCount += 1;
			}
			// tell main thread, we are done
			mUIHandler.sendEmptyMessage(100);
		}
	}
	
	// thread concurrent with synchronized
	class CounterLocked implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < LOOP_COUNT; i++) {
				synchronized(mCountLock) {
					mCount += 1;
				}
			}
			// tell main thread, we are done
			mUIHandler.sendEmptyMessage(100);
		}
	}
	
	// thread concurrent with volatile
	class CounterVolatile implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < LOOP_COUNT; i++) {
				mCountVolatile += 1;
			}
			// tell main thread, we are done
			mUIHandler.sendEmptyMessage(100);
		}
	}
	
	// thread concurrent with synchronized and volatile
	class CounterVolatileLocked implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < LOOP_COUNT; i++) {
				mCountVolatile += 1;
			}
			// tell main thread, we are done
			mUIHandler.sendEmptyMessage(100);
		}
	}
	
	// thread concurrent with Atomic
	class CounterAtomic implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < LOOP_COUNT; i++) {
				mCountAtomic.incrementAndGet();
			}
			// tell main thread, we are done
			mUIHandler.sendEmptyMessage(100);
		}
	}
	
	class H extends Handler {
		@Override
		public void handleMessage(Message msg) {
			mExecutorCount += 1;
			if (mExecutorCount >= THREAD_SIZE) {
				completeConcurrentTest();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mRunService = Executors.newFixedThreadPool(THREAD_SIZE);
		
		mRadioGroup = (RadioGroup) findViewById(R.id.op_group);
		mBtnStart = (Button) findViewById(R.id.btn_start);
		mTvResult = (TextView) findViewById(R.id.tv_result);
		
		mRadioGroup.setOnCheckedChangeListener(this);
		mBtnStart.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		shutdownThreadExecutors();
	}
	
	@Override
	public void onClick(View view) {
		if (view.equals(mBtnStart)) {
			startConcurrentTest();
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		mConcurrentMode = checkedId;
	}
	
	private void startConcurrentTest() {
		mCount = 0;
		mCountVolatile = 0;
		mCountAtomic.set(0);
		
		mExecutorCount = 0;
		mBtnStart.setEnabled(false);
		
		mCostTime = System.currentTimeMillis();
		mTvResult.setText("concurrent test start ...");
		
		for (int i = 0; i < THREAD_SIZE; i++) {
			// if you are performance consider, you should write the if outside the for loop
			// but now we don't care this ... ...
			switch (mConcurrentMode) {
			case R.id.op_sync:
				mRunService.execute(new CounterLocked());
				break;
			case R.id.op_vol:
				mRunService.execute(new CounterVolatile());
				break;
			case R.id.op_sync_vol:
				mRunService.execute(new CounterVolatileLocked());
				break;	
			case R.id.op_atomic:
				mRunService.execute(new CounterAtomic());
				break;
			case R.id.op_noting:
			default:
				mRunService.execute(new Counter());
				break;
			}
		}
	}
	
	private void completeConcurrentTest() {
		mCostTime = System.currentTimeMillis() - mCostTime;
		int result = 0;
		String mode = null;
		switch (mConcurrentMode) {
		case R.id.op_sync:
			result = mCount;
			mode = "synchronized";
			break;
		case R.id.op_vol:
			result = mCountVolatile;
			mode = "volatile";
			break;
		case R.id.op_sync_vol:
			result = mCountVolatile;
			mode = "synchronized and volatile";
			break;
		case R.id.op_atomic:
			result = mCountAtomic.get();
			mode = "atomic";
			break;
		case R.id.op_noting:
		default:
			result = mCount;
			mode = "noting";
			break;
		}
		mTvResult.setText(String.format("count=%d, cost time=%d ms with mode=%s", 
				result, mCostTime, mode));
		
		mExecutorCount = 0;
		mBtnStart.setEnabled(true);
	}
	
	private void shutdownThreadExecutors() {
		mRunService.shutdown();
		try {
			mRunService.awaitTermination(5000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

