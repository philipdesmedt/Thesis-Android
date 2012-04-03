package be.pds.collaborative;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import be.pds.thesis.ServerConnection;

public class DropboxItemActivity extends Activity {

	private final String TAG = getClass().getName();
	private String file;
	private long dropboxId;
	private Button downloadButton;
	private Button addNoteButton;
	private EditText noteInput;
	private TextView noteOutput;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dropboxitem); 
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	TextView filename = (TextView) findViewById(R.id.filename);
        	file = extras.getString("file");
        	filename.setText(file);
        	ServerConnection.getInstance().getDropboxNotes(file);
        	dropboxId = extras.getLong("uid");
        }
        
        noteInput = (EditText) findViewById(R.id.noteInput);
        noteOutput = (TextView) findViewById(R.id.noteOutput);
        downloadButton = (Button) findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// http://dl.dropbox.com/u/<your users uid>/<path under /Public>/filename
				try {
					URL url = new URL("http://dl.dropbox.com/u/" + dropboxId + "/Thesis/" + file);
					try {
						URLConnection ucon = url.openConnection();
						InputStream is = ucon.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is);
						
					} catch (IOException e) { }
				} catch (MalformedURLException e) { }
			}
        });
        
        addNoteButton = (Button) findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String note = noteInput.getText().toString();
				String user = ServerConnection.getInstance().getUser();
				ServerConnection.getInstance().addDropboxNote(file, note);
				
				addNote(user, note);
			}
		});
        ServerConnection.getInstance().addComponent("dropbox", this);
    }
    
    public void addNote(final String user, final String note) {
    	runOnUiThread(new Runnable() {
			public void run() {
				String output = noteOutput.getText().toString();
				output += user + ": " + note + "\n";
				noteOutput.setText(output);
			}
		});
    }
}
