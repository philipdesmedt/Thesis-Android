package be.pds.collaborative;


public class TwitterComponent extends AndroidComponent {

	private SharedPreferences prefs;
	private final Handler mTwitterHandler = new Handler();


	public TwitterComponent(Activity a) {
		super(a);
		setupTwitter();
	}

	private void setupTwitter() { 

	    this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    sendTweet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authAndTweet();
			}
		});
	}

	final Runnable mUpdateTwitterNotification = new Runnable() {
		public void run() {
			Toast.makeText(this.parent.getBaseContext(), "Tweet sent !", Toast.LENGTH_LONG).show();
		}
	};

	@Override
	public void action(HashMap<String, Object> properties) {
		authAndTweet();
	}
		
	private void authAndTweet() {
		if (TwitterUtils.isAuthenticated(prefs)) {
			sendTweet();
		} else {
			Intent i = new Intent(this.parent.getApplicationContext(), PrepareRequestTokenActivity.class);
			i.putExtra("tweet_msg", tweetText.getText().toString());
			this.parent.startActivity(i);
		}
	}

	private void sendTweet() {
		sendTweet(tweetText.getText().toString());
	}

	@Override
	public void onMessage(String message) { }

	private void sendTweet(final String tweet) {
		Thread t = new Thread() {
			public void run() {
				try {
					TwitterUtils.sendTweet(prefs, tweet);
					mTwitterHandler.post(mUpdateTwitterNotification);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

}
