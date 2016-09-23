package com.example.brandon.airrater;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.Header;

import java.lang.ref.WeakReference;

public class AsyncDownload extends Object implements Runnable {


	private class Response {
		public Response(String r, Object l) {
			result = r;
			notes = l;
		}

		public String result;
		public Object notes;
	}

	public JSONBase type;
	
	public AsyncDownload(String u, JSONBase t) {
		this(u, null, false, t);
		this.returnAsBinary = true;
	}

	public AsyncDownload(String u, RequestParams p, JSONBase t) {
		this(u, p, true, t);
	}

	public AsyncDownload(String u, RequestParams p, Boolean notes, JSONBase t) {
		mainThread = new hHandler(this);

		url = u;
		params = p;
		returnAsNotes = notes;
		returnAsBinary = false;
		type = t;
	}

	protected void onPostExecute(String result, Object notes) {
	}

    //protected void onPostExecute(Bitmap result){

    //}

	protected void onPostExecute(byte[] binaryData, boolean success) {

	}

	private hHandler mainThread;

	static class hHandler extends Handler {
		private final WeakReference<AsyncDownload> mTarget;

		hHandler(AsyncDownload target) {
			mTarget = new WeakReference<AsyncDownload>(target);
		}

		@Override
		public void handleMessage(Message msg) {
			AsyncDownload target = mTarget.get();
			if (target.returnAsBinary) {
				if (msg.what == REQUEST_SUCCESS) {
					target.onPostExecute((byte[]) msg.obj, true);
				} else {
					target.onPostExecute((byte[]) msg.obj, true);
				}
			} else {
				if (msg.what == REQUEST_SUCCESS) {

					Response r = (Response) msg.obj;
					target.onPostExecute(r.result, r.notes);
				} else {
					if (msg.obj != null)
						Log.w("ccm", (String) msg.obj);
					target.onPostExecute(null, null);
				}
			}
		}
	}

	private static int REQUEST_SUCCESS = 1;
	private static int REQUEST_FAILED = 0;

	public String url;
	public RequestParams params;
	public Boolean returnAsNotes;
	public Boolean returnAsBinary;
	public boolean running = false;


	@Override
	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

		if (!running) {
			running = true;
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(1000 * 60);

			if (returnAsBinary) {
				client.get(url, new BinaryHttpResponseHandler() {

					@Override
					public String[] getAllowedContentTypes() {
						return new String[] { "image/jpeg", "image/png", "application/pdf" };
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
						mainThread.sendMessage(Message.obtain(mainThread, REQUEST_SUCCESS, binaryData));
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
						mainThread.sendMessage(Message.obtain(mainThread, REQUEST_FAILED, binaryData));

					}
				});
			} else {
				client.post(url, params, new TextHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, String responseString) {
						if (returnAsNotes) {
                            Object notes = type.arrayFromJSON(responseString);
							mainThread.sendMessage(Message.obtain(mainThread, REQUEST_SUCCESS, new Response(responseString, notes)));
						} else
							mainThread.sendMessage(Message.obtain(mainThread, REQUEST_SUCCESS, new Response(responseString, null)));

					}

					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						mainThread.sendMessage(Message.obtain(mainThread, REQUEST_FAILED, responseString));

					}

				});
			}
		}
	}

}
