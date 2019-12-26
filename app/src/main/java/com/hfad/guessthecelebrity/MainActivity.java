package com.hfad.guessthecelebrity;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    ArrayList<String> celebURLS = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();

    Pattern pattern;
    Matcher matcher;
    int chosenCelebrity =0;
    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void chooseAnswer(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Wrong! It was "+celebNames.get(chosenCelebrity), Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public void newQuestion(){
        try {
            Random random = new Random();
            chosenCelebrity = random.nextInt(celebURLS.size());
            ImageDownloader imageDownloader = new ImageDownloader();
            Bitmap celebImage = imageDownloader.execute(celebURLS.get(chosenCelebrity)).get();
            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCelebrity);
                } else {
                    incorrectAnswerLocation = random.nextInt(celebURLS.size());
                    while (incorrectAnswerLocation == chosenCelebrity) {
                        incorrectAnswerLocation = random.nextInt(celebURLS.size());
                    }

                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"listedArticles\">");
            pattern = Pattern.compile("img src=\"(.*?)\"");
            matcher = pattern.matcher(splitResult[0]);
            while (matcher.find()){
                celebURLS.add(matcher.group(1));
            }
            pattern = Pattern.compile("alt=\"(.*?)\"");
            matcher = pattern.matcher(splitResult[0]);
            while (matcher.find()){
                celebNames.add(matcher.group(1));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        newQuestion();
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}


