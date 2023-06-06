package com.example.autotrackerca400;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * <h1>Following Functionality;</h1>
 * <p>
 * - Sets up voice used throughout the app.
 *</p><p>
 * - Voice can speak based on message passed through.
 *</p>
 */

public class VoiceListener implements TextToSpeech.OnInitListener {
    public static TextToSpeech voiceSpeed;
    Activity activityContext;


    public VoiceListener(Activity MainActivity){
        this.activityContext = MainActivity;
        onInit(0);
    }

    public Locale GetLocale(String Language){
        Locale locale = null;
        if(Language == "German") {
            locale = Locale.GERMANY;
        }
        if(Language == "French"){
            locale = Locale.FRANCE;
        }
        if (Language == "English"){
            locale = Locale.ENGLISH;
        }
        if(Language == "Japanese"){
            locale = Locale.JAPANESE;
        }
        if(Language == "Chinese"){
            locale = Locale.CHINESE;
        }
        if(Language == "Italian"){
            locale = Locale.ITALIAN;
        }
        if(Language == "Korean"){
            locale = Locale.KOREAN;
        }
        return locale;
    }

    public void TestVoiceParameters(String Language,float Pitch, float SpeechRate){
        TextToSpeech tmpVoiceSpeed;
        tmpVoiceSpeed = voiceSpeed;
        Locale tmpLocale = GetLocale(Language);
        tmpVoiceSpeed.setLanguage(tmpLocale);
        tmpVoiceSpeed.setSpeechRate(SpeechRate);
        tmpVoiceSpeed.setPitch(Pitch);
        tmpVoiceSpeed.speak("This is a test",TextToSpeech.QUEUE_FLUSH, null);
    }

    public void SetVoiceParameters(String Language,float Pitch, float SpeechRate){
        voiceSpeed.setSpeechRate(SpeechRate);
        voiceSpeed.setPitch(Pitch);
        Locale setLocale = GetLocale(Language);
        voiceSpeed.setLanguage(setLocale);

    }

    public void onInit(int status) {
        // setup voice service
        voiceSpeed = new TextToSpeech(activityContext.getApplicationContext(), statusnew -> {
            if (statusnew == TextToSpeech.SUCCESS) {
                int result = voiceSpeed.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }

    public void voiceSpeak(String VoiceOutput){
        voiceSpeed.speak(VoiceOutput, TextToSpeech.QUEUE_FLUSH, null);
    }

}
