package com.Test;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.TargetDataLine;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;


public class Echo implements RecognizeCallback {
	
	class UserInputThread extends Thread{
		private boolean running;
		
		public void run(){ //tts함수
			BufferedReader br = null;
			try{
				running = true;
				br = new BufferedReader(new InputStreamReader(System.in));
				String line = null;
				while(running){
					line = br.readLine();
					if(line!=null){
						if("quit".equals(line))
							break;
						
						play(line);
					}
				};
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{br.close();}catch(Exception e){}
			}
		}
	}

	private static final String TTS_USERNAME = "92987f1a-8f65-4248-815d-30ce80e34724";
	private static final String TTS_PASSWD = "dQXb1tO8fufw";
	private static final String STT_USERNAME = "40056ebf-2945-4f36-b992-0bc5d6e43b59";
	private static final String STT_PASSWD = "Ua81uN0u103g";
	
	private TextToSpeech ttsService;
	private SpeechToText sttService;
	private boolean flagEcho;
	
	public Echo(boolean echo){
		this.flagEcho = echo;
		
		ttsService = new TextToSpeech();
		ttsService.setUsernameAndPassword(TTS_USERNAME, TTS_PASSWD);
		
		sttService = new SpeechToText();
		sttService.setUsernameAndPassword(STT_USERNAME, STT_PASSWD);
	}
	
	public void startEcho() throws Exception{ //stt함수 듣는기능
		UserInputThread user = new UserInputThread();
		user.start();
		
		int sampleRate = 16000;
		AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		
		if(!AudioSystem.isLineSupported(info)){
			System.out.println("Line not supported");
			System.exit(0);
		}
		
		TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
		line.open(format);
		line.start();
		
		AudioInputStream audio = new AudioInputStream(line);
		RecognizeOptions options = new RecognizeOptions.Builder()
				.continuous(true)
				.interimResults(true)
				.contentType(HttpMediaType.AUDIO_RAW + "; rate=" + sampleRate)
				.build();
		
		sttService.recognizeUsingWebSocket(audio, options, this);
		
		System.out.println("Listening to your voice for the next 30s....");
		Thread.sleep(30*1000);
		
		line.stop();
		line.close();
		
		user.running = false;
	}
	
	private void play(String text) throws Exception{ //tts 결과물 말하기
		InputStream in = ttsService.synthesize(text, Voice.EN_ALLISON,
				com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat.WAV).execute();
		
		AudioInputStream sound = AudioSystem.getAudioInputStream(WaveUtils.reWriteWaveHeader(in));
		
		DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
		Clip clip = (Clip) AudioSystem.getLine(info);
		clip.open(sound);
		System.out.println("text = " + text);
		if(text.equals("goodbye")){
			System.out.println("stop systems...");
			clip.stop();
		}else{
			clip.addLineListener(new LineListener(){
				public void update(LineEvent event){
					if(event.getType() == LineEvent.Type.STOP){
						event.getLine().close();
					}
				}
			});
			System.out.println("continue~");
			clip.start();
		}
		
		
	}
	
	
	@Override
	public void onConnected(){
		
	}
	
	@Override
	public void onDisconnected(){
		
	}
	
	@Override
	public void onError(Exception arg0){
		
	}
	
	@Override
	public void onInactivityTimeout(RuntimeException arg0){
		
	}
	
	@Override
	public void onListening(){
		
	}
	
	@Override
	public void onTranscription(SpeechResults speechResults){ //stt 결과물 출력
		List<Transcript> rs = speechResults.getResults();
		
		for(Transcript ts : rs){
			if(ts.isFinal()){
				String speech = ts.getAlternatives().get(0).getTranscript();
				System.out.println("@.@ STT Result >> " + speech);
				if(!flagEcho){
					try{
						System.out.println(speech);
						
						play("you said ");
						play(speech);	
						
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			Echo echo = new Echo(false);
			echo.startEcho();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
